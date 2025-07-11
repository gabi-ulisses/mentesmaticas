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
 * Representa a tela inicial/login do jogo.
 */
public class TelaInicial {
    private JogoController controller;
    private JFrame frame;
    private JTextField txtNome;

    public TelaInicial(JogoController controller) {
        this.controller = controller;
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        frame = new JFrame("MentesMáticas - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 150);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        
        panel.add(new JLabel("Seu Nome:"));
        txtNome = new JTextField(15);
        panel.add(txtNome);
        
        JButton btnConectar = new JButton("Conectar ao Jogo");
        panel.add(btnConectar);
        
        btnConectar.addActionListener(e -> {
            controller.conectar(txtNome.getText());
        });
        
        frame.add(panel);
    }

    public void mostrar() {
        frame.setVisible(true);
    }

    public void esconder() {
        frame.setVisible(false);
    }
    
    public void mostrarAviso(String mensagem) {
        JOptionPane.showMessageDialog(frame, mensagem, "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}