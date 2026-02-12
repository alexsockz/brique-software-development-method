package brique.ui.gui;

import brique.core.GameState;
import brique.core.Position;
import brique.core.Stone;
import brique.ui.gui.board.BoardPanel;
import brique.ui.gui.board.BoardTheme;
import brique.ui.gui.controller.ActionCommand;
import brique.ui.gui.controller.GameController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Set;

public class BriqueGameView extends JPanel implements GameStateObserver {

    private final transient GameController controller;
    private final transient BoardTheme theme;

    // UI components
    private final BoardPanel boardPanel;
    private final JLabel statusLabel;
    private final JLabel turnIndicator;
    private final JTextArea logArea;
    private final JButton swapButton;
    private final JButton newGameButton;
    private final JButton quitButton;
    private final StonePreviewPanel stonePreview;

    private int currentBoardSize = 11;

    private transient Runnable newGameAction = () -> { };

    private transient Runnable quitAction = () -> { };

    public BriqueGameView(GameController controller, BoardTheme theme) {
        this.controller = controller;
        this.theme      = theme;

        UIComponentFactory factory = new UIComponentFactory(theme);

        // Create components via factory (Factory Pattern)
        boardPanel    = new BoardPanel(theme);
        logArea       = factory.createLogArea();
        statusLabel   = factory.createStatusLabel("Welcome to Brique!");
        turnIndicator = factory.createTurnIndicator();
        stonePreview  = factory.createStonePreview();
        swapButton    = factory.createStyledButton(
                    "\u21C4 Swap (Pie Rule)", theme.menu().online());
        newGameButton = factory.createStyledButton(
                    "\u2726 New Game", theme.menu().local());
        quitButton    = factory.createStyledButton(
                    "\u2715 Quit", theme.ui().quitButton());

        // Assemble layout
        setLayout(new BorderLayout(0, 0));
        add(buildTopPanel(factory), BorderLayout.NORTH);
        add(buildCenterPanel(factory), BorderLayout.CENTER);
        add(buildBottomPanel(factory), BorderLayout.SOUTH);

        // Wire UI events → controller
        wireListeners();

        // Register as observer (Observer Pattern)
        controller.addObserver(this);
    }

    // --- Callbacks for window-level actions --------------------

    public void setNewGameAction(Runnable action) {
        this.newGameAction = action != null ? action : () -> { };
    }

    public void setQuitAction(Runnable action) {
        this.quitAction = action != null ? action : () -> { };
    }

    // --- Layout construction (pure view assembly) -------------

    private JPanel buildTopPanel(UIComponentFactory factory) {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(theme.backgrounds().statusBg());
        top.setBorder(new EmptyBorder(10, 16, 10, 16));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);
        left.add(stonePreview);
        left.add(turnIndicator);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(statusLabel);

        top.add(left, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);
        return top;
    }

    private JPanel buildCenterPanel(UIComponentFactory factory) {
        JPanel center = new JPanel(new BorderLayout(8, 0));
        center.setBackground(theme.backgrounds().main());
        center.setBorder(new EmptyBorder(8, 8, 0, 8));

        JPanel boardWrapper = new JPanel(new BorderLayout());
        boardWrapper.setBackground(theme.backgrounds().panel());
        boardWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(theme.ui().boardBorder(), 1),
            new EmptyBorder(4, 4, 4, 4)
        ));
        boardWrapper.add(boardPanel, BorderLayout.CENTER);

        center.add(boardWrapper, BorderLayout.CENTER);
        center.add(factory.createLegendPanel(), BorderLayout.EAST);
        return center;
    }

    private JPanel buildBottomPanel(UIComponentFactory factory) {
        JPanel bottom = new JPanel(new BorderLayout(0, 0));
        bottom.setBackground(theme.backgrounds().main());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        buttons.setBackground(theme.backgrounds().panel());
        buttons.setBorder(BorderFactory.createMatteBorder(
            1, 0, 0, 0, theme.ui().boardBorder()));
        buttons.add(newGameButton);
        buttons.add(swapButton);
        buttons.add(quitButton);

        bottom.add(buttons, BorderLayout.NORTH);
        bottom.add(factory.createLogScrollPane(logArea), BorderLayout.CENTER);
        return bottom;
    }

    // --- Event wiring (delegates to controller) ---------------

    private void wireListeners() {
        boardPanel.addCellClickListener((row, col) -> {
            if (controller.isRunning()) {
                controller.submitCommand(new ActionCommand.PlaceStone(row, col));
            }
        });

        swapButton.addActionListener(e -> {
            if (controller.isRunning()) controller.submitCommand(ActionCommand.Swap.INSTANCE);
        });

        newGameButton.addActionListener(e -> {
            controller.stopGame();
            newGameAction.run();
        });

        quitButton.addActionListener(e -> {
            if (controller.isRunning()) controller.submitCommand(ActionCommand.Quit.INSTANCE);
            quitAction.run();
        });
    }

    // --- GameStateObserver callbacks (from game thread → EDT) -

@Override
public void onGameStarted(int boardSize, GameState state) {
    SwingUtilities.invokeLater(() -> {
        currentBoardSize = boardSize;
        logArea.setText("");
        boardPanel.clearHighlights();
        boardPanel.setGameState(state);  // no more controller.getEngine().getState()
        appendToLog("=== New Game (" + boardSize + "×" + boardSize + ") ===");
        appendToLog("BLACK plays first. Click a cell to place a stone.");
    });
}

    @Override
    public void onBoardUpdated() {
        SwingUtilities.invokeLater(() -> boardPanel.refreshBoard());
    }

    @Override
    public void onStateChanged(Stone currentPlayer, boolean pieRuleAvailable,
                                boolean inProgress, int moveCount) {
        SwingUtilities.invokeLater(() -> {
            turnIndicator.setText(
                inProgress ? currentPlayer + "'s turn" : "Game Over");
            stonePreview.setCurrentPlayer(currentPlayer);
            boardPanel.setCurrentPlayer(currentPlayer);

            boolean showSwap = inProgress
                && currentPlayer == Stone.WHITE && pieRuleAvailable;
            swapButton.setEnabled(showSwap);
            swapButton.setVisible(showSwap);

            if (inProgress) {
                statusLabel.setText("Move #" + (moveCount + 1)
                    + "  |  Board: " + currentBoardSize + "\u00D7"
                    + currentBoardSize);
            }
        });
    }

    @Override
    public void onMoveExecuted(Position pos, Stone player,
                                Set<Position> filled,
                                Set<Position> captured) {
        SwingUtilities.invokeLater(() -> {
            boardPanel.setLastMovePosition(pos);
            boardPanel.setHighlightedPositions(filled, captured);
        });
    }

    @Override
    public void onPieRuleApplied() {
        SwingUtilities.invokeLater(() -> boardPanel.clearHighlights());
    }

    @Override
    public void onGameOver(Stone winner) {
        SwingUtilities.invokeLater(() -> {
            String msg;
            if (winner != Stone.EMPTY) {
                msg = "\uD83C\uDF89 Game Over \u2014 " + winner + " wins!";
            } else {
                msg = "Game Over \u2014 No winner.";
            }
            appendToLog("\n" + msg);
            statusLabel.setText(msg);
        });
    }

    @Override
    public void onMessage(String message) {
        SwingUtilities.invokeLater(() -> appendToLog(message));
    }

    // --- Helpers ----------------------------------------------

    void appendToLog(String text) {
        logArea.append(text + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
