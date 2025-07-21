package br.edu.ifsp.arq.model;

import br.edu.ifsp.arq.dao.QuestaoDAO;
import br.edu.ifsp.arq.model.mensagens.Mensagem;
import br.edu.ifsp.arq.model.mensagens.MensagemControle;
import br.edu.ifsp.arq.model.mensagens.MensagemPergunta;
import br.edu.ifsp.arq.model.mensagens.MensagemPlacar;
import br.edu.ifsp.arq.model.mensagens.MensagemStatus;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * É o "Mestre do Jogo". Controla uma partida completa para uma dupla de jogadores.
 * Roda em sua própria Thread para não travar o servidor principal.
 * Lógico do jogo
 */
public class Partida implements Runnable {
    
    private static final int TEMPO_POR_RODADA_SEGUNDOS = 60;

    private List<Questao> questoes;
    private final List<Jogador> jogadores = new ArrayList<>();

    // Atributos de cada jogador
    private Jogador jogador1;
    private Jogador jogador2;
    private ObjectInputStream entrada1;
    private ObjectOutputStream saida1;
    private ObjectInputStream entrada2;
    private ObjectOutputStream saida2;

    private final Map<Jogador, ObjectOutputStream> saidasDosJogadores = new HashMap<>();
    private final Map<Jogador, ObjectInputStream> entradasDosJogadores = new HashMap<>(); 

    // Atributos de controle de estado do jogo
    private boolean sessaoAtiva;
    private boolean partidaEmAndamento;
    private Timer timerGeral;
    
    // Atributos para comunicação entre as Threads.
    private final Object trava = new Object(); 
    private RespostaJogador ultimaRespostaRecebida;
    private String ultimoComandoRecebido;
    private Jogador autorDoUltimoComando;

    public Partida(ObjectInputStream entrada1, ObjectOutputStream saida1, ObjectInputStream entrada2, ObjectOutputStream saida2) {
        this.questoes = new ArrayList<>();
        this.entrada1 = entrada1;
        this.saida1 = saida1;
        this.entrada2 = entrada2;
        this.saida2 = saida2;
    }

