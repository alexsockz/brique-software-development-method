package brique.ui.gui.controller;

import brique.core.GameEngine;
import brique.core.GameEngineFactory;
import brique.core.GameMode;
import brique.core.Move;
import brique.core.Position;
import brique.core.Stone;
import brique.ui.gui.GameStateObserver;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// Controller class that manages the game logic and state updates.
// It runs the game loop on a background thread and communicates with the GUI through the GameStateObserver interface. 
// It processes user input (moves, swap, quit) and updates the game state accordingly, 
// while ensuring thread safety and responsiveness of the UI.

public class GameController {

    private GameEngine engine; // The core game engine that manages rules and state
    private GameMode currentMode = GameMode.LOCAL_1V1;
    private final GameNotifier notifier = new GameNotifier(); // Extracted observer management (SRP)
    private final BlockingQueue<String> inputQueue = new LinkedBlockingQueue<>(); // Queue for receiving user input from the GUI thread
    private Thread gameThread; // Background thread that runs the game loop
    private volatile boolean running; // Flag to control the game loop and ensure thread-safe start/stop of the game

    public void setGameMode(GameMode mode) { this.currentMode = mode; }

    public GameMode getGameMode() { return currentMode; }

    // --- Observer management (delegated to GameNotifier) ------

    public void addObserver(GameStateObserver observer) {
        notifier.addObserver(observer);
    }

    public void removeObserver(GameStateObserver observer) {
        notifier.removeObserver(observer);
    }

    // --- Input submission -------------------------------------

    // Thread-safe method for submitting user input (cell clicks, "swap", "quit") from the UI thread.
    public void submitInput(String input) {
        if(!inputQueue.offer(input)){throw new RuntimeException("input queue error");}
    }

    // --- Game lifecycle ---------------------------------------

    public void startNewGame(int boardSize) {
        stopGame(); // Ensure any existing game is stopped before starting a new one
        engine = GameEngineFactory.create(currentMode, boardSize); // Create a new game engine with the specified board size and depending on the gameplay mode selected
        running = true; // Set the running flag to true to start the game loop
        inputQueue.clear(); // Clear any pending input from previous games

        notifier.notifyGameStarted(boardSize);
        notifier.notifyStateChanged(engine.getState());

        gameThread = new Thread(this::gameLoop, "BriqueGameThread"); // Create a new thread to run the game loop
        gameThread.setDaemon(true); // Set as daemon so it doesn't prevent the application from exiting
        gameThread.start(); // Start the game loop thread
    }

    public void stopGame() {

        if (!running) return; // If the game is not running, no need to stop
        running = false; // Set the running flag to false to signal the game loop to stop
        if(!inputQueue.offer("quit")){ throw new RuntimeException("input queue fails");} // unblock the reading thread

        if (gameThread != null) { // Interrupt the game thread to ensure it stops promptly
            gameThread.interrupt();

            try {
                gameThread.join(500); // Wait for the game thread to finish, with a timeout to prevent hanging
            } catch (InterruptedException e) { // If the current thread is interrupted while waiting, re-interrupt it and exit
                Thread.currentThread().interrupt();
            }
        }
    }

    // Returns whether a game is currently running. Useful for the GUI to enable/disable controls accordingly.
    public boolean isRunning() {
        return running;
    }

    public GameEngine getEngine() {
        return engine;
    }

    // --- Game loop (runs on background thread) ----------------

    private void gameLoop() {

        // Main game loop: runs until the game is over or the controller is stopped. 
        // It waits for user input, processes it, and updates the game state accordingly. 
        // It also notifies observers of any state changes, move effects, and game over conditions.
        while (running && !engine.isGameOver()) {

            notifier.notifyStateChanged(engine.getState());

            String input; // Wait for the next user input from the queue (e.g. cell click, "swap", "quit")

            try {
                input = inputQueue.take();
            
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            if (!running) break; // Check if the game was stopped while waiting for input

            processInput(input.trim()); // Process the input command (handle moves, swap, quit) and update the game state accordingly
        }

        running = false; // Ensure the running flag is false when the game loop exits (either by game over or stop signal)

        if (engine != null) {
            notifier.notifyGameOver(engine.getState().getWinner());
        }
    }

    // --- Input processing (typed command dispatch) ------------

    private void processInput(String input) {

        ActionCommand cmd = ActionCommand.parse(input);

        if (cmd == null) {
            notifier.notifyMessage("Invalid input.");
            return;
        }

        if (cmd instanceof ActionCommand.Quit) {
            handleQuit();
        } else if (cmd instanceof ActionCommand.Swap) {
            handleSwap();
        } else if (cmd instanceof ActionCommand.PlaceStone place) {
            handlePlaceStone(place);
        }
    }

    private void handleQuit() {
        engine.getState().abort();
        notifier.notifyMessage("Game aborted.");
        running = false;
    }

    private void handleSwap() {
        try {
            engine.getState().applyPieRule();
            notifier.notifyMessage("\u21C4 Pie rule applied! Colors swapped.");
            notifier.notifyPieRuleApplied();
            notifier.notifyBoardUpdated();
        } catch (Exception e) {
            notifier.notifyMessage("Cannot swap: " + e.getMessage());
        }
    }

    private void handlePlaceStone(ActionCommand.PlaceStone cmd) {

        try {
            Position pos  = cmd.getPosition();
            Stone    player = engine.getState().getCurrentPlayer();
            boolean  success = engine.playMove(pos);

            if (!success) {
                notifier.notifyMessage("Invalid move at (" + cmd.getRow() + ", " + cmd.getCol() + "). Try again.");
            } else {
                notifier.notifyMessage(player + " placed at (" + cmd.getRow() + ", " + cmd.getCol() + ")");
                reportMoveEffects(pos, player);
                notifier.notifyBoardUpdated();
            }
        } catch (IllegalStateException e) {
            notifier.notifyMessage("Error: " + e.getMessage());
            running = false;
        }
    }

    // Helper method to report the effects of a move (placements, captures, fillings) to observers after a move is executed.
    private void reportMoveEffects(Position pos, Stone player) {

        // Retrieve the last move from the game history to get the details of the move effects 
        // (captured positions, filled positions) and notify observers with this information for UI updates.
        List<Move> history = engine.getState().getMoveHistory();

        // If there is no move history (which should not happen after a successful move), return without notifying observers.
        if (history.isEmpty()) return;

        // Get the last move from the history, extract the filled and captured positions, 
        // and notify observers of the move execution with these details for UI updates (e.g. highlights, messages).
        Move lastMove = history.get(history.size() - 1);
        Set<Position> filled  = new HashSet<>(lastMove.getFilledPositions());
        Set<Position> captured = new HashSet<>(lastMove.getCapturedPositions());

        notifier.notifyMoveExecuted(pos, player, filled, captured);

        if (!filled.isEmpty()) {
            notifier.notifyMessage("  \u2192 Escort fill: " + filled.size() + " cell(s)");
        }
        if (!captured.isEmpty()) {
            notifier.notifyMessage("  \u2192 Captured: " + captured.size() + " opponent stone(s)!");
        }
    }

}
