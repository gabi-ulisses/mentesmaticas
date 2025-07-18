package br.edu.ifsp.arq;

import br.edu.ifsp.arq.controller.JogoController;

/**
 * Ponto de entrada principal (o "botão de play") para a aplicação do jogador.
 * Este é o arquivo que você executa para abrir o jogo com a interface gráfica.
 */
public class App {
    public static void main(String[] args) {
        // Cria o JogoController, que é o "cérebro" da nossa aplicação cliente.
        JogoController controller = new JogoController();
        // Chama o método iniciar() no controller para que ele mostre a primeira tela (a de login).
        controller.iniciar();
    }
}