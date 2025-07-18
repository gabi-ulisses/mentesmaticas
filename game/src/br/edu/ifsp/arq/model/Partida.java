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
 * É o "Mestre do Jogo". Controla uma partida completa para uma dupla de jogadores.
 * Roda em seu próprio "trabalho" (Thread) para não travar o servidor principal.
 */
public class Partida implements Runnable {

    // Constante para definir o tempo de resposta em segundos para cada turno.
    private static final int TEMPO_POR_RODADA_SEGUNDOS = 60;

    private List<Questao> questoes;
    // Lista para guardar as informações de conexão de cada jogador.
    private final List<ClientConnection> connections;
    // "volatile" garante que as outras threads sempre vejam o valor mais atual desta variável.
    private volatile boolean sessaoAtiva;
    private volatile boolean partidaEmAndamento;
    private Timer timerGeral; 

    // Fila para as RESPOSTAS das perguntas.
    private final BlockingQueue<RespostaJogador> filaDeRespostas = new LinkedBlockingQueue<>();
    // Fila para os COMANDOS dos jogadores.
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
    
    // Classe interna para agrupar o jogador e um comando que ele enviou.
    private static class ComandoJogador {
        final Jogador jogador;
        final String comando;

        ComandoJogador(Jogador jogador, String comando) {
            this.jogador = jogador;
            this.comando = comando;
        }
    }

    // Prepara o objeto Partida com as conexões dos dois jogadores.
    public Partida(ObjectInputStream entrada1, ObjectOutputStream saida1, ObjectInputStream entrada2, ObjectOutputStream saida2) {
        this.questoes = new ArrayList<>();
        this.connections = new ArrayList<>();
        // Adiciona as duas conexões à lista para serem configuradas depois.
        this.connections.add(new ClientConnection(entrada1, saida1));
        this.connections.add(new ClientConnection(entrada2, saida2));
    }

