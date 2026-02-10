package brique.ui.gui;

import brique.core.Position;
import brique.core.Stone;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;

public class BriqueGUI extends JFrame implements GameStateObserver {

    private final GameController controller;

    private final BoardTheme theme;

    // -------------------- UI Components --------------------

    private final BoardPanel boardPanel;

    private final JLabel statusLabel;

    private final JLabel turnIndicator;

    private final JTextArea logArea;

    private final JButton swapButton;

    private final JButton newGameButton;

    private final JButton quitButton;

    private final StonePreviewPanel stonePreview;

    private int currentBoardSize = 11;

    public BriqueGUI(GameController controller, BoardTheme theme) {
        super("Brique — Board Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.controller = controller; // Injected controller dependency
        this.theme      = theme;      // Injected theme dependency

        // Factory centralizes UI component styling (Factory Pattern)
        UIComponentFactory factory = new UIComponentFactory(theme);

        // -------- Component creation --------
        boardPanel    = new BoardPanel(theme); // Board rendering and interaction
        logArea       = factory.createLogArea(); // Scrollable text area for game messages
        statusLabel   = factory.createStatusLabel("Welcome to Brique!"); // Status text at the top
        turnIndicator = factory.createTurnIndicator(); // Shows current player's turn
        stonePreview  = factory.createStonePreview(); // Visual preview of the current player's stone

        // Action buttons with consistent styling
        swapButton    = factory.createStyledButton(
                            "⇄ Swap (Pie Rule)", new Color(70, 130, 180));
        newGameButton = factory.createStyledButton(
                            "✦ New Game", new Color(80, 140, 80));
        quitButton    = factory.createStyledButton(
                            "✕ Quit", new Color(180, 70, 70));

        // -------- Layout assembly --------
        setLayout(new BorderLayout(0, 0));
        add(buildTopPanel(factory), BorderLayout.NORTH); // Status bar at the top
        add(buildCenterPanel(factory), BorderLayout.CENTER); // Board in the center
        add(buildBottomPanel(factory), BorderLayout.SOUTH); // Buttons and log at the bottom

        // Connect UI events to controller actions
        wireListeners();

        // Register as observer of game state changes (Observer Pattern)
        controller.addObserver(this);

        // Window configuration
        setMinimumSize(new Dimension(800, 700));
        setPreferredSize(new Dimension(950, 800));
        pack();
        setLocationRelativeTo(null);
    }

    public void promptAndStartGame() {

        String input = JOptionPane.showInputDialog(
            this, "Enter board size (3–19):", "New Game", // Prompt for board size
            JOptionPane.QUESTION_MESSAGE);

        
        int size = 11; // default size

        if (input != null && !input.trim().isEmpty()) { // Validate input
            try {
                size = Integer.parseInt(input.trim());
                if (size < 3)  size = 3;
                if (size > 19) size = 19;
            } catch (NumberFormatException e) { // Invalid input; use default size
                appendToLog("Invalid size; using default 11."); 
            }
        }
        controller.startNewGame(size); // Start game with the specified or default size
    }

    // -------------------- Layout construction --------------------

    private JPanel buildTopPanel(UIComponentFactory factory) {
        JPanel top = new JPanel(new BorderLayout()); // Status bar with turn indicator and messages
        top.setBackground(theme.getStatusBackground()); // Distinct background for status area
        top.setBorder(new EmptyBorder(10, 16, 10, 16)); // Padding around the status bar

        // Left side: stone preview + turn indicator
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false); // Transparent background to show the status bar color
        left.add(stonePreview); // Visual preview of the current player's stone
        left.add(turnIndicator); // Text indicating whose turn it is

        // Right side: status label
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); // Align to the right edge
        right.setOpaque(false); // Transparent background to show the status bar color
        right.add(statusLabel); // General status messages (e.g. game over, errors)

        top.add(left, BorderLayout.WEST); // Add left-aligned turn indicator and stone preview
        top.add(right, BorderLayout.EAST); // Add right-aligned status messages

