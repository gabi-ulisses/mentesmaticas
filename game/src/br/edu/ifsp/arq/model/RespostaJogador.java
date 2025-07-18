package br.edu.ifsp.arq.model;

/**
 * Uma classe simples para "juntar" o jogador com a resposta que ele enviou.
 * É como um "pacotinho" que guarda essas duas informações juntas.
 */
public class RespostaJogador {
    public Jogador jogador;
    public String textoResposta;

    public RespostaJogador(Jogador jogador, String textoResposta) {
        this.jogador = jogador;
        this.textoResposta = textoResposta;
    }
}