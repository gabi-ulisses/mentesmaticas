package br.edu.ifsp.arq.model;

/**
 * Representa um jogador no jogo, com seu nome e pontuação.
 */
public class Jogador {

    private String nome;
    private int pontuacao;

    public Jogador(String nome) {
        this.nome = nome;
        this.pontuacao = 0; // Todo jogador começa com 0 pontos.
    }

    public String getNome() {
        return nome;
    }



    public int getPontuacao() {
        return pontuacao;
    }

    public void adicionarPonto() {
        this.pontuacao++;
    }
}