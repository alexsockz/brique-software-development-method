package brique.ui.gui;

import brique.core.Stone;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class BriqueGUI extends JFrame {

    private final GameController controller;
    private final BriqueGameView gameView;

    public BriqueGUI(GameController controller, BoardTheme theme) {
        super("Brique \u2014 Board Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.controller = controller;
        this.gameView   = new BriqueGameView(controller, theme);

        setContentPane(gameView);

        // Set window-level actions on the view
        gameView.setNewGameAction(this::promptAndStartGame);
        gameView.setQuitAction(() -> {
            Timer t = new Timer(300, ev -> { dispose(); System.exit(0); });
            t.setRepeats(false);
            t.start();
        });

        // Register window-level observer for game-over dialog
        controller.addObserver(new GameStateObserver() {
            @Override
            public void onGameOver(Stone winner) {
                SwingUtilities.invokeLater(() -> {
                    String msg;
                    if (winner != Stone.EMPTY) {
                        msg = "\uD83C\uDF89 Game Over \u2014 " + winner + " wins!";
                    } else {
                        msg = "Game Over \u2014 No winner.";
                    }
                    JOptionPane.showMessageDialog(
                        BriqueGUI.this,
                        msg + "\n\nClick 'New Game' to play again.",
                        "Game Over", JOptionPane.INFORMATION_MESSAGE);
                });
            }
        });

        // Window close listener
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (controller.isRunning()) controller.submitInput("quit");
            }
        });

        // Window configuration
        setMinimumSize(new Dimension(800, 700));
        setPreferredSize(new Dimension(950, 800));
        pack();
        setLocationRelativeTo(null);
    }
    
    public BriqueGameView getGameView() {
        return gameView;
    }

    public void promptAndStartGame() {
        String input = JOptionPane.showInputDialog(
            this, "Enter board size (3\u201319):", "New Game",
            JOptionPane.QUESTION_MESSAGE);

        int size = 11;
        if (input != null && !input.trim().isEmpty()) {
            try {
                size = Integer.parseInt(input.trim());
                if (size < 3)  size = 3;
                if (size > 19) size = 19;
            } catch (NumberFormatException e) {
                gameView.appendToLog("Invalid size; using default 11.");
            }
        }
        controller.startNewGame(size);
    }
}
