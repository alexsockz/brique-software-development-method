package brique.ui.gui.controller;

import brique.core.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

// Runs the game loop on a background thread.
// Owns the blocking queue and command dispatch — nothing else.
public class GameLoop implements Runnable {

    private final GameEngine engine;
    private final BlockingQueue<ActionCommand> inputQueue;
    private final GameNotifier notifier;
    private volatile boolean running = true;

    public GameLoop(GameEngine engine,
                    BlockingQueue<ActionCommand> inputQueue,
                    GameNotifier notifier) {
        this.engine     = engine;
        this.inputQueue = inputQueue;
        this.notifier   = notifier;
    }

    public void stop() { running = false; }
    public boolean isRunning() { return running; }

    @Override
    public void run() {
        while (running && !engine.isGameOver()) {
            notifier.notifyStateChanged(engine.getState());

            ActionCommand command;
            try {
                command = inputQueue.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            if (!running) break;

            processCommand(command);
        }

        running = false;
        if (engine != null) {
            notifier.notifyGameOver(engine.getState().getWinner());
        }
    }

    private void processCommand(ActionCommand cmd) {
        if (cmd == null) {
            notifier.notifyMessage("Invalid input.");
            return;
        }

        if (cmd instanceof ActionCommand.Quit) {
            engine.getState().abort();
            notifier.notifyMessage("Game aborted.");
            running = false;
        } else if (cmd instanceof ActionCommand.Swap) {
            handleSwap();
        } else if (cmd instanceof ActionCommand.PlaceStone place) {
            handlePlaceStone(place);
        }
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
            Position pos    = cmd.getPosition();
            Stone    player = engine.getState().getCurrentPlayer();
            boolean  ok     = engine.playMove(pos);

            if (!ok) {
                notifier.notifyMessage(
                    "Invalid move at (" + cmd.getRow() + ", " + cmd.getCol() + "). Try again.");
            } else {
                notifier.notifyMessage(
                    player + " placed at (" + cmd.getRow() + ", " + cmd.getCol() + ")");
                reportMoveEffects(pos, player);
                notifier.notifyBoardUpdated();
            }
        } catch (IllegalStateException e) {
            notifier.notifyMessage("Error: " + e.getMessage());
            running = false;
        }
    }

    private void reportMoveEffects(Position pos, Stone player) {
        List<Move> history = engine.getState().getMoveHistory();
        if (history.isEmpty()) return;

        Move lastMove = history.get(history.size() - 1);
        Set<Position> filled   = new HashSet<>(lastMove.getFilledPositions());
        Set<Position> captured = new HashSet<>(lastMove.getCapturedPositions());

        notifier.notifyMoveExecuted(pos, player, filled, captured);
        if (!filled.isEmpty())
            notifier.notifyMessage("  \u2192 Escort fill: " + filled.size() + " cell(s)");
        if (!captured.isEmpty())
            notifier.notifyMessage("  \u2192 Captured: " + captured.size() + " opponent stone(s)!");
    }
}