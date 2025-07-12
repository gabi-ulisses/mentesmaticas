package br.edu.ifsp.arq.model;

import br.edu.ifsp.arq.dao.QuestaoDAO;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Orquestra uma sessão completa do jogo MentesMáticas como uma thread.
 * Gerencia o ciclo de vida da partida, a lógica de turnos, o reinício
 * e a comunicação em rede, incluindo o temporizador visível para os jogadores.
 */
public class Partida implements Runnable {
    
    private static final int TEMPO_POR_RODADA_SEGUNDOS = 60;

    private List<Questao> questoes;
    private final List<ClientConnection> connections;
    private volatile boolean sessaoAtiva;
    private volatile boolean partidaEmAndamento;
    private Timer timerGeral; 

    private final BlockingQueue<RespostaJogador> filaDeRespostas = new LinkedBlockingQueue<>();
    private final BlockingQueue<ComandoJogador> filaDeComandos = new LinkedBlockingQueue<>();

    // Classe interna para agrupar tudo relacionado a um jogador
    private static class ClientConnection {
        final ObjectInputStream entrada;
        final ObjectOutputStream saida;
        Jogador jogador;

        ClientConnection(ObjectInputStream in, ObjectOutputStream out) {
            this.entrada = in;
            this.saida = out;
        }
    }
    
    // Classe interna para comandos (ex: reiniciar partida)
    private static class ComandoJogador {
        final Jogador jogador;
        final String comando;

        ComandoJogador(Jogador jogador, String comando) {
            this.jogador = jogador;
            this.comando = comando;
        }
    }


    public Partida(ObjectInputStream entrada1, ObjectOutputStream saida1, ObjectInputStream entrada2, ObjectOutputStream saida2) {
        this.questoes = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.connections.add(new ClientConnection(entrada1, saida1));
        this.connections.add(new ClientConnection(entrada2, saida2));
    }

