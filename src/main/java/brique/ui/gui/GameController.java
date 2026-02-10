package brique.ui.gui;

import brique.core.GameEngine;
import brique.core.GameEngineFactory;
import brique.core.GameMode;
import brique.core.GameState;
import brique.core.LocalGameEngine;
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

public class GameController {

    private GameEngine engine;
    private GameMode currentMode = GameMode.LOCAL_1V1;
    private final List<GameStateObserver> observers = new CopyOnWriteArrayList<>();
    private final BlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();
    private Thread gameThread;
    private volatile boolean running;

    public void setGameMode(GameMode mode) { this.currentMode = mode; }

    public GameMode getGameMode() { return currentMode; }

    // --- Observer management ----------------------------------

    public void addObserver(GameStateObserver observer) {
        observers.add(Objects.requireNonNull(observer));
    }

    public void removeObserver(GameStateObserver observer) {
        observers.remove(observer);
    }

    // --- Input submission -------------------------------------

    public void submitInput(String input) {
        inputQueue.offer(input);
    }

    // --- Game lifecycle ---------------------------------------

    public void startNewGame(int boardSize) {
        stopGame();
        engine = GameEngineFactory.create(currentMode, boardSize);
        running = true;
        inputQueue.clear();

        notifyGameStarted(boardSize);
        notifyStateChanged();

        gameThread = new Thread(this::gameLoop, "BriqueGameThread");
        gameThread.setDaemon(true);
        gameThread.start();
    }

    public void stopGame() {
        if (!running) return;
        running = false;
        inputQueue.offer("quit"); // unblock the reading thread
        if (gameThread != null) {
            gameThread.interrupt();
            try {
                gameThread.join(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public GameEngine getEngine() {
        return engine;
    }

    // --- Game loop (runs on background thread) ----------------

    private void gameLoop() {
        while (running && !engine.isGameOver()) {
            notifyStateChanged();

            String input;
            try {
                input = inputQueue.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            if (!running) break;

            processInput(input.trim());
        }

        running = false;
        if (engine != null) {
            notifyGameOver(engine.getState().getWinner());
        }
    }

    // --- Input processing (Strategy-like dispatch) ------------

    private void processInput(String input) {
        if (input.equalsIgnoreCase("quit")) {
            handleQuit();
        } else if (input.equalsIgnoreCase("swap")) {
            handleSwap();
        } else {
            handleMove(input);
        }
    }

    private void handleQuit() {
        engine.getState().abort();
        notifyMessage("Game aborted.");
        running = false;
    }

    private void handleSwap() {
        try {
            engine.getState().applyPieRule();
            notifyMessage("\u21C4 Pie rule applied! Colors swapped.");
            notifyPieRuleApplied();
            notifyBoardUpdated();
        } catch (Exception e) {
            notifyMessage("Cannot swap: " + e.getMessage());
        }
    }

    private void handleMove(String input) {
        String[] parts = input.split("\\s+");
        if (parts.length != 2) {
            notifyMessage("Invalid input.");
            return;
        }

        try {
            int row = Integer.parseInt(parts[0]);
            int col = Integer.parseInt(parts[1]);
            Position pos = new Position(row, col);
            Stone player = engine.getState().getCurrentPlayer();

            boolean success = engine.playMove(pos);
            if (!success) {
                notifyMessage("Invalid move at (" + row + ", " + col + "). Try again.");
            } else {
                notifyMessage(player + " placed at (" + row + ", " + col + ")");
                reportMoveEffects(pos, player);
                notifyBoardUpdated();
            }
        } catch (NumberFormatException e) {
            notifyMessage("Invalid coordinates. Click on a cell to play.");
        } catch (IllegalStateException e) {
            notifyMessage("Error: " + e.getMessage());
            running = false;
        }
    }

    private void reportMoveEffects(Position pos, Stone player) {
        List<Move> history = engine.getState().getMoveHistory();
        if (history.isEmpty()) return;

        Move lastMove = history.get(history.size() - 1);
        Set<Position> filled  = new HashSet<>(lastMove.getFilledPositions());
        Set<Position> captured = new HashSet<>(lastMove.getCapturedPositions());

        notifyMoveExecuted(pos, player, filled, captured);

        if (!filled.isEmpty()) {
            notifyMessage("  \u2192 Escort fill: " + filled.size() + " cell(s)");
        }
        if (!captured.isEmpty()) {
            notifyMessage("  \u2192 Captured: " + captured.size() + " opponent stone(s)!");
        }
    }

    // --- Observer notification helpers ------------------------

    private void notifyGameStarted(int boardSize) {
        for (GameStateObserver o : observers) o.onGameStarted(boardSize);
    }

    private void notifyBoardUpdated() {
        for (GameStateObserver o : observers) o.onBoardUpdated();
    }

    private void notifyStateChanged() {
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
        for (GameStateObserver o : observers) o.onMoveExecuted(pos, player, filled, captured);
    }

    private void notifyPieRuleApplied() {
        for (GameStateObserver o : observers) o.onPieRuleApplied();
    }

    private void notifyGameOver(Stone winner) {
        for (GameStateObserver o : observers) o.onGameOver(winner);
    }

    private void notifyMessage(String message) {
        for (GameStateObserver o : observers) o.onMessage(message);
    }
}
