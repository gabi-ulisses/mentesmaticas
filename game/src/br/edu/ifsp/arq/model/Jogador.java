package br.edu.ifsp.arq.model;

import java.util.Objects;

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

    public void resetarPontuacao() {
        this.pontuacao = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Jogador jogador = (Jogador) o;
        return Objects.equals(nome, jogador.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }
}
