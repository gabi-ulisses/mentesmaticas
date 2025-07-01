package br.edu.ifsp.arq.model;

public class RespostaJogador {
    public Jogador jogador;
    public String textoResposta;

    public RespostaJogador(Jogador jogador, String textoResposta) {
        this.jogador = jogador;
        this.textoResposta = textoResposta;
    }
}