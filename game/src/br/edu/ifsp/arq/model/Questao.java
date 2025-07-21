package br.edu.ifsp.arq.model;

/**
 * Guarda os dados de uma única pergunta do jogo.
 * É como se fosse uma "ficha" com o enunciado, as opções e qual é a certa
 */
public class Questao {

    private String enunciado;
    // Uma lista de textos com as alternativas (ex: ["3", "4", "5"])
    private String[] opcoes;
    // O número do índice da resposta correta na lista de opções.
    private int indiceRespostaCorreta;

    public Questao(String enunciado, String[] opcoes, int indiceRespostaCorreta) {
        this.enunciado = enunciado;
        this.opcoes = opcoes;
        this.indiceRespostaCorreta = indiceRespostaCorreta;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public String[] getOpcoes() {
        return opcoes;
    }

     public int getIndiceRespostaCorreta() {
        return indiceRespostaCorreta;
    }

    // Verifica se a opção escolhida pelo jogador é a correta.
    public boolean isRespostaCorreta(int indiceEscolhido) {
        return indiceEscolhido == this.indiceRespostaCorreta;
    }
}