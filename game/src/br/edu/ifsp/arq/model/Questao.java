package br.edu.ifsp.arq.model;

/**
 * Representa uma única pergunta do jogo, com seu enunciado, alternativas e a resposta correta.
 */
public class Questao {

    private String enunciado;
    private String[] opcoes;
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

    public boolean isRespostaCorreta(int indiceEscolhido) {
        return indiceEscolhido == this.indiceRespostaCorreta;
    }
}