        return top; // Return the assembled top status bar panel
    }

    private JPanel buildCenterPanel(UIComponentFactory factory) {

        JPanel center = new JPanel(new BorderLayout(8, 0)); // Main area with board and legend, separated by a gap
        center.setBackground(theme.getBackground()); // Match the main background color
        center.setBorder(new EmptyBorder(8, 8, 0, 8)); // Padding around the center area

        // Board container with border
        JPanel boardWrapper = new JPanel(new BorderLayout());
        boardWrapper.setBackground(theme.getPanelBackground()); // Match panel background for the board area
        boardWrapper.setBorder(BorderFactory.createCompoundBorder( // Subtle border around the board
            BorderFactory.createLineBorder(new Color(180, 170, 155), 1), // Outer line border for definition
            new EmptyBorder(4, 4, 4, 4) // Inner padding to separate the board from the border
        ));
        boardWrapper.add(boardPanel, BorderLayout.CENTER); // Add the interactive board to the center of the wrapper

        center.add(boardWrapper, BorderLayout.CENTER); // Place the board wrapper in the center of the main area
        center.add(factory.createLegendPanel(), BorderLayout.EAST); // Add a legend on the right side to explain symbols and colors

        return center; // Return the assembled center panel containing the board and legend
    }

    private JPanel buildBottomPanel(UIComponentFactory factory) {
        JPanel bottom = new JPanel(new BorderLayout(0, 0)); // Bottom area with action buttons and log, separated by a vertical gap
        bottom.setBackground(theme.getBackground()); // Match the main background color

        // Action buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8)); // Centered row of buttons with horizontal spacing
        buttons.setBackground(theme.getPanelBackground()); // Match panel background for the button area
        buttons.setBorder(BorderFactory.createMatteBorder(
            1, 0, 0, 0, new Color(180, 170, 155))); // Top border to separate buttons from the log area

        buttons.add(newGameButton); // Button to start a new game
        buttons.add(swapButton); // Button to apply the Pie Rule (swap players)
        buttons.add(quitButton); // Button to quit the application

        bottom.add(buttons, BorderLayout.NORTH); // Place buttons at the top of the bottom area
        bottom.add(factory.createLogScrollPane(logArea), BorderLayout.CENTER); // Add the log area below the buttons, filling the remaining space

        return bottom; // Return the assembled bottom panel containing action buttons and the log area
    }

    // -------------------- Event wiring --------------------

    private void wireListeners() {

        // Board clicks → controller input
        boardPanel.addCellClickListener((row, col) -> {
            if (controller.isRunning()) {
                controller.submitInput(row + " " + col); // Send cell coordinates as input to the controller
            }
        });

        // Pie Rule button
        swapButton.addActionListener(e -> {
            if (controller.isRunning()) controller.submitInput("swap"); // Send "swap" command to the controller to apply the Pie Rule
        });

        // New game button
        newGameButton.addActionListener(e -> {
            controller.stopGame(); // Stop the current game if running (graceful shutdown)
            promptAndStartGame(); // Prompt for new game settings and start a new game
        });

        // Quit button (graceful shutdown)
        quitButton.addActionListener(e -> {
            if (controller.isRunning()) controller.submitInput("quit"); // Send "quit" command to the controller to end the game and exit
            Timer t = new Timer(300, ev -> {
                dispose(); // Close the window
                System.exit(0); // Terminate the application after a short delay to allow any final messages to be processed
            });

            t.setRepeats(false); // Only execute once
            t.start(); // Start the timer to close the application after a brief delay
        });

        // Handle window close (e.g. OS close button)
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                if (controller.isRunning()) controller.submitInput("quit"); // Send "quit" command to the controller to ensure graceful shutdown when the window is closed
            }
        });
    }

    // -------------------- GameStateObserver callbacks --------------------

    @Override
    public void onGameStarted(int boardSize) {
        SwingUtilities.invokeLater(() -> {
            currentBoardSize = boardSize; // Cache the board size for status display
            logArea.setText(""); // Clear the log for the new game
            boardPanel.clearHighlights(); // Clear any highlights from the previous game
            boardPanel.setGameState(controller.getEngine().getState()); // Provide the new game state to the board panel for rendering

            // Log the new game start with the selected board size
            appendToLog("=== New Game (" + boardSize + "×" + boardSize + ") ===");
            appendToLog("BLACK plays first. Click a cell to place a stone.");
        });
    }

    @Override
    public void onBoardUpdated() {
        SwingUtilities.invokeLater(() -> boardPanel.refreshBoard()); // Refresh the board display to reflect the latest state
    }

    @Override
    public void onStateChanged(Stone currentPlayer, boolean pieRuleAvailable,
                               boolean inProgress, int moveCount) {

        // Update the turn indicator, status label, and Pie Rule button based on the new state
        SwingUtilities.invokeLater(() -> {
            turnIndicator.setText(
                inProgress ? currentPlayer + "'s turn" : "Game Over"); // Update turn indicator text based on whether the game is in progress

            // Update the stone preview and board panel with the current player for visual consistency
            stonePreview.setCurrentPlayer(currentPlayer);
            boardPanel.setCurrentPlayer(currentPlayer);

            // Pie Rule only available to WHITE on second move
            boolean showSwap = inProgress
                && currentPlayer == Stone.WHITE && pieRuleAvailable;

            // Enable and show the swap button only when the Pie Rule is available for White's turn
            swapButton.setEnabled(showSwap);
            swapButton.setVisible(showSwap);

            // Update the status label with the current move number and board size if the game is in progress
            if (inProgress) {
                statusLabel.setText("Move #" + (moveCount + 1)
                    + "  |  Board: " + currentBoardSize + "×"
                    + currentBoardSize);
            }
        });
    }

    @Override
    public void onMoveExecuted(Position pos, Stone player,
                               Set<Position> filled,
                               Set<Position> captured) {

        // Update the board highlights to show the last move, filled positions, and captured stones
        SwingUtilities.invokeLater(() -> {
            boardPanel.setLastMovePosition(pos);
            boardPanel.setHighlightedPositions(filled, captured);
        });
    }

    // Called when the Pie Rule is applied (players are swapped)
    @Override
    public void onPieRuleApplied() {
        SwingUtilities.invokeLater(() -> boardPanel.clearHighlights());
    }

    // Called when the game ends with a winner or a draw
    @Override
    public void onGameOver(Stone winner) {

        // Display the final board state and announce the winner or a draw in a dialog
        SwingUtilities.invokeLater(() -> {

            // Construct the game over message based on whether there is a winner or if it's a draw
            String msg = (winner != Stone.EMPTY)
                ? "🎉 Game Over — " + winner + " wins!"
                : "Game Over — No winner.";

            // Log the game over message and update the status label
            appendToLog("\n" + msg);
            statusLabel.setText(msg);

            // Show a dialog with the game over message and prompt to start a new game
            JOptionPane.showMessageDialog(
                this, msg + "\n\nClick 'New Game' to play again.",
                "Game Over", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    // Called when a general message needs to be displayed (e.g. errors, info)
    @Override
    public void onMessage(String message) {
        SwingUtilities.invokeLater(() -> appendToLog(message));
    }

    // -------------------- Utility --------------------

    private void appendToLog(String text) {
        logArea.append(text + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