    @Override
    public void run() {
        try {
            this.sessaoAtiva = true;
            configurarJogadores();
            connections.sort(Comparator.comparing(c -> c.jogador.getNome(), String.CASE_INSENSITIVE_ORDER));

            for (ClientConnection conn : connections) {
                iniciarOuvinte(conn);
            }
            
            boolean jogarNovamente = true;
            while(jogarNovamente && sessaoAtiva) {
                partidaEmAndamento = true;
                jogar();
                partidaEmAndamento = false;
                
                if (sessaoAtiva) {
                    jogarNovamente = perguntarSeQuerJogarNovamente();
                    if(jogarNovamente) {
                        resetarPartida();
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Sessão encerrada por erro crítico: " + e.getMessage());
        } finally {
            this.sessaoAtiva = false;
            encerrarConexoes();
        }
    }
    
    private void configurarJogadores() throws IOException, ClassNotFoundException {
        // Lógica para configurar jogadores (sem alterações)
        for (ClientConnection conn : connections) {
            conn.saida.writeObject("Bem-vindo! Por favor, digite seu nome: ");
            String nome = (String) conn.entrada.readObject();
            conn.jogador = new Jogador(nome);
            System.out.println("Jogador '" + nome + "' configurado.");
        }
    }

    private void iniciarOuvinte(ClientConnection conn) {
        // Lógica do ouvinte (sem alterações)
        new Thread(() -> {
            while (sessaoAtiva) {
                try {
                    String mensagem = (String) conn.entrada.readObject();
                    if (mensagem.startsWith("CMD|")) {
                        filaDeComandos.put(new ComandoJogador(conn.jogador, mensagem.substring(4)));
                    } else if (partidaEmAndamento) {
                        filaDeRespostas.put(new RespostaJogador(conn.jogador, mensagem));
                    }
                } catch (IOException | ClassNotFoundException | InterruptedException e) {
                    System.out.println("Ouvinte para " + (conn.jogador != null ? conn.jogador.getNome() : "desconhecido") + " encerrado. A sessão terminará.");
                    sessaoAtiva = false; 
                }
            }
        }).start();
    }

    private void jogar() throws IOException, InterruptedException {
        transmitirParaTodos("Todos os jogadores estão conectados e ordenados.");
        transmitirParaTodos("A partida vai começar!");
        carregarQuestoes();

        for (Questao questao : questoes) {
            if(!sessaoAtiva) break;
            
            transmitirParaTodos("\n----- NOVA RODADA -----");
            transmitirParaTodos("Pergunta: " + questao.getEnunciado());
            for (int i = 0; i < questao.getOpcoes().length; i++) {
                transmitirParaTodos((char) ('a' + i) + ") " + questao.getOpcoes()[i]);
            }
            
            filaDeRespostas.clear();

            for (ClientConnection connDaVez : connections) {
                if(!sessaoAtiva) break;
                
                Jogador jogadorDaVez = connDaVez.jogador;
                
                enviarMensagemParaJogador(jogadorDaVez, "PROMPT|Sua vez, " + jogadorDaVez.getNome() + "! Resposta: ");
                for (ClientConnection outraConn : connections) {
                    if (!outraConn.jogador.equals(jogadorDaVez)) {
                        enviarMensagemParaJogador(outraConn.jogador, "Aguarde a vez de " + jogadorDaVez.getNome() + "...");
                    }
                }
                
                // Inicia o timer da rodada
                iniciarTimerRodada(jogadorDaVez);
                
                RespostaJogador respostaRecebida = null;
                // Espera por uma resposta por no máximo o tempo da rodada + 2 segundos de margem
                while (partidaEmAndamento) {
                    RespostaJogador r = filaDeRespostas.poll(TEMPO_POR_RODADA_SEGUNDOS + 2, TimeUnit.SECONDS);

                    if (r == null) { // Timeout ou a partida acabou
                        break; 
                    }
                    
                    if (r.jogador.equals(jogadorDaVez)) {
                        respostaRecebida = r;
                        if (timerGeral != null) timerGeral.cancel(); // Para o timer assim que a resposta certa chega
                        break; 
                    } else {
                        enviarMensagemParaJogador(r.jogador, "Aviso: Não é a sua vez de responder.");
                    }
                }
                
                if (timerGeral != null) timerGeral.cancel();

                if (respostaRecebida != null && !respostaRecebida.textoResposta.isEmpty()) {
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
        
        if(sessaoAtiva) {
            anunciarVencedorFinal();
        }
    }
    
    private void iniciarTimerRodada(Jogador jogadorDaVez) {
        if(timerGeral != null) timerGeral.cancel();
        timerGeral = new Timer();
        
        // Usamos um array de 1 elemento para que possa ser modificado dentro da classe anônima
        final int[] tempoRestante = {TEMPO_POR_RODADA_SEGUNDOS};

        timerGeral.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (tempoRestante[0] > 0) {
                        transmitirParaTodos("TIMER|" + tempoRestante[0]);
                        tempoRestante[0]--;
                    } else {
                        // Tempo esgotado
                        transmitirParaTodos("TIMER|0");
                        enviarMensagemParaJogador(jogadorDaVez, "Tempo esgotado! Você não respondeu a tempo.");
                        transmitirParaTodos(jogadorDaVez.getNome() + " não respondeu a tempo.");
                        filaDeRespostas.offer(new RespostaJogador(jogadorDaVez, ""));
                        cancel(); // Para este TimerTask
                    }
                } catch(IOException e) {
                    System.err.println("Erro ao transmitir tempo: " + e.getMessage());
                    cancel();
                }
            }
        }, 0, 1000); // Inicia imediatamente, repete a cada 1 segundo
    }

    private boolean perguntarSeQuerJogarNovamente() throws IOException, InterruptedException {
        // Lógica para reiniciar (sem alterações)
        transmitirParaTodos("CONTROL|PLAY_AGAIN");
        
        int respostasContadas = 0;
        int votosSim = 0;
        filaDeComandos.clear();
        
        while(sessaoAtiva && respostasContadas < connections.size()) {
            ComandoJogador cmd = filaDeComandos.poll(30, TimeUnit.SECONDS);
            
            if (cmd == null) { 
                System.out.println("Timeout esperando resposta para reiniciar. Encerrando.");
                return false;
            }
            
            if(cmd.comando.equals("RESTART_YES")) {
                votosSim++;
            }
            respostasContadas++;
        }
        
        return votosSim == connections.size();
    }
    
    private void resetarPartida() throws IOException {
        // Lógica de reset (sem alterações)
        for(ClientConnection conn : connections) {
            conn.jogador.resetarPontuacao();
        }
        filaDeRespostas.clear();
        filaDeComandos.clear();
        transmitirParaTodos("\n--- REINICIANDO PARTIDA ---");
    }

    private void exibirPlacarGeral() throws IOException {
        // Lógica do placar (sem alterações)
        transmitirParaTodos("\n--- PLACAR ---");
        for (ClientConnection conn : connections) {
            transmitirParaTodos(conn.jogador.getNome() + ": " + conn.jogador.getPontuacao() + " ponto(s)");
        }
        transmitirParaTodos("--------------");
    }
    
    private void anunciarVencedorFinal() throws IOException {
        // Lógica do vencedor (sem alterações)
        int maxPontos = -1;
        for (ClientConnection conn : connections) {
            maxPontos = Math.max(maxPontos, conn.jogador.getPontuacao());
        }
        
        List<Jogador> vencedores = new ArrayList<>();
        for (ClientConnection conn : connections) {
            if (conn.jogador.getPontuacao() == maxPontos) {
                vencedores.add(conn.jogador);
            }
        }
        
        transmitirParaTodos("\n----- FIM DE JOGO -----");
        if (vencedores.size() > 1) {
            transmitirParaTodos("Houve um empate entre os finalistas com " + maxPontos + " pontos!");
        } else if (!vencedores.isEmpty()){
            transmitirParaTodos("O grande vencedor é: " + vencedores.get(0).getNome() + " com " + maxPontos + " pontos!");
        } else {
            transmitirParaTodos("Fim de jogo. Ninguém pontuou.");
        }
    }

    private void enviarMensagemParaJogador(Jogador jogador, String msg) throws IOException {
        // Enviar mensagem (sem alterações)
        for(ClientConnection conn : connections) {
            if(conn.jogador != null && conn.jogador.equals(jogador)) {
                conn.saida.writeObject(msg);
                conn.saida.flush();
                return;
            }
        }
    }

    private void transmitirParaTodos(String msg) throws IOException {
        // Transmitir para todos (sem alterações)
        if(!sessaoAtiva) return;
        for (ClientConnection conn : connections) {
            try {
                conn.saida.writeObject(msg);
                conn.saida.flush();
            } catch (IOException e) {
                System.out.println("Falha ao transmitir para " + (conn.jogador != null ? conn.jogador.getNome() : "desconhecido") + ". Removendo.");
                sessaoAtiva = false; // Se uma escrita falha, a conexão provavelmente caiu.
            }
        }
    }
    
    private void carregarQuestoes() {
        QuestaoDAO dao = new QuestaoDAO();
        this.questoes = dao.carregarQuestoesDoXML("questoes.xml");
        Collections.shuffle(this.questoes); // Embaralha as questões a cada nova partida
    }
    
    private void encerrarConexoes() {
        if(timerGeral != null) timerGeral.cancel();
        
        try { transmitirParaTodos("CONTROL|SESSION_END"); } catch (IOException e) {}
        
        for(ClientConnection conn : connections) {
            try { conn.saida.close(); } catch (IOException e) {}
            try { conn.entrada.close(); } catch (IOException e) {}
        }
        System.out.println("Sessão finalizada e conexões encerradas.");
    }
}
