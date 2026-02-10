package brique.ui.gui;

import brique.core.GameEngine;
import brique.core.GameState;
import brique.core.Move;
import brique.core.Position;
import brique.core.Stone;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

// Controller class that manages the game logic and state updates.
// It runs the game loop on a background thread and communicates with the GUI through the GameStateObserver interface. 
// It processes user input (moves, swap, quit) and updates the game state accordingly, 
// while ensuring thread safety and responsiveness of the UI.

public class GameController {

    private GameEngine engine; // The core game engine that manages rules and state
    private final List<GameStateObserver> observers = new CopyOnWriteArrayList<>(); // Thread-safe list of observers to notify about state changes
    private final BlockingQueue<String> inputQueue = new LinkedBlockingQueue<>(); // Queue for receiving user input from the GUI thread
    private Thread gameThread; // Background thread that runs the game loop
    private volatile boolean running; // Flag to control the game loop and ensure thread-safe start/stop of the game

    // --- Observer management ----------------------------------

    public void addObserver(GameStateObserver observer) {
        observers.add(Objects.requireNonNull(observer)); // Ensure observer is not null before adding
    }

    public void removeObserver(GameStateObserver observer) {
        observers.remove(observer); // Remove observer from the list
    }

    // --- Input submission -------------------------------------

    // Thread-safe method for submitting user input (cell clicks, "swap", "quit") from the UI thread.
    public void submitInput(String input) {
        inputQueue.offer(input);
    }

    // --- Game lifecycle ---------------------------------------

    public void startNewGame(int boardSize) {
        stopGame(); // Ensure any existing game is stopped before starting a new one
        engine = new GameEngine(boardSize); // Create a new game engine with the specified board size
        running = true; // Set the running flag to true to start the game loop
        inputQueue.clear(); // Clear any pending input from previous games

        notifyGameStarted(boardSize); // Notify observers that a new game has started with the given board size
        notifyStateChanged(); // Notify observers of the initial state of the game

        gameThread = new Thread(this::gameLoop, "BriqueGameThread"); // Create a new thread to run the game loop
        gameThread.setDaemon(true); // Set as daemon so it doesn't prevent the application from exiting
        gameThread.start(); // Start the game loop thread
    }

    public void stopGame() {

        if (!running) return; // If the game is not running, no need to stop
        running = false; // Set the running flag to false to signal the game loop to stop
        inputQueue.offer("quit"); // unblock the reading thread

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

            notifyStateChanged(); // Notify observers of the current state (current player, pie rule availability, move count, etc.)

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

        // Notify observers that the game is over and provide the winner (if any) for final UI updates
        if (engine != null) {
            notifyGameOver(engine.getState().getWinner());
        }
    }

    // --- Input processing (Strategy-like dispatch) ------------

    private void processInput(String input) {
        
        // Handle the quit command to abort the game and notify observers
        if (input.equalsIgnoreCase("quit")) { 
            handleQuit();
        
            // Handle the swap command to apply the Pie Rule and update the game state and observers
        } else if (input.equalsIgnoreCase("swap")) {
            handleSwap();
        
        // Otherwise, treat the input as a move command (e.g. "row col") and attempt to play the move through the game engine, then notify observers of the move effects
        } else {
            handleMove(input);
        }
    }

    // Handles the "quit" command by aborting the game and notifying observers
    private void handleQuit() {
        engine.getState().abort();
        notifyMessage("Game aborted."); // Notify observers that the game was aborted
        running = false;
    }

    // Handles the "swap" command by applying the Pie Rule, updating the game state, and notifying observers of the swap and any resulting state changes
    private void handleSwap() {

        try {
            // Apply the Pie Rule through the game engine, 
            // which will swap the players and update the state accordingly. 
            // Notify observers of the swap and any resulting state changes.
            engine.getState().applyPieRule();
            notifyMessage("\u21C4 Pie rule applied! Colors swapped.");
            notifyPieRuleApplied();
            notifyBoardUpdated();

        // Catch any exceptions that occur during the swap (e.g. invalid state for swap) 
        // and notify observers of the error message without crashing the game loop
        } catch (Exception e) {
            notifyMessage("Cannot swap: " + e.getMessage());
        }
    }

