package br.edu.ifsp.arq.model;

import br.edu.ifsp.arq.dao.QuestaoDAO;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Partida implements Runnable {

    private List<Questao> questoes;
    private List<Jogador> jogadores;
    private boolean partidaEmAndamento;

    private List<ObjectOutputStream> saidas = null;
    private List<ObjectInputStream> entradas = null;

    public Partida(ObjectInputStream entrada1, ObjectOutputStream saida1, ObjectInputStream entrada2, ObjectOutputStream saida2) {
        this.questoes = new ArrayList<>();
        this.jogadores = new ArrayList<>();
        this.partidaEmAndamento = false;
        
        this.entradas = new ArrayList<>();
        this.saidas = new ArrayList<>();
        
        this.entradas.add(entrada1);
        this.entradas.add(entrada2);
        this.saidas.add(saida1);
        this.saidas.add(saida2);
    }

    @Override
    public void run() {
        try {
            this.partidaEmAndamento = true;
            
            for (int i = 0; i < 2; i++) {
                enviarMensagemParaJogador(i, "Bem-vindo! Por favor, digite seu nome:");
                String nome = (String) entradas.get(i).readObject();
                this.jogadores.add(new Jogador(nome));
                System.out.println("Jogador conectado: " + nome);
            }
            
            transmitirParaTodos("A partida vai começar!");
            carregarQuestoes();

            for (Questao questao : questoes) {
                transmitirParaTodos("\nPergunta: " + questao.getEnunciado());
                String[] opcoes = questao.getOpcoes();
                for (int i = 0; i < opcoes.length; i++) {
                    transmitirParaTodos((char) ('a' + i) + ") " + opcoes[i]);
                }

                for (int i = 0; i < jogadores.size(); i++) {
                    String respostaTexto = (String) entradas.get(i).readObject();
                    char respostaChar = respostaTexto.toLowerCase().charAt(0);
                    int respostaIndex = respostaChar - 'a';

                    if (questao.isRespostaCorreta(respostaIndex)) {
                        jogadores.get(i).adicionarPonto();
                        transmitirParaTodos("-> " + jogadores.get(i).getNome() + " acertou!");
                    } else {
                        transmitirParaTodos("-> " + jogadores.get(i).getNome() + " errou.");
                    }
                }
                
                transmitirParaTodos("\n--- PLACAR ---");
                for (Jogador jogador : jogadores) {
                    transmitirParaTodos(jogador.getNome() + ": " + jogador.getPontuacao() + " ponto(s)");
                }
                transmitirParaTodos("--------------");
            }

            Jogador vencedor = jogadores.get(0);
            if (jogadores.size() > 1 && jogadores.get(1).getPontuacao() > vencedor.getPontuacao()) {
                vencedor = jogadores.get(1);
            }
            transmitirParaTodos("\nO vencedor é: " + vencedor.getNome());

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Partida encerrada por erro de conexão: " + e.getMessage());
        } finally {
            this.partidaEmAndamento = false;
        }
    }
    
    private void enviarMensagemParaJogador(int i, String msg) throws IOException {
        saidas.get(i).writeObject(msg);
    }

    private void transmitirParaTodos(String msg) throws IOException {
        for(ObjectOutputStream saida : saidas) {
            saida.writeObject(msg);
        }
    }
    
    public Partida() {
        this.questoes = new ArrayList<>();
        this.jogadores = new ArrayList<>();
        this.partidaEmAndamento = false;
    }

    public void adicionarJogador(Jogador jogador) {
        if (!partidaEmAndamento) {
            this.jogadores.add(jogador);
        }
    }

    public void carregarQuestoesTxt() {
        String[] opcoesQ1 = {"3", "4", "5"};
        questoes.add(new Questao("Quanto é 2 + 2?", opcoesQ1, 1));
        String[] opcoesQ2 = {"Paris", "Londres", "Roma"};
        questoes.add(new Questao("Qual a capital da França?", opcoesQ2, 0));
        String[] opcoesQ3 = {"8", "9", "10"};
        questoes.add(new Questao("Quanto é 3 * 3?", opcoesQ3, 1));
    }

    public void carregarQuestoes() {
        QuestaoDAO dao = new QuestaoDAO();
        this.questoes = dao.carregarQuestoesDoXML("questoes.xml");
        if (!this.questoes.isEmpty()) {
            System.out.println(this.questoes.size() + " questões carregadas com sucesso do XML!");
        } else {
            System.out.println("Nenhuma questão foi carregada. Verifique o arquivo XML.");
        }
    }

    public void iniciarPartida() {
        if (jogadores.size() < 2) {
            System.out.println("A partida precisa de no mínimo 2 jogadores.");
            return;
        }
        if (questoes.isEmpty()) {
            System.out.println("Nenhuma questão carregada. A partida não pode começar.");
            return;
        }

        char[] letrasOpcoes = {'a', 'b', 'c', 'd', 'e'};
        this.partidaEmAndamento = true;
        System.out.println("\n--- A PARTIDA VAI COMEÇAR! ---");
        Scanner scanner = new Scanner(System.in);

        for (int i = 0; i < questoes.size(); i++) {
            System.out.println("\n----- RODADA " + (i + 1) + " -----");
            Questao questaoDaVez = questoes.get(i);
            for (Jogador jogador : jogadores) {
                System.out.println("\n>> Vez do jogador: " + jogador.getNome());
                System.out.println("Pergunta: " + questaoDaVez.getEnunciado());
                String[] opcoes = questaoDaVez.getOpcoes();
                for (int j = 0; j < opcoes.length; j++) {
                    System.out.println(letrasOpcoes[j] + ") " + opcoes[j]);
                }
                System.out.print("Sua resposta (digite a letra): ");
                String respostaTexto = scanner.next();
                char respostaChar = respostaTexto.toLowerCase().charAt(0);
                int respostaIndex = -1;
                for (int j = 0; j < letrasOpcoes.length; j++) {
                    if (letrasOpcoes[j] == respostaChar) {
                        respostaIndex = j;
                        break;
                    }
                }
                if (questaoDaVez.isRespostaCorreta(respostaIndex)) {
                    jogador.adicionarPonto();
                    System.out.println("Resposta CORRETA! Ponto para " + jogador.getNome() + "!");
                } else {
                    System.out.println("Resposta INCORRETA.");
                }
            }
            exibirPlacar();
        }

        scanner.close();
        this.partidaEmAndamento = false;
        System.out.println("\n----- FIM DE JOGO -----");
        exibirVencedor();
    }

    private void exibirPlacar() {
        System.out.println("\n--- PLACAR ---");
        for (Jogador jogador : jogadores) {
            System.out.println(jogador.getNome() + ": " + jogador.getPontuacao() + " ponto(s)");
        }
        System.out.println("--------------");
    }

    private void exibirVencedor() {
        Jogador vencedor = jogadores.get(0);
        for (int i = 1; i < jogadores.size(); i++) {
            if (jogadores.get(i).getPontuacao() > vencedor.getPontuacao()) {
                vencedor = jogadores.get(i);
            }
        }
        System.out.println("O grande vencedor é: " + vencedor.getNome());
    }
}
