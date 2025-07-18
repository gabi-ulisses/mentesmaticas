package br.edu.ifsp.arq.view;

import br.edu.ifsp.arq.controller.JogoController;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * É a tela de login do jogo, a primeira coisa que o jogador vê.
 * Sua única função é pegar o nome do jogador e avisar o Controller
 * quando o botão de conectar for clicado.
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
        frame.setSize(400, 550); // Aumentei um pouco a altura para o botão
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        
        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setBackground(new Color(214, 237, 240)); 
        painelPrincipal.setLayout(new BoxLayout(painelPrincipal, BoxLayout.Y_AXIS)); 
        painelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20)); 
        frame.add(painelPrincipal);

        // --- Adiciona a Logo ---
        try {
            ImageIcon logoIconOriginal = new ImageIcon(getClass().getResource("/logo.png"));
            Image imagemOriginal = logoIconOriginal.getImage();
            Image imagemRedimensionada = imagemOriginal.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            ImageIcon logoIconRedimensionado = new ImageIcon(imagemRedimensionada);
            JLabel labelLogo = new JLabel(logoIconRedimensionado);
            labelLogo.setAlignmentX(JPanel.CENTER_ALIGNMENT);
            painelPrincipal.add(labelLogo);
        } catch (Exception e) {
            System.err.println("Erro ao carregar a imagem da logo.");
        }

        painelPrincipal.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- Painel de Boas-Vindas (Azul e Arredondado) ---
        JPanelArredondado painelBoasVindas = new JPanelArredondado();
        painelBoasVindas.setBackground(new Color(137, 207, 219)); // Cor azul do design
        painelBoasVindas.setLayout(new GridBagLayout()); 
        painelBoasVindas.setPreferredSize(new Dimension(350, 60));
        painelBoasVindas.setMaximumSize(new Dimension(350, 60)); 
        JLabel labelBoasVindas = new JLabel("Bem vindo, ao MentesMáticas!");
        labelBoasVindas.setFont(new Font("Arial", Font.BOLD, 14));
        painelBoasVindas.add(labelBoasVindas);
        painelPrincipal.add(painelBoasVindas);

        // Adiciona um espaço vertical PEQUENO, pois os painéis são juntos na imagem.
        painelPrincipal.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // --- Painel de Login (Branco e Arredondado) ---
        JPanelArredondado painelLogin = new JPanelArredondado();
        painelLogin.setBackground(Color.WHITE);
        painelLogin.setLayout(new BoxLayout(painelLogin, BoxLayout.Y_AXIS));
        painelLogin.setPreferredSize(new Dimension(350, 120));
        painelLogin.setMaximumSize(new Dimension(350, 120));
        painelLogin.setBorder(new EmptyBorder(15, 15, 15, 15)); // Margem interna
        
        JLabel labelNome = new JLabel("Digite seu nome para iniciar:");
        labelNome.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        labelNome.setFont(new Font("Arial", Font.PLAIN, 14));
        
        txtNome = new JTextFieldArredondado(20);
        txtNome.setPreferredSize(new Dimension(300, 50));
        txtNome.setMaximumSize(new Dimension(300, 50));
        txtNome.setFont(new Font("Arial", Font.PLAIN, 16));
        txtNome.setHorizontalAlignment(JTextField.CENTER);
        // Define a cor de fundo do campo de texto para um cinza claro, como na imagem
        txtNome.setBackground(new Color(235, 235, 235));
        
        // Adiciona os componentes ao painel de login com espaçamentos
        painelLogin.add(labelNome);
        painelLogin.add(Box.createRigidArea(new Dimension(0, 10)));
        painelLogin.add(txtNome);
        
        painelPrincipal.add(painelLogin);
        
        // --- Botão Conectar (separado, abaixo dos painéis) ---
        painelPrincipal.add(Box.createVerticalGlue()); // Espaço flexível para empurrar o botão para baixo
        
        JButton btnConectar = new JButton("Conectar ao Jogo");
        btnConectar.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        painelPrincipal.add(btnConectar);
        
        // --- Ação do Botão ---
        btnConectar.addActionListener(e -> controller.conectar(txtNome.getText()));
    }
    
    // ... métodos mostrar(), esconder(), mostrarAviso() ...
    public void mostrar() { frame.setVisible(true); }
    public void esconder() { frame.setVisible(false); }
    public void mostrarAviso(String mensagem) { JOptionPane.showMessageDialog(frame, mensagem, "Aviso", JOptionPane.WARNING_MESSAGE); }
}


// --- CLASSES AUXILIARES DE DESIGN ---

class JPanelArredondado extends JPanel {
    private int radius = 25;
    private Color bgColor;
    // Construtor padrão, usa branco se nenhuma cor for especificada.
    public JPanelArredondado() {
        this(Color.WHITE);
    }
    public JPanelArredondado(Color bgColor) {
        super();
        this.bgColor = bgColor;
        setOpaque(false);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Usa a cor que foi guardada (bgColor) para desenhar o fundo
        graphics.setColor(bgColor);
        graphics.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
    }
}

/**
 * Classe auxiliar para criar um JTextField com cantos arredondados.
 */
class JTextFieldArredondado extends JTextField {
    private Shape shape;

    // --- CONSTRUTOR ADICIONADO ---
    // Este é o construtor "vazio" que estava faltando.
    // Ele simplesmente chama o outro construtor com um valor padrão (ex: 20).
    public JTextFieldArredondado() {
        this(20);
    }
    
    // Construtor original que recebe o tamanho.
    public JTextFieldArredondado(int size) {
        super(size);
        setOpaque(false); // Deixa o fundo padrão transparente para podermos desenhar o nosso
        setBorder(new EmptyBorder(5, 10, 5, 10)); // Adiciona uma margem interna para o texto
    }

    protected void paintComponent(Graphics g) {
         g.setColor(getBackground());
         // Desenha um retângulo arredondado com a cor de fundo do componente
         g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 25, 25);
         super.paintComponent(g);
    }
    protected void paintBorder(Graphics g) {
         g.setColor(Color.GRAY);
         // Desenha a borda do retângulo arredondado
         g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 25, 25);
    }
    public boolean contains(int x, int y) {
         if (shape == null || !shape.getBounds().equals(getBounds())) {
             shape = new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 25, 25);
         }
         return shape.contains(x, y);
    }
}