    @Override
    public void run() {
        try {
            this.sessaoAtiva = true;
            // Pega os nomes dos jogadores.
            configurarJogadores();
            // Ordena a lista de conexões com base no nome do jogador em ordem alfabética.
            connections.sort(Comparator.comparing(c -> c.jogador.getNome(), String.CASE_INSENSITIVE_ORDER));

            // Inicia as threads ouvintes, um para cada jogador.
            for (ClientConnection conn : connections) {
                iniciarOuvinte(conn);
            }
            
            boolean jogarNovamente = true;
            while(jogarNovamente && sessaoAtiva) {
                partidaEmAndamento = true;
                jogar();
                partidaEmAndamento = false;

                // Se a sessão ainda estiver ativa, pergunta se querem jogar de novo.
                if (sessaoAtiva) {
                    jogarNovamente = perguntarSeQuerJogarNovamente();
                    if(jogarNovamente) { // Se ambos disserem sim, reseta o jogo.
                        resetarPartida();
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Sessão encerrada por erro crítico: " + e.getMessage());
        } finally {
            this.sessaoAtiva = false;
            encerrarConexoes(); // Garante que as conexões sejam fechadas.
        }
    }

    // Obtém o nome de cada jogador, enviado pelo cliente, um de cada vez.
    private void configurarJogadores() throws IOException, ClassNotFoundException {

        for (ClientConnection conn : connections) {
            conn.saida.writeObject("Bem-vindo ao Mentes Máticas!\n\n Regras do Jogo: \n     1. A ordem de jogadas é definida pelo nome do jogador, em ordem alfabética.\n       2. Cada jogador tem 60 segundos para responder cada pergunta.\n         3. As respostas válidas são as letras das opções apresentadas.\n        4. Respostas corretas valem 1 ponto.\n      5. O jogo termina quando todas as perguntas forem respondidas ou se um jogador desconectar.\n\n");
            String nome = (String) conn.entrada.readObject();
            conn.jogador = new Jogador(nome);
            System.out.println("Jogador '" + nome + "' configurado.");
        }
    }

    // Cria uma thread "ouvinte" para cada jogador.
    private void iniciarOuvinte(ClientConnection conn) {
        new Thread(() -> {
            while (sessaoAtiva) {
                try {
                    // Fica esperando por uma mensagem do jogador.
                    String mensagem = (String) conn.entrada.readObject();
                    // Decide se a mensagem é um comando ou uma resposta.
                    if (mensagem.startsWith("CMD|")) {
                        filaDeComandos.put(new ComandoJogador(conn.jogador, mensagem.substring(4)));
                    } else if (partidaEmAndamento) {
                        // Se der erro (ex: jogador desconectou), encerra a sessão.
                        filaDeRespostas.put(new RespostaJogador(conn.jogador, mensagem));
                    }
                } catch (IOException | ClassNotFoundException | InterruptedException e) {
                    System.out.println("Ouvinte para " + (conn.jogador != null ? conn.jogador.getNome() : "desconhecido") + " encerrado. A sessão terminará.");
                    sessaoAtiva = false; 
                }
            }
        }).start();
    }

    // Contém a lógica de uma partida completa, com todas as suas rodadas.
    private void jogar() throws IOException, InterruptedException {
        transmitirParaTodos("CONTROL|START_GAME");
        transmitirParaTodos("Todos os jogadores estão conectados e ordenados.");
        transmitirParaTodos("A partida vai começar!");
        carregarQuestoes();

        // Loop principal para cada questão (rodada).
        for (Questao questao : questoes) {
            if(!sessaoAtiva) break;
            
            transmitirParaTodos("\n----- NOVA RODADA -----");
            String[] opcoes = questao.getOpcoes();
            StringBuilder opcoesFormatadas = new StringBuilder();
            for (int i = 0; i < opcoes.length; i++) {
                opcoesFormatadas.append((char)('a' + i)).append(") ").append(opcoes[i]);
                if (i < opcoes.length - 1) {
                    opcoesFormatadas.append(";"); // Usa ; como separador de opções
                }
            }
            
            String perguntaMsg = String.format("PERGUNTA|%s|%s", questao.getEnunciado(), opcoesFormatadas.toString());
            transmitirParaTodos(perguntaMsg);
            
            filaDeRespostas.clear();
            // Loop para o turno de cada jogador.
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
                }else {
                    // Garante que o resultado do turno seja anunciado mesmo se o tempo esgotar.
                    transmitirParaTodos("-> " + jogadorDaVez.getNome() + " não respondeu a tempo.");
                }
            }
            exibirPlacarGeral();
            Thread.sleep(4000); 
        }
        
        if(sessaoAtiva) {
            anunciarVencedorFinal();
        }
    }

    // Inicia um contador regressivo para a rodada.
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
                        // A mensagem de "não respondeu a tempo" agora será tratada pelo método jogar().
                        cancel(); 
                    }
                } catch(IOException e) {
                    System.err.println("Erro ao transmitir tempo: " + e.getMessage());
                    cancel();
                }
            }
        }, 0, 1000); // Inicia imediatamente, repete a cada 1 segundo
    }

    private boolean perguntarSeQuerJogarNovamente() throws IOException, InterruptedException {
        // Lógica para reiniciar 
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
        // Lógica de reset 
        for(ClientConnection conn : connections) {
            conn.jogador.resetarPontuacao();
        }
        filaDeRespostas.clear();
        filaDeComandos.clear();
        transmitirParaTodos("\n--- REINICIANDO PARTIDA ---");
    }

    private void exibirPlacarGeral() throws IOException {
        // Monta a mensagem de placar no formato que o Controller espera.
        // Ex: "PLACAR|Ana;1;Gabrielle;0"
        String placarMsg = String.format("PLACAR|%s;%d;%s;%d",
            connections.get(0).jogador.getNome(), connections.get(0).jogador.getPontuacao(),
            connections.get(1).jogador.getNome(), connections.get(1).jogador.getPontuacao()
        );
        transmitirParaTodos(placarMsg);
    }
    
    private void anunciarVencedorFinal() throws IOException {
        // Lógica do vencedor 
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
        for(ClientConnection conn : connections) {
            if(conn.jogador != null && conn.jogador.equals(jogador)) {
                // Se a mensagem não for um prompt, o controller a tratará como status de turno
                if(!msg.startsWith("PROMPT|")) {
                    conn.saida.writeObject("TURNO|" + msg);
                } else {
                    conn.saida.writeObject(msg);
                }
                conn.saida.flush();
                return;
            }
        }
    }

    private void transmitirParaTodos(String msg) throws IOException {
        // Transmitir para todos 
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