    // Handles move commands by parsing the input, validating the move, 
    // applying it through the game engine, and notifying observers of the move effects 
    // (placements, captures, fillings). 
    // It also handles any errors in move processing and notifies observers accordingly.
    private void handleMove(String input) {
       
        String[] parts = input.split("\\s+");
       
        if (parts.length != 2) {
            notifyMessage("Invalid input."); // Notify observers that the input was invalid and prompt for correct input format
            return;
        }

        try {
            // Parse the row and column from the input, create a Position object, 
            // and attempt to play the move through the game engine.
            int row = Integer.parseInt(parts[0]); 
            int col = Integer.parseInt(parts[1]);
            // Create a Position object from the parsed row and column, and attempt to play the move through the game engine.
            Position pos = new Position(row, col);
            // Get the current player from the game state before playing the move, so we can report it in the observers after the move is processed.
            Stone player = engine.getState().getCurrentPlayer();

            boolean success = engine.playMove(pos);
            
            // If the move was unsuccessful, notify observers that the move was invalid and prompt for another move.
            if (!success) {
                notifyMessage("Invalid move at (" + row + ", " + col + "). Try again.");
            
            // else if the move was successful, notify observers of the move execution, including the position, player, 
            // and any effects (filled positions, captured stones), and then update the board display.
            } else {
                notifyMessage(player + " placed at (" + row + ", " + col + ")");
                reportMoveEffects(pos, player);
                notifyBoardUpdated();
            }

        // Catch any exceptions that occur during move processing (e.g. invalid coordinates, illegal move) 
        // and notify observers of the error message without crashing the game loop
        } catch (NumberFormatException e) {
            notifyMessage("Invalid coordinates. Click on a cell to play.");
        
        // Catch any other exceptions (e.g. illegal move, game state issues) and notify observers of the error message
        } catch (IllegalStateException e) {
            notifyMessage("Error: " + e.getMessage());
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

        // Notify observers of the move execution, including the position of the move, 
        // the player who made the move, and any filled or captured positions resulting from the move.
        notifyMoveExecuted(pos, player, filled, captured);

        // Additionally, send messages to observers about the move effects 
        // (e.g. number of cells filled, number of stones captured) for user feedback.
        if (!filled.isEmpty()) {
            notifyMessage("  \u2192 Escort fill: " + filled.size() + " cell(s)");
        }
        if (!captured.isEmpty()) {
            notifyMessage("  \u2192 Captured: " + captured.size() + " opponent stone(s)!");
        }
    }

    // --- Observer notification helpers ------------------------

    private void notifyGameStarted(int boardSize) {
        for (GameStateObserver o : observers) o.onGameStarted(boardSize); // Notify observers that a new game has started with the specified board size
    }

    private void notifyBoardUpdated() {
        for (GameStateObserver o : observers) o.onBoardUpdated(); // Notify observers that the board has been updated and they should refresh their display
    } 

    private void notifyStateChanged() { 
        // Notify observers of the current game state (current player, pie rule availability, 
        // move count, etc.) so they can update their UI elements accordingly 
        // (e.g. turn indicator, status label, swap button visibility)
        
        if (engine == null) return;
        
        GameState state = engine.getState();
        
        for (GameStateObserver o : observers) {
            o.onStateChanged(
                state.getCurrentPlayer(),
                state.isPieRuleAvailable(),
                state.isInProgress(),
                state.getMoveHistory().size()
            );
        }
    }

    private void notifyMoveExecuted(Position pos, Stone player,
                                    Set<Position> filled, Set<Position> captured) {    
        // Notify observers that a move has been executed, providing the position of the move, the player who made the move,
        // and the sets of positions that were filled and captured as a result of the move, 
        // so that observers can update their UI (e.g. highlights, messages) to reflect the move effects
        for (GameStateObserver o : observers) o.onMoveExecuted(pos, player, filled, captured); 
    }

    private void notifyPieRuleApplied() {
        // Notify observers that the Pie Rule has been applied (players have been swapped) 
        // so they can update their UI accordingly (e.g. turn indicator, board highlights)
        for (GameStateObserver o : observers) o.onPieRuleApplied();
    }

    private void notifyGameOver(Stone winner) {
        // Notify observers that the game is over and provide the winner (if any) 
        // so they can display the final board state and announce the winner or a draw in the UI
        for (GameStateObserver o : observers) o.onGameOver(winner);
    }

    private void notifyMessage(String message) {
        // Notify observers of a generic message (e.g. errors, info) that should be displayed to the user,
        // so they can append it to the log or show it in a dialog as appropriate
        for (GameStateObserver o : observers) o.onMessage(message);
    }
}
