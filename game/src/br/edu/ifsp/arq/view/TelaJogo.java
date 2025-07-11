package br.edu.ifsp.arq.view;

import br.edu.ifsp.arq.controller.JogoController;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Representa a tela principal onde o jogo acontece.
 */
public class TelaJogo {
    private JogoController controller;
    private JFrame frame;
    private JTextArea areaLog;
    private JTextField campoResposta;
    private JButton btnEnviar;

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
        
        JPanel painelInferior = new JPanel(new BorderLayout());
        campoResposta = new JTextField();
        btnEnviar = new JButton("Enviar");
        
        painelInferior.add(campoResposta, BorderLayout.CENTER);
        painelInferior.add(btnEnviar, BorderLayout.EAST);
        
        frame.add(painelInferior, BorderLayout.SOUTH);
        
        // Ação para o botão e para a tecla Enter
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
        // Rola automaticamente para o final
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }

    public void limparCampoResposta() {
        campoResposta.setText("");
    }
    
    public void mostrarAviso(String mensagem) {
        JOptionPane.showMessageDialog(frame, mensagem, "Aviso", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void desabilitarInteracao() {
        campoResposta.setEnabled(false);
        btnEnviar.setEnabled(false);
    }
}