package br.edu.ifsp.arq.view;

import br.edu.ifsp.arq.controller.JogoController;
import br.edu.ifsp.arq.view.componentes.JPanelArredondado;
import br.edu.ifsp.arq.view.componentes.JTextFieldArredondado;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Image;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * É a tela de login do jogo, a primeira coisa que o jogador vê.
 * Sua única função é pegar o nome do jogador e avisar o Controller
 * quando o botão de conectar for clicado.
 */
public class TelaInicial extends Tela{
    private JogoController controller;
    private JTextField txtNome;

    public TelaInicial(JogoController controller) {
        super("MentesMáticas - Login");        
        this.controller = controller;
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        frame.setSize(400, 550); 
    
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

}
