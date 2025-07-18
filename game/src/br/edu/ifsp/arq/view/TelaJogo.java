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
 * É a tela principal do jogo, onde as perguntas e o placar aparecem.
 * Ela é como a "mesa" e o "cardápio": só mostra o que o JogoController mandar
 * e avisa ele quando o jogador faz alguma coisa (como responder).
 */
public class TelaJogo {
    // Referência para o "cérebro" do cliente, para que a tela possa avisá-lo dos eventos.
    private JogoController controller;
    // A janela principal da tela do jogo.
    private JFrame frame;
    // A área de texto grande, onde o histórico da partida é exibido.
    private JTextArea areaLog;
    // O campo de texto pequeno onde o jogador digita a resposta.
    private JTextField campoResposta;
    // O botão para enviar a resposta.
    private JButton btnEnviar;
    // O texto que mostra o tempo restante.
    private JLabel lblContador;

    public TelaJogo(JogoController controller) {
        this.controller = controller;
        inicializarComponentes();
    }

    // Método que cria e organiza todos os componentes visuais da janela.
    private void inicializarComponentes() {
        // Cria a janela principal do jogo.
        frame = new JFrame("MentesMáticas");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        
        // Cria a área de Log (JTextArea) 
        areaLog = new JTextArea();
        areaLog.setEditable(false); // Impede que o usuário digite diretamente na área de log.
        areaLog.setLineWrap(true); // Faz com que o texto quebre a linha automaticamente.
        areaLog.setWrapStyleWord(true); // Quebra a linha por palavras, não por caracteres.
        // Adiciona a área de log com uma barra de rolagem no centro da janela.
        frame.add(new JScrollPane(areaLog), BorderLayout.CENTER);
        
        JPanel painelInferior = new JPanel(new BorderLayout(10, 0));
        painelInferior.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Cria componentes do Painel Inferior 
        campoResposta = new JTextField();
        btnEnviar = new JButton("Enviar");
        lblContador = new JLabel("60", SwingConstants.CENTER); // Inicia com 60
        lblContador.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Adiciona os componentes ao painel inferior em suas posições.
        painelInferior.add(lblContador, BorderLayout.WEST);     // Timer à esquerda.
        painelInferior.add(campoResposta, BorderLayout.CENTER); // Campo de texto no meio.
        painelInferior.add(btnEnviar, BorderLayout.EAST);       // Botão à direita.
        
        frame.add(painelInferior, BorderLayout.SOUTH);

        //  Lógica de Eventos
        // Adiciona um "ouvinte" de ação para o botão e para a tecla Enter no campo de texto.
        // "this::enviarAcao" é um atalho para chamar o método enviarAcao() desta classe.   
        btnEnviar.addActionListener(this::enviarAcao);
        campoResposta.addActionListener(this::enviarAcao);
    }

    // Método chamado sempre que o botão "Enviar" ou a tecla Enter são pressionados.
    private void enviarAcao(ActionEvent e) {
        controller.enviarResposta(campoResposta.getText());
    }

    // Torna a janela visível.
    public void mostrar() {
        frame.setVisible(true);
    }

    // Esconde a janela.
    public void esconder() {
        frame.setVisible(false);
    }
    
    public void setTitulo(String titulo) {
        frame.setTitle(titulo);
    }

    // Adiciona texto à área de log do jogo.
    public void adicionarLog(String texto, boolean pularLinha) {
        areaLog.append(texto + (pularLinha ? "\n" : ""));
        // Faz a barra de rolagem descer automaticamente para a última linha.
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
