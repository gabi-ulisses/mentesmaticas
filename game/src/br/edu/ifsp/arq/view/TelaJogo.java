package br.edu.ifsp.arq.view;

import br.edu.ifsp.arq.controller.JogoController;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * É a tela principal do jogo, onde as perguntas e o placar aparecem.
 * Utiliza um CardLayout para alternar entre a tela de "Espera" e a de "Perguntas".
 */
public class TelaJogo {
    // Referências para o controller e os componentes principais da janela.
    private JogoController controller;
    private JFrame frame;
    
    // O painel que usa CardLayout para mostrar uma "tela" de cada vez.
    private JPanel painelPrincipal;
    private CardLayout cardLayout;
    
    // Componentes do placar, visíveis em ambas as telas.
    private JLabel labelPlacar1;
    private JLabel labelPlacar2;
    
    // Componentes da tela de espera.
    private JLabel labelBemVindo;

    // Componentes da tela de perguntas.
    private JTextArea areaLogPerguntas;
    private JTextField campoResposta;
    private JButton btnEnviar;
    private JLabel lblContador;
    private JLabel labelStatusTurno;

    // Constantes para identificar os painéis no CardLayout.
    private final String PAINEL_ESPERA = "TELA_ESPERA";
    private final String PAINEL_JOGO = "TELA_JOGO";

