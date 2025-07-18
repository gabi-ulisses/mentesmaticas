package br.edu.ifsp.arq.view;

import br.edu.ifsp.arq.controller.JogoController;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * É a tela de login do jogo, a primeira coisa que o jogador vê.
 * Sua única função é pegar o nome do jogador e avisar o Controller
 * quando o botão de conectar for clicado.
 */
public class TelaInicial {
    // Referência para o "cérebro" (controller), para que possamos avisá-lo do clique.
    private JogoController controller;
    // A janela (o quadro) da tela de login.
    private JFrame frame;
    // O campo de texto onde o jogador digita o nome.
    private JTextField txtNome;

    public TelaInicial(JogoController controller) {
        this.controller = controller;
        // Chama o método que monta a parte visual da tela.
        inicializarComponentes();
    }

    // Método responsável por criar e organizar todos os componentes visuais.
    private void inicializarComponentes() {
        frame = new JFrame("MentesMáticas - Login");
        // Define que o programa deve fechar quando esta janela for fechada.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 150);
        // Faz a janela aparecer no centro da tela.
        frame.setLocationRelativeTo(null);

        // Cria um painel para organizar os componentes. FlowLayout alinha um após o outro.
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        
        panel.add(new JLabel("Seu Nome:"));
        txtNome = new JTextField(15);
        panel.add(txtNome);
        
        JButton btnConectar = new JButton("Conectar ao Jogo");
        panel.add(btnConectar);
        
        btnConectar.addActionListener(e -> {
            // Avisa o controller que o botão foi clicado, passando o nome digitado.
            controller.conectar(txtNome.getText());
        });
        
        frame.add(panel);
    }

    // Método que o controller chama para tornar esta janela visível.
    public void mostrar() {
        frame.setVisible(true);
    }

    // Método que o controller chama para esconder esta janela (ex: após conectar).
    public void esconder() {
        frame.setVisible(false);
    }
    
    // Mostra uma pequena janela de aviso com uma mensagem.
    public void mostrarAviso(String mensagem) {
        JOptionPane.showMessageDialog(frame, mensagem, "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}