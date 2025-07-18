package br.edu.ifsp.arq.view.componentes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class JLabelArredondado extends JLabel {
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