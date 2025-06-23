package br.edu.ifsp.arq;

import br.edu.ifsp.arq.model.Jogador;
import br.edu.ifsp.arq.model.Partida;

public class Versao1 {
    public static void main(String[] args) {
        System.out.println("Bem-vindo ao MentesMáticas!");

        Partida partida = new Partida();

        partida.adicionarJogador(new Jogador("Albert Einstein"));
        partida.adicionarJogador(new Jogador("Marie Curie"));
        
        partida.carregarQuestoesTxt();

        partida.iniciarPartida();

        System.out.println("\nObrigado por jogar MentesMáticas!");
    }
}