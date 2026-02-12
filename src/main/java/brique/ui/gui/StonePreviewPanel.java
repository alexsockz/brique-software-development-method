package brique.ui.gui;

import brique.core.Stone;
import brique.ui.gui.board.BoardTheme;

import javax.swing.*;
import java.awt.*;

public class StonePreviewPanel extends JPanel {

    private final transient BoardTheme theme;
    private Stone currentPlayer = Stone.BLACK;

    public StonePreviewPanel(BoardTheme theme) {
        this.theme = theme;
        setPreferredSize(new Dimension(24, 24));
        setOpaque(false);
    }

    public void setCurrentPlayer(Stone player) {
        this.currentPlayer = player;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        if (currentPlayer == Stone.BLACK) {
            g2.setColor(theme.stones().black());
        } else {
            g2.setColor(theme.stones().white());
        }
        g2.fillOval(2, 2, 20, 20);
        g2.dispose();
    }
}