    @Override
    public void run() {
        try {
            this.sessaoAtiva = true;
            configurarJogadores();
            exibirPlacarGeral(); 
            iniciarOuvintes();

            boolean jogarNovamente = true;
            while (jogarNovamente && sessaoAtiva) {
                partidaEmAndamento = true;
                jogar();
                partidaEmAndamento = false;

                if (sessaoAtiva) {
                    jogarNovamente = perguntarSeQuerJogarNovamente();
                    if (jogarNovamente) {
                        resetarPartida();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Sessão encerrada por erro crítico: " + e.getMessage());
            e.printStackTrace();
        } finally {
            encerrarSessao();
        }
    }

    private void jogar() throws IOException, InterruptedException {
        transmitirParaTodos(new MensagemControle(MensagemControle.Tipo.INICIAR_JOGO, null));
        transmitirParaTodos(new MensagemStatus("Todos os jogadores estão conectados e ordenados."));
        transmitirParaTodos(new MensagemStatus("A partida vai começar!"));
        carregarQuestoes();

        for (Questao questao : questoes) {
            if (!sessaoAtiva) break;
            transmitirParaTodos(new MensagemStatus("\nNOVA RODADA"));
            List<String> opcoesLista = Arrays.asList(questao.getOpcoes());
            transmitirParaTodos(new MensagemPergunta(questao.getEnunciado(), opcoesLista));

            for (Jogador jogadorDaVez : jogadores) {
                if (!sessaoAtiva) break;

                Jogador outroJogador = (jogadorDaVez.equals(jogadores.get(0))) ? jogadores.get(1) : jogadores.get(0);

                enviarMensagemParaJogador(jogadorDaVez, new MensagemControle(MensagemControle.Tipo.SOLICITAR_RESPOSTA, "Sua vez, " + jogadorDaVez.getNome() + "! Responda:"));
                enviarMensagemParaJogador(outroJogador, new MensagemControle(MensagemControle.Tipo.AGUARDAR_OPONENTE, "Aguarde a vez de " + jogadorDaVez.getNome() + "..."));

                iniciarTimerRodada();
                long tempoLimite = System.currentTimeMillis() + (TEMPO_POR_RODADA_SEGUNDOS * 1000);
                RespostaJogador respostaProcessada = null;

                synchronized (trava) {
                    while (System.currentTimeMillis() < tempoLimite && sessaoAtiva && respostaProcessada == null) {

                        trava.wait(tempoLimite - System.currentTimeMillis());
                        
                        if (ultimaRespostaRecebida != null) {
                            if (ultimaRespostaRecebida.jogador.equals(jogadorDaVez)) {
                                respostaProcessada = ultimaRespostaRecebida;
                                ultimaRespostaRecebida = null;
                            } else {
                                enviarMensagemParaJogador(ultimaRespostaRecebida.jogador, new MensagemStatus("Aviso: Não é a sua vez de responder."));
                                ultimaRespostaRecebida = null;
                            }
                        }

                    }
                }
                
                if (timerGeral != null) timerGeral.cancel();

                // Lógica de feedback PRIVADO
                if (respostaProcessada != null && !respostaProcessada.textoResposta.isEmpty()) {
                    char respostaChar = respostaProcessada.textoResposta.toLowerCase().charAt(0);
                    int respostaIndex = (respostaChar >= 'a' && respostaChar <= 'z') ? respostaChar - 'a' : -1;

                    if (questao.isRespostaCorreta(respostaIndex)) {
                        jogadorDaVez.adicionarPonto();
                        // Envia o feedback apenas para o jogador que respondeu.
                        enviarMensagemParaJogador(jogadorDaVez, new MensagemStatus("Você acertou!"));
                    } else {
                        // Envia o feedback apenas para o jogador que respondeu.
                        enviarMensagemParaJogador(jogadorDaVez, new MensagemStatus("Você errou."));
                    }
                } else {
                    // O aviso de tempo esgotado é público.
                    transmitirParaTodos(new MensagemStatus("-> " + jogadorDaVez.getNome() + " não respondeu a tempo."));
                }
                
                // Pausa para dar um ritmo ao jogo entre os turnos.
                Thread.sleep(1500); 
            }

            // Revela a resposta correta para TODOS.
            int indiceCorreto = questao.getIndiceRespostaCorreta();
            String textoRespostaCorreta = questao.getOpcoes()[indiceCorreto];
            transmitirParaTodos(new MensagemStatus("A resposta correta era: " + textoRespostaCorreta));
            
            // Pausa para que todos possam ler a resposta correta.
            Thread.sleep(2000);

            exibirPlacarGeral();
            
            // Pausa maior para que todos possam ver o placar atualizado.
            Thread.sleep(3000);
        }

        if (sessaoAtiva) {
            anunciarVencedorFinal();
        }
    }

    // Configuração e preparação da partida

    private void configurarJogadores() throws IOException, ClassNotFoundException {
        String welcomeMessage = "Bem-vindo ao Mentes Máticas!\n\nRegras do Jogo: \n"+
                                "   1. A ordem de jogadas é definida pelo nome do jogador, em ordem alfabética.\n"+
                                "   2. Cada jogador tem 60 segundos para responder cada pergunta.\n"+
                                "   3. As respostas válidas são as letras das opções apresentadas.\n"+
                                "   4. Respostas corretas valem 1 ponto.\n"+
                                "   5. O jogo termina quando todas as perguntas forem respondidas ou se um jogador desconectar.\n\n";

        saida1.writeObject(new MensagemStatus(welcomeMessage));
        String nome1 = (String) entrada1.readObject();
        this.jogador1 = new Jogador(nome1);
        System.out.println("Jogador 1 '" + nome1 + "' configurado.");

        saida2.writeObject(new MensagemStatus(welcomeMessage));
        String nome2 = (String) entrada2.readObject();
        this.jogador2 = new Jogador(nome2);
        System.out.println("Jogador 2 '" + nome2 + "' configurado.");

        // Associa os jogadores (sem ordenação por nome) aos seus canais de comunicação
        saidasDosJogadores.put(jogador1, saida1);
        saidasDosJogadores.put(jogador2, saida2);
        entradasDosJogadores.put(this.jogador1, entrada1);
        entradasDosJogadores.put(this.jogador2, entrada2);

        // Adiciona os jogadores à lista que será usada para ordem de jogada
        jogadores.add(jogador1);
        jogadores.add(jogador2);
        jogadores.sort((j1, j2) -> j1.getNome().compareToIgnoreCase(j2.getNome()));
    }
    
    private void iniciarOuvintes() {
        // Inicia os ouvintes considerando os jogadores sem ordenação, garantindo a ligação correta com os canais de entrada
        iniciarOuvinte(jogador1, entrada1);
        iniciarOuvinte(jogador2, entrada2);
    }

    private void iniciarOuvinte(Jogador jogador, ObjectInputStream entrada) {
        new Thread(() -> {
            while (sessaoAtiva) {
                try {
                    Object objetoRecebido = entrada.readObject();
                    if (objetoRecebido instanceof String) {
                        String mensagemTexto = (String) objetoRecebido;
                        processarMensagemDeTexto(jogador, mensagemTexto);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    if (sessaoAtiva) {
                        System.out.println("Conexão com " + jogador.getNome() + " perdida. A sessão terminará.");
                        sessaoAtiva = false;
                        synchronized(trava) {
                            trava.notifyAll();
                        }
                    }
                }
            }
        }).start();
    }
    
    private void carregarQuestoes() {
        QuestaoDAO dao = new QuestaoDAO();
        this.questoes = dao.carregarQuestoesDoXML("questoes.xml");
        Collections.shuffle(this.questoes);
    }

    // Processamento e lógica do jogo

    private void processarMensagemDeTexto(Jogador autor, String mensagem) {
        synchronized (trava) {
            if (mensagem.equals("JOGAR_NOVAMENTE_SIM")) {
                this.ultimoComandoRecebido = mensagem;
                this.autorDoUltimoComando = autor;
            } else if (partidaEmAndamento) {
                this.ultimaRespostaRecebida = new RespostaJogador(autor, mensagem);
            }
            trava.notifyAll();
        }
    }

    private boolean perguntarSeQuerJogarNovamente() throws IOException, InterruptedException {
        transmitirParaTodos(new MensagemControle(MensagemControle.Tipo.JOGAR_NOVAMENTE, null));
        int votosSim = 0;
        int respostasContadas = 0;
        long tempoLimite = System.currentTimeMillis() + 30000;
        
        while (respostasContadas < 2 && System.currentTimeMillis() < tempoLimite && sessaoAtiva) {
            synchronized (trava) {
                ultimoComandoRecebido = null;
                autorDoUltimoComando = null;
                trava.wait(tempoLimite - System.currentTimeMillis());

                if (ultimoComandoRecebido != null && ultimoComandoRecebido.equals("JOGAR_NOVAMENTE_SIM")) {
                    votosSim++;
                }
                // Se um comando (qualquer um) foi recebido, conta como resposta.
                if (autorDoUltimoComando != null) {
                    respostasContadas++;
                }
            }
        }
        return votosSim == 2;
    }
    
    private void resetarPartida() throws IOException {
        for (Jogador jogador : jogadores) {
            jogador.resetarPontuacao();
        }
        this.ultimaRespostaRecebida = null;
        this.ultimoComandoRecebido = null;
        this.autorDoUltimoComando = null;
        transmitirParaTodos(new MensagemStatus("\nREINICIANDO PARTIDA "));
    }

    private void anunciarVencedorFinal() throws IOException {
        transmitirParaTodos(new MensagemStatus("\nFIM DE JOGO"));

        jogadores.sort((j1, j2) -> Integer.compare(j2.getPontuacao(), j1.getPontuacao()));

        Jogador primeiroLugar = jogadores.get(0);
        Jogador segundoLugar = jogadores.get(1);
        
        if (primeiroLugar.getPontuacao() == segundoLugar.getPontuacao()) {
            transmitirParaTodos(new MensagemStatus("Houve um empate com " + primeiroLugar.getPontuacao() + " pontos!"));
        } else if (primeiroLugar.getPontuacao() > segundoLugar.getPontuacao()) {
            transmitirParaTodos(new MensagemStatus("O grande vencedor é: " + primeiroLugar.getNome() + " com " + primeiroLugar.getPontuacao() + " pontos!"));
        } else {
            transmitirParaTodos(new MensagemStatus("O grande vencedor é: " + segundoLugar.getNome() + " com " + segundoLugar.getPontuacao() + " pontos!"));
        }
    }
    
    private void exibirPlacarGeral() throws IOException {
        Map<String, Integer> placares = new HashMap<>();
        for (Jogador jogador : jogadores) {
            placares.put(jogador.getNome(), jogador.getPontuacao());
        }
        transmitirParaTodos(new MensagemPlacar(placares));
    }
    
    private void iniciarTimerRodada() {
        if (timerGeral != null) timerGeral.cancel();
        timerGeral = new Timer();
        final int[] tempoRestante = {TEMPO_POR_RODADA_SEGUNDOS};

        timerGeral.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    if (tempoRestante[0] >= 0 && partidaEmAndamento && sessaoAtiva) {
                        transmitirParaTodos(new MensagemControle(MensagemControle.Tipo.ATUALIZAR_TIMER, tempoRestante[0]));
                        tempoRestante[0]--;
                    } else {
                        cancel();
                    }
                } catch (IOException e) {
                    cancel();
                }
            }
        }, 0, 1000);
    }
    
