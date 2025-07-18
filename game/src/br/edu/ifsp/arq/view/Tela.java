package br.edu.ifsp.arq.view;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Classe "mãe" abstrata para todas as janelas do jogo.
 * Contém o código comum (o JFrame e os métodos de controle)
 * para evitar a repetição de código nas classes "filhas".
 */
public abstract class Tela {
    // O JFrame agora pertence à classe base.
    // "protected" significa que ele é acessível por esta classe e suas filhas.
    protected JFrame frame;


    public Tela(String titulo) {
        frame = new JFrame(titulo);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Centraliza a janela
        frame.setResizable(false);
    }

    // Método que o controller chama para tornar janela visível.
    public void mostrar() {
        frame.setVisible(true);
    }

    // Método que o controller chama para esconder janela.
    public void esconder() {
        frame.setVisible(false);
    }

    // Mostra uma pequena janela de aviso com uma mensagem.
    public void mostrarAviso(String mensagem) {
        JOptionPane.showMessageDialog(frame, mensagem, "Aviso", JOptionPane.WARNING_MESSAGE);
    }
    
    // Permite que o controller mude o título da janela.
    public void setTitulo(String titulo) {
        frame.setTitle(titulo);
    }
}