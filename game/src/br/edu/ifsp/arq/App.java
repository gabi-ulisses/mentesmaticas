package br.edu.ifsp.arq;

import br.edu.ifsp.arq.controller.JogoController;

/**
 * Ponto de entrada principal para a aplicação cliente com interface gráfica (GUI).
 * Responsável por iniciar o Controller e a primeira tela do jogo.
 */

public class App {
    public static void main(String[] args) {
        JogoController controller = new JogoController();
        controller.iniciar();
    }
}