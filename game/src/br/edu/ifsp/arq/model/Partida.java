package br.edu.ifsp.arq.model;

import br.edu.ifsp.arq.dao.QuestaoDAO;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Orquestra uma sessão completa do jogo MentesMáticas como uma thread.
 * Esta classe gerencia a configuração dos jogadores, o ciclo de vida da partida,
 * a lógica de turnos e a comunicação em rede de forma segura e concorrente.
 */
public class Partida implements Runnable {

    private List<Questao> questoes;
    private final List<ClientConnection> connections;
    private final FilaDeRespostas filaDeRespostas;
    private volatile boolean partidaEmAndamento;

    // Classe interna para agrupar tudo relacionado a um jogador
    private static class ClientConnection {
        final ObjectInputStream entrada;
        final ObjectOutputStream saida;
        Jogador jogador; // Será definido após o jogador digitar o nome

        ClientConnection(ObjectInputStream in, ObjectOutputStream out) {
            this.entrada = in;
            this.saida = out;
        }
    }

    public Partida(ObjectInputStream entrada1, ObjectOutputStream saida1, ObjectInputStream entrada2, ObjectOutputStream saida2) {
        this.questoes = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.filaDeRespostas = new FilaDeRespostas();
        
        // Adiciona as duas conexões à lista
        this.connections.add(new ClientConnection(entrada1, saida1));
        this.connections.add(new ClientConnection(entrada2, saida2));
    }

    @Override
    public void run() {
        try {
            this.partidaEmAndamento = true;

            // Fase 1: Configurar jogadores (obter nomes)
            configurarJogadores();
            
            // Fase 2: Ordenar jogadores e iniciar threads "Ouvintes"
            connections.sort(Comparator.comparing(c -> c.jogador.getNome(), String.CASE_INSENSITIVE_ORDER));
            for (ClientConnection conn : connections) {
                iniciarOuvinte(conn);
            }
            
            // Fase 3: Iniciar e executar a lógica principal do jogo
            jogar();

        } catch (Exception e) {
            System.err.println("Partida encerrada por erro crítico: " + e.getMessage());
            e.printStackTrace();
        } finally {
            this.partidaEmAndamento = false;
            encerrarConexoes();
        }
    }
    
    private void configurarJogadores() throws IOException, ClassNotFoundException {
        for (ClientConnection conn : connections) {
            conn.saida.writeObject("Bem-vindo! Por favor, digite seu nome: ");
            String nome = (String) conn.entrada.readObject();
            conn.jogador = new Jogador(nome);
            System.out.println("Jogador '" + nome + "' configurado.");
        }
    }

    private void iniciarOuvinte(ClientConnection conn) {
        new Thread(() -> {
            while (partidaEmAndamento) {
                try {
                    String textoResposta = (String) conn.entrada.readObject();
                    filaDeRespostas.adicionar(new RespostaJogador(conn.jogador, textoResposta));
                } catch (IOException | ClassNotFoundException e) {
                    break; 
                }
            }
        }).start();
    }

    private void jogar() throws IOException, InterruptedException {
        transmitirParaTodos("Todos os jogadores estão conectados e ordenados.");
        transmitirParaTodos("A partida vai começar!");
        carregarQuestoes();

        for (Questao questao : questoes) {
            transmitirParaTodos("\n----- NOVA RODADA -----");
            transmitirParaTodos("Pergunta: " + questao.getEnunciado());
            for (int i = 0; i < questao.getOpcoes().length; i++) {
                transmitirParaTodos((char) ('a' + i) + ") " + questao.getOpcoes()[i]);
            }
            
            filaDeRespostas.limpar();

            for (ClientConnection connDaVez : connections) {
                Jogador jogadorDaVez = connDaVez.jogador;
                
                enviarMensagemParaJogador(jogadorDaVez, "PROMPT|Sua vez, " + jogadorDaVez.getNome() + "! Resposta: ");
                for (ClientConnection outraConn : connections) {
                    if (!outraConn.jogador.equals(jogadorDaVez)) {
                        enviarMensagemParaJogador(outraConn.jogador, "Aguarde a vez de " + jogadorDaVez.getNome() + "...");
                    }
                }

                RespostaJogador respostaRecebida = null;
                while (partidaEmAndamento) {
                    RespostaJogador r = filaDeRespostas.remover();
                    if (r.jogador.equals(jogadorDaVez)) {
                        respostaRecebida = r;
                        break; 
                    } else {
                        enviarMensagemParaJogador(r.jogador, "Aviso: Não é a sua vez de responder.");
                    }
                }
                
                if (respostaRecebida != null) {
                     char respostaChar = respostaRecebida.textoResposta.toLowerCase().charAt(0);
                     int respostaIndex = respostaChar - 'a';
                     if (questao.isRespostaCorreta(respostaIndex)) {
                         jogadorDaVez.adicionarPonto();
                         transmitirParaTodos(jogadorDaVez.getNome() + " acertou!");
                     } else {
                         transmitirParaTodos(jogadorDaVez.getNome() + " errou.");
                     }
                }
            }
            exibirPlacarGeral();
            Thread.sleep(4000); 
        }
        
        anunciarVencedorFinal();
    }

    private void exibirPlacarGeral() throws IOException {
        transmitirParaTodos("\n--- PLACAR ---");
        for (ClientConnection conn : connections) {
            transmitirParaTodos(conn.jogador.getNome() + ": " + conn.jogador.getPontuacao() + " ponto(s)");
        }
        transmitirParaTodos("--------------");
    }
    
    private void anunciarVencedorFinal() throws IOException {
        int maxPontos = -1;
        List<Jogador> jogadores = new ArrayList<>();
        for (ClientConnection conn : connections) {
            jogadores.add(conn.jogador);
            maxPontos = Math.max(maxPontos, conn.jogador.getPontuacao());
        }
        
        List<Jogador> vencedores = new ArrayList<>();
        for (Jogador j : jogadores) { if (j.getPontuacao() == maxPontos) { vencedores.add(j); } }
        
        transmitirParaTodos("\n----- FIM DE JOGO -----");
        if (vencedores.size() > 1) {
            transmitirParaTodos("Houve um empate entre os finalistas com " + maxPontos + " pontos!");
        } else if (!vencedores.isEmpty()){
            transmitirParaTodos("O grande vencedor é: " + vencedores.get(0).getNome() + " com " + maxPontos + " pontos!");
        }
    }

    private void enviarMensagemParaJogador(Jogador jogador, String msg) throws IOException {
        for(ClientConnection conn : connections) {
            if(conn.jogador != null && conn.jogador.equals(jogador)) {
                conn.saida.writeObject(msg);
                conn.saida.flush();
                return;
            }
        }
    }

    private void transmitirParaTodos(String msg) throws IOException {
        for (ClientConnection conn : connections) {
            conn.saida.writeObject(msg);
            conn.saida.flush();
        }
    }
    
    private void carregarQuestoes() {
        QuestaoDAO dao = new QuestaoDAO();
        this.questoes = dao.carregarQuestoesDoXML("questoes.xml");
    }
    
    private void encerrarConexoes() {
        partidaEmAndamento = false;
        for(ClientConnection conn : connections) {
            try { conn.saida.close(); } catch (IOException e) {}
            try { conn.entrada.close(); } catch (IOException e) {}
        }
        System.out.println("Partida finalizada e conexões encerradas.");
    }
}