    // Comunicação (Rede) 

    private void enviarMensagemParaJogador(Jogador jogador, Mensagem mensagem) throws IOException {
        if (!sessaoAtiva) return;

        ObjectOutputStream saida = saidasDosJogadores.get(jogador);

        try {
            saida.writeObject(mensagem);
            saida.flush();
        } catch (IOException e) {
            if(sessaoAtiva) System.out.println("Falha ao enviar mensagem para " + jogador.getNome() + ".");
            sessaoAtiva = false;
        }
    }

    private void transmitirParaTodos(Mensagem mensagem) throws IOException {
        for (Jogador jogador : saidasDosJogadores.keySet()) {
            enviarMensagemParaJogador(jogador, mensagem);
        }
    }
    
    private void encerrarSessao() {
        this.sessaoAtiva = false;
        if (timerGeral != null) timerGeral.cancel();
        
        System.out.println("Encerrando sessão...");
        try {
            transmitirParaTodos(new MensagemControle(MensagemControle.Tipo.FIM_DE_SESSAO, null));
        } catch (IOException e) {
            // Ignora erros aqui, pois já estamos encerrando.
        }
        
        try { if (saida1 != null) saida1.close(); } catch (IOException e) {}
        try { if (entrada1 != null) entrada1.close(); } catch (IOException e) {}
        try { if (saida2 != null) saida2.close(); } catch (IOException e) {}
        try { if (entrada2 != null) entrada2.close(); } catch (IOException e) {}
        System.out.println("Sessão finalizada e conexões encerradas.");
    }
}