package brique.ui.gui.controller;

import brique.core.GameEngine;
import brique.core.GameEngineFactory;
import brique.core.GameMode;
import brique.exceptions.ActionInputException;
import brique.ui.gui.GameStateObserver;

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
    private final BlockingQueue<ActionCommand> inputQueue = new LinkedBlockingQueue<>(); // Queue for receiving user input from the GUI thread
    private Thread gameThread; // Background thread that runs the game loop
    private GameLoop gameLoop;

    public void setGameMode(GameMode mode) { this.currentMode = mode; }
    public GameMode getGameMode() { return currentMode; }

    // --- Observer management (delegated to GameNotifier) ------

    public void addObserver(GameStateObserver observer)    { notifier.addObserver(observer); }
    public void removeObserver(GameStateObserver observer)  { notifier.removeObserver(observer); }

    // --- Input submission -------------------------------------

    // Thread-safe method for submitting user input (cell clicks, "swap", "quit") from the UI thread.
    public void submitCommand(ActionCommand command) {
        if (!inputQueue.offer(command)) {
            throw new ActionInputException("input queue error");
        }
    }

    // --- Game lifecycle ---------------------------------------

    public void startNewGame(int boardSize) {
        stopGame(); // Ensure any existing game is stopped before starting a new one
        engine = GameEngineFactory.create(currentMode, boardSize); // Create a new game engine with the specified board size and depending on the gameplay mode selected
        inputQueue.clear(); // Clear any pending input from previous games

        gameLoop = new GameLoop(engine, inputQueue, notifier);

        notifier.notifyGameStarted(boardSize, engine.getState());
        notifier.notifyStateChanged(engine.getState());

        gameThread = new Thread(gameLoop, "BriqueGameThread"); // Create a new thread to run the game loop
        gameThread.setDaemon(true); // Set as daemon so it doesn't prevent the application from exiting
        gameThread.start(); // Start the game loop thread
    }

    public void stopGame() {
        if (gameLoop == null || !gameLoop.isRunning()) return;
        gameLoop.stop();
        if(!inputQueue.offer(ActionCommand.Quit.INSTANCE)){throw new ActionInputException("quit action failed");} // unblock

        if (gameThread != null) { // Interrupt the game thread to ensure it stops promptly
            gameThread.interrupt();
            try { gameThread.join(500); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    // Returns whether a game is currently running. Useful for the GUI to enable/disable controls accordingly.
    public boolean isRunning() {
        return gameLoop != null && gameLoop.isRunning();
    }

    public GameEngine getEngine() { return engine; }
}