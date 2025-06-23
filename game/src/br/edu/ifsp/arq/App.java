package br.edu.ifsp.arq;

import br.edu.ifsp.arq.model.Jogador;
import br.edu.ifsp.arq.model.Partida;

public class App {
    public static void main(String[] args) {
        System.out.println("Bem-vindo ao MentesMáticas!");

        Partida partida = new Partida();

        partida.adicionarJogador(new Jogador("Albert Einstein"));
        partida.adicionarJogador(new Jogador("Marie Curie"));
        
        partida.carregarQuestoes();

        partida.iniciarPartida();

        System.out.println("\nObrigado por jogar MentesMáticas!");
    }
}