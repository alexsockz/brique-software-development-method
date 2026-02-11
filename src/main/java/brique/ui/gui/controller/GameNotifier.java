package brique.ui.gui.controller;

import brique.core.GameState;
import brique.core.Position;
import brique.core.Stone;
import brique.ui.gui.GameStateObserver;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;


public class GameNotifier {

    private final List<GameStateObserver> observers = new CopyOnWriteArrayList<>();

    public void addObserver(GameStateObserver observer) {
        observers.add(Objects.requireNonNull(observer));
    }

    public void removeObserver(GameStateObserver observer) {
        observers.remove(observer);
    }

    public void notifyGameStarted(int boardSize) {
        for (GameStateObserver o : observers) o.onGameStarted(boardSize);
    }

    public void notifyBoardUpdated() {
        for (GameStateObserver o : observers) o.onBoardUpdated();
    }

    
    public void notifyStateChanged(GameState state) {
        for (GameStateObserver o : observers) {
            o.onStateChanged(
                state.getCurrentPlayer(),
                state.isPieRuleAvailable(),
                state.isInProgress(),
                state.getMoveHistory().size()
            );
        }
    }

    public void notifyMoveExecuted(Position pos, Stone player,
                                   Set<Position> filled, Set<Position> captured) {
        for (GameStateObserver o : observers) o.onMoveExecuted(pos, player, filled, captured);
    }

    public void notifyPieRuleApplied() {
        for (GameStateObserver o : observers) o.onPieRuleApplied();
    }

    public void notifyGameOver(Stone winner) {
        for (GameStateObserver o : observers) o.onGameOver(winner);
    }

    public void notifyMessage(String message) {
        for (GameStateObserver o : observers) o.onMessage(message);
    }
}
