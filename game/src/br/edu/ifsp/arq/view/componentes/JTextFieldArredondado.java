package br.edu.ifsp.arq.view.componentes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class JTextFieldArredondado extends JTextField {
    private Shape shape;

    public JTextFieldArredondado() { this(20); }
    public JTextFieldArredondado(int size) {
        super(size);
        setOpaque(false);
        setBorder(new EmptyBorder(5, 10, 5, 10));
    }

    @Override
    protected void paintComponent(Graphics g) {
         g.setColor(getBackground());
         g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 25, 25);
         super.paintComponent(g);
    }
    @Override
    protected void paintBorder(Graphics g) {
         g.setColor(Color.GRAY);
         g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 25, 25);
    }
    @Override
    public boolean contains(int x, int y) {
         if (shape == null || !shape.getBounds().equals(getBounds())) {
             shape = new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 25, 25);
         }
         return shape.contains(x, y);
    }
}