    public TelaJogo(JogoController controller) {
        this.controller = controller;
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        // --- Configuração da Janela Principal (JFrame) ---
        frame = new JFrame("MentesMáticas");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 700);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(214, 237, 240)); // Cor de fundo azul claro

        // --- PAINEL DO PLACAR (TOPO) ---
        JPanel painelPlacarContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 20));
        painelPlacarContainer.setOpaque(false); // Fundo transparente.
        labelPlacar1 = new JLabelArredondado("Jogador 1: 0 pontos", new Color(255, 235, 156));
        labelPlacar2 = new JLabelArredondado("Jogador 2: 0 pontos", new Color(255, 235, 156));
        painelPlacarContainer.add(labelPlacar1);
        painelPlacarContainer.add(labelPlacar2);
        frame.add(painelPlacarContainer, BorderLayout.NORTH);
        
        // --- PAINEL PRINCIPAL COM CARDLAYOUT (CENTRO) ---
        cardLayout = new CardLayout();
        painelPrincipal = new JPanel(cardLayout);
        painelPrincipal.setOpaque(false);
        
        // --- CRIA E ADICIONA O PAINEL DE ESPERA (IMAGEM 3.PNG) ---
        JPanel painelEspera = criarPainelEspera();
        painelPrincipal.add(painelEspera, PAINEL_ESPERA);
        
        // --- CRIA E ADICIONA O PAINEL DE PERGUNTAS (IMAGEM 2.PNG) ---
        JPanel painelJogo = criarPainelJogo();
        painelPrincipal.add(painelJogo, PAINEL_JOGO);

        frame.add(painelPrincipal, BorderLayout.CENTER);
        
        // O jogo começa mostrando a tela de espera.
        cardLayout.show(painelPrincipal, PAINEL_ESPERA);
    }
    
    // Método que monta a tela de "Aguardando jogador...".
    private JPanel criarPainelEspera() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setOpaque(false);
        painel.setBorder(new EmptyBorder(50, 50, 50, 50));
        
        JPanelArredondado painelBemVindo = new JPanelArredondado(new Color(137, 207, 219));
        painelBemVindo.setLayout(new GridBagLayout());
        painelBemVindo.setPreferredSize(new Dimension(600, 60));
        painelBemVindo.setMaximumSize(new Dimension(600, 60)); 
        labelBemVindo = new JLabel("Bem vindo, ..."); // O nome será atualizado pelo controller.
        labelBemVindo.setFont(new Font("Arial", Font.BOLD, 16));
        painelBemVindo.add(labelBemVindo);
        
        JPanelArredondado painelRegras = new JPanelArredondado(Color.WHITE);
        painelRegras.setLayout(new BorderLayout());
        JTextArea textoRegras = new JTextArea(
            "Regras do Jogo\n\n" +
            "1. A ordem de jogadas é definida pelo nome do jogador, em ordem alfabética.\n" +
            "2. Cada jogador tem um tempo limite para responder cada pergunta.\n" +
            "3. As respostas válidas são as letras das opções apresentadas (a, b, c...).\n" +
            "4. Respostas corretas valem 1 ponto.\n" +
            "5. O jogo termina quando todas as perguntas forem respondidas ou se um jogador desconectar."
        );
        textoRegras.setEditable(false);
        textoRegras.setOpaque(false);
        textoRegras.setFont(new Font("Arial", Font.PLAIN, 14));
        textoRegras.setLineWrap(true);
        textoRegras.setWrapStyleWord(true);
        textoRegras.setBorder(new EmptyBorder(15, 15, 15, 15));
        painelRegras.add(textoRegras, BorderLayout.CENTER);
        
        JLabel labelAguarde = new JLabel("Aguarde outro jogador para iniciar...", SwingConstants.CENTER);
        labelAguarde.setFont(new Font("Arial", Font.PLAIN, 16));
        labelAguarde.setBorder(new EmptyBorder(20, 0, 0, 0));
        labelAguarde.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        
        painel.add(painelBemVindo);
        painel.add(Box.createRigidArea(new Dimension(0, 5)));
        painel.add(painelRegras);
        painel.add(labelAguarde);
        
        return painel;
    }
    
    // Método que monta a tela principal, com as perguntas.
    private JPanel criarPainelJogo() {
        JPanel painelExterno = new JPanel(new BorderLayout());
        painelExterno.setOpaque(false);
        painelExterno.setBorder(new EmptyBorder(0, 50, 50, 50));

        JPanelArredondado painelPerguntaContainer = new JPanelArredondado(new Color(137, 207, 219));
        painelPerguntaContainer.setLayout(new BorderLayout());
        
        JPanelArredondado painelOpcoes = new JPanelArredondado(Color.WHITE);
        painelOpcoes.setLayout(new BorderLayout());
        
        areaLogPerguntas = new JTextArea();
        areaLogPerguntas.setEditable(false);
        areaLogPerguntas.setOpaque(false); // Fundo transparente para vermos a cor do painel
        areaLogPerguntas.setFont(new Font("Arial", Font.PLAIN, 16));
        areaLogPerguntas.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        painelOpcoes.add(areaLogPerguntas, BorderLayout.CENTER);
        painelPerguntaContainer.add(painelOpcoes, BorderLayout.CENTER); // Painel branco dentro do azul
        
        labelStatusTurno = new JLabel("Aguarde a sua vez!", SwingConstants.CENTER);
        labelStatusTurno.setFont(new Font("Arial", Font.BOLD, 16));
        labelStatusTurno.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        JPanel painelInputContainer = new JPanel(new BorderLayout(10, 0));
        painelInputContainer.setOpaque(false);
        
        JTextFieldArredondado painelInput = new JTextFieldArredondado();
        painelInput.setLayout(new BorderLayout(10, 0));
        painelInput.setBorder(new EmptyBorder(5, 10, 5, 10));
        painelInput.setBackground(Color.WHITE);
        
        campoResposta = new JTextField();
        campoResposta.setBorder(null); 
        campoResposta.setFont(new Font("Arial", Font.PLAIN, 16));
        
        btnEnviar = new JButton("Enviar");
        lblContador = new JLabel("--", SwingConstants.CENTER);
        lblContador.setFont(new Font("Arial", Font.BOLD, 20));
        
        painelInput.add(lblContador, BorderLayout.WEST);
        painelInput.add(campoResposta, BorderLayout.CENTER);
        
        painelInputContainer.add(painelInput, BorderLayout.CENTER);
        painelInputContainer.add(btnEnviar, BorderLayout.EAST);
        
        JPanel painelSul = new JPanel();
        painelSul.setLayout(new BoxLayout(painelSul, BoxLayout.Y_AXIS));
        painelSul.setOpaque(false);
        labelStatusTurno.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        painelInputContainer.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        
        painelSul.add(labelStatusTurno);
        painelSul.add(painelInputContainer);
        
        painelExterno.add(painelPerguntaContainer, BorderLayout.CENTER);
        painelExterno.add(painelSul, BorderLayout.SOUTH);
        
        btnEnviar.addActionListener(this::enviarAcao);
        campoResposta.addActionListener(this::enviarAcao);
        
        return painelExterno;
    }

    // --- MÉTODOS PÚBLICOS PARA O CONTROLLER ---
    
    // Chamado pelo controller para alternar para a tela de jogo.
    public void mostrarPainelJogo() {
        cardLayout.show(painelPrincipal, PAINEL_JOGO);
    }
    /**
     * Chamado pelo controller para alternar para a tela de espera.
     * Isso é útil quando uma nova partida começa após um reinício.
     */
    public void mostrarPainelEspera() {
        cardLayout.show(painelPrincipal, PAINEL_ESPERA);
    }
    // Chamado para atualizar a mensagem de boas vindas com o nome do jogador.
    public void setNomeJogador(String nome) {
        labelBemVindo.setText("Bem vindo, " + nome + "!");
    }
    
    // Atualiza os placares no topo da tela.
    public void atualizarPlacares(String nomeJogador1, int pontos1, String nomeJogador2, int pontos2) {
        labelPlacar1.setText(nomeJogador1 + ": " + pontos1 + " pontos");
        labelPlacar2.setText(nomeJogador2 + ": " + pontos2 + " pontos");
    }

    // Preenche a área de texto com a pergunta e as opções da rodada.
    public void setTextoPergunta(String pergunta, String[] opcoes) {
        StringBuilder sb = new StringBuilder();
        sb.append("Pergunta: ").append(pergunta).append("\n\n");
        sb.append("Opções:\n");
        for (int i = 0; i < opcoes.length; i++) {
            sb.append((char)('a' + i)).append(") ").append(opcoes[i]).append("\n");
        }
        areaLogPerguntas.setText(sb.toString().trim());
    }
    
    // Atualiza o texto de status (ex: "Sua vez!" ou "Aguarde...").
    public void setStatusTurno(String texto) {
        labelStatusTurno.setText(texto);
    }

    private void enviarAcao(ActionEvent e) { controller.enviarResposta(campoResposta.getText()); }
    public void mostrar() { frame.setVisible(true); }
    public void esconder() { frame.setVisible(false); }
    public void setTitulo(String titulo) { frame.setTitle(titulo); }
    public void atualizarContador(String texto) { lblContador.setText(texto); }
    public void limparCampoResposta() { campoResposta.setText(""); }
    public int mostrarDialogoReiniciar() { return JOptionPane.showConfirmDialog(frame, "A partida acabou. Desejam jogar novamente?", "Fim de Jogo", JOptionPane.YES_NO_OPTION); }
    public void mostrarAviso(String mensagem) { JOptionPane.showMessageDialog(frame, mensagem, "Aviso", JOptionPane.INFORMATION_MESSAGE); }
    public void desabilitarInteracao() { campoResposta.setEnabled(false); btnEnviar.setEnabled(false); }
}


// --- CLASSES AUXILIARES PARA COMPONENTES ARREDONDADOS ---

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

class JLabelArredondado extends JLabel {
    private int radius = 20;
    private Color bgColor;

    public JLabelArredondado(String text, Color bgColor) {
        super(text);
        this.bgColor = bgColor;
        setOpaque(false);
        setHorizontalAlignment(SwingConstants.CENTER);
        setFont(new Font("Arial", Font.BOLD, 14));
        setBorder(new EmptyBorder(10, 20, 10, 20));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.setColor(getForeground());
        super.paintComponent(g2);
        g2.dispose();
    }
}