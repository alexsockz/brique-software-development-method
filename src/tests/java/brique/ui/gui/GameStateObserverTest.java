package brique.ui.gui;

import brique.core.Position;
import brique.core.Stone;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public class GameStateObserverTest {

    @Test
    void defaultMethodsDoNotThrow() {
        GameStateObserver obs = new GameStateObserver() { };

        // Dummy arguments
        Position pos = new Position(0, 0);
        Set<Position> set = new HashSet<>();
        // Invoke every callback; no assertions needed beyond no exception
        obs.onGameStarted(5);
        obs.onBoardUpdated();
        obs.onStateChanged(Stone.BLACK, true, true, 0);
        obs.onMoveExecuted(pos, Stone.WHITE, set, set);
        obs.onPieRuleApplied();
        obs.onGameOver(Stone.EMPTY);
        obs.onMessage("Test message");
    }

    @Test
    void overriddenCallbacksAreInvoked() {
        class CountingObserver implements GameStateObserver {
            int started = 0;
            int updated = 0;
            int stateChanged = 0;
            int moveExecuted = 0;
            int pieApplied = 0;
            int gameOver = 0;
            int messaged = 0;
            @Override
            public void onGameStarted(int boardSize) { started++; }
            @Override
            public void onBoardUpdated() { updated++; }
            @Override
            public void onStateChanged(Stone currentPlayer, boolean pieRuleAvailable,
                                      boolean inProgress, int moveCount) { stateChanged++; }
            @Override
            public void onMoveExecuted(Position position, Stone player, Set<Position> filled,
                                      Set<Position> captured) { moveExecuted++; }
            @Override
            public void onPieRuleApplied() { pieApplied++; }
            @Override
            public void onGameOver(Stone winner) { gameOver++; }
            @Override
            public void onMessage(String message) { messaged++; }
        }
        CountingObserver obs = new CountingObserver();
        Position p = new Position(1, 1);
        Set<Position> empty = new HashSet<>();
        obs.onGameStarted(3);
        obs.onBoardUpdated();
        obs.onStateChanged(Stone.WHITE, false, true, 1);
        obs.onMoveExecuted(p, Stone.BLACK, empty, empty);
        obs.onPieRuleApplied();
        obs.onGameOver(Stone.BLACK);
        obs.onMessage("done");
        Assertions.assertThat(obs.started).isEqualTo(1);
        Assertions.assertThat(obs.updated).isEqualTo(1);
        Assertions.assertThat(obs.stateChanged).isEqualTo(1);
        Assertions.assertThat(obs.moveExecuted).isEqualTo(1);
        Assertions.assertThat(obs.pieApplied).isEqualTo(1);
        Assertions.assertThat(obs.gameOver).isEqualTo(1);
        Assertions.assertThat(obs.messaged).isEqualTo(1);
    }

    @Test
    void interfaceDefinesAllCallbacksWithDefaults() throws Exception {
        Method[] declared = GameStateObserver.class.getDeclaredMethods();
        // There should be at least seven declared methods (compiler may add synthetic ones)
        Assertions.assertThat(declared.length).isGreaterThanOrEqualTo(7);
        // None of the non-synthetic methods should be abstract (default methods are concrete)
        for (Method m : declared) {
            if (!m.isSynthetic()) {
                Assertions.assertThat(Modifier.isAbstract(m.getModifiers()))
                          .as("Method %s should not be abstract", m.getName())
                          .isFalse();
            }
        }
        // Verify the signature of onStateChanged
        Method stateChanged = GameStateObserver.class.getDeclaredMethod("onStateChanged",
            Stone.class, boolean.class, boolean.class, int.class);
        Class<?>[] params = stateChanged.getParameterTypes();
        Assertions.assertThat(params[0]).isEqualTo(Stone.class);
        Assertions.assertThat(params[1]).isEqualTo(boolean.class);
        Assertions.assertThat(params[2]).isEqualTo(boolean.class);
        Assertions.assertThat(params[3]).isEqualTo(int.class);
    }
}