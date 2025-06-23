package br.edu.ifsp.arq.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import br.edu.ifsp.arq.dao.QuestaoDAO;

/**
 * "Orquestra" o jogo, gerenciando os jogadores, as questões e o andamento da partida.
 */
public class Partida {

    private List<Questao> questoes;
    private List<Jogador> jogadores;
    private boolean partidaEmAndamento;

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

    // Este é um método provisório. No futuro, as questões virão do QuestaoDAO
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

        // Array de opções - poderia ser I, II, ou outras
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

                // Array exibe a letra correspondente ao índice j
                for (int j = 0; j < opcoes.length; j++) {
                    System.out.println(letrasOpcoes[j] + ") " + opcoes[j]);
                }
                
                System.out.print("Sua resposta (digite a letra): ");
                String respostaTexto = scanner.next();
                char respostaChar = respostaTexto.toLowerCase().charAt(0);
                
                // Conversão de letra de volta para um índice, procurando a letra no array.
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