package br.edu.ifsp.arq.view;

import br.edu.ifsp.arq.controller.JogoController;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Representa a tela principal onde o jogo acontece.
 */
public class TelaJogo {
    private JogoController controller;
    private JFrame frame;
    private JTextArea areaLog;
    private JTextField campoResposta;
    private JButton btnEnviar;
    private JLabel lblContador; // Label para o temporizador

    public TelaJogo(JogoController controller) {
        this.controller = controller;
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        frame = new JFrame("MentesMáticas");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        
        areaLog = new JTextArea();
        areaLog.setEditable(false);
        areaLog.setLineWrap(true);
        areaLog.setWrapStyleWord(true);
        frame.add(new JScrollPane(areaLog), BorderLayout.CENTER);
        
        JPanel painelInferior = new JPanel(new BorderLayout(10, 0));
        painelInferior.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        campoResposta = new JTextField();
        btnEnviar = new JButton("Enviar");
        lblContador = new JLabel("60", SwingConstants.CENTER); // Inicia com 60
        lblContador.setFont(new Font("Arial", Font.BOLD, 16));
        
        painelInferior.add(campoResposta, BorderLayout.CENTER);
        painelInferior.add(btnEnviar, BorderLayout.EAST);
        painelInferior.add(lblContador, BorderLayout.WEST); // Adiciona o contador à esquerda
        
        frame.add(painelInferior, BorderLayout.SOUTH);
        
        btnEnviar.addActionListener(this::enviarAcao);
        campoResposta.addActionListener(this::enviarAcao);
    }
    
    private void enviarAcao(ActionEvent e) {
        controller.enviarResposta(campoResposta.getText());
    }

    public void mostrar() {
        frame.setVisible(true);
    }

    public void esconder() {
        frame.setVisible(false);
    }
    
    public void setTitulo(String titulo) {
        frame.setTitle(titulo);
    }
    
    public void adicionarLog(String texto, boolean pularLinha) {
        areaLog.append(texto + (pularLinha ? "\n" : ""));
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }
    
    public void atualizarContador(String texto) {
        // Garante que a atualização da GUI ocorra na Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            lblContador.setText(texto);
        });
    }

    public void limparCampoResposta() {
        campoResposta.setText("");
    }
    
    public void limparLog() {
        areaLog.setText("");
    }
    
    public int mostrarDialogoReiniciar() {
        return JOptionPane.showConfirmDialog(frame, "A partida acabou. Desejam jogar novamente?", "Fim de Jogo", JOptionPane.YES_NO_OPTION);
    }

    public void mostrarAviso(String mensagem) {
        JOptionPane.showMessageDialog(frame, mensagem, "Aviso", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void desabilitarInteracao() {
        campoResposta.setEnabled(false);
        btnEnviar.setEnabled(false);
    }
}
