package brique.ui.gui;

import brique.core.GameState;
import brique.core.Position;
import brique.core.Stone;

import java.util.Set;

public interface GameStateObserver {

    default void onGameStarted(int boardSize, GameState state) { }

    default void onBoardUpdated() { }

    default void onStateChanged(Stone currentPlayer, boolean pieRuleAvailable,
                                boolean inProgress, int moveCount) { }

    default void onMoveExecuted(Position position, Stone player,
                                Set<Position> filledPositions,
                                Set<Position> capturedPositions) { }

    default void onPieRuleApplied() { }

    default void onGameOver(Stone winner) { }

    default void onMessage(String message) { }
}
