package br.edu.ifsp.arq.view.componentes;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class JPanelArredondado extends JPanel {
    private int radius = 35;
    private Color bgColor;

    public JPanelArredondado() { this(Color.WHITE); }
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
        graphics.setColor(bgColor);
        graphics.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
    }
}