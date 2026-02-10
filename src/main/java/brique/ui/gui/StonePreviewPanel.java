package brique.ui.gui;

import brique.core.Stone;

import javax.swing.*;
import java.awt.*;

public class StonePreviewPanel extends JPanel {

    private transient final BoardTheme theme; // Reference to the theme for consistent colouring
    private Stone currentPlayer = Stone.BLACK; // Default to Black, as they always start

    public StonePreviewPanel(BoardTheme theme) {
        this.theme = theme; // Store the theme reference for use in painting
        setPreferredSize(new Dimension(24, 24)); // Set a fixed size for the stone preview
        setOpaque(false); // Make the panel transparent so only the stone is visible
    }

    public void setCurrentPlayer(Stone player) {
        this.currentPlayer = player; // Update the current player and trigger a repaint to show the new stone colour
        repaint(); // Request the panel to be repainted with the new player's stone colour
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Call the superclass method to ensure proper painting behavior
        Graphics2D g2 = (Graphics2D) g.create(); // Create a copy of the Graphics context to avoid side effects on the original

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                            RenderingHints.VALUE_ANTIALIAS_ON); // Enable anti-aliasing for smoother edges on the stone

        if (currentPlayer == Stone.BLACK) {
            g2.setColor(theme.getBlackStone()); // Use the theme's black stone colour for consistency with the board
        } else {
            g2.setColor(theme.getWhiteStone()); // Use the theme's white stone colour for consistency with the board
            g2.fillOval(2, 2, 20, 20); // Draw a white stone with a grey border to make it visible on the background
            g2.setColor(new Color(150, 150, 150)); // Set a grey colour for the border of the white stone to enhance visibility against the background
        }
        g2.fillOval(2, 2, 20, 20); // Draw the stone as a filled oval, with a small margin to fit within the panel
        g2.dispose(); // Dispose of the Graphics context to free up resources and avoid memory leaks
    }
}
