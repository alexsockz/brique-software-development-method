package brique.ui.gui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.assertj.core.api.Assertions;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class GameControllerTest {

    private GameController controller;

    @BeforeEach
    void setUp() {
        controller = new GameController();
    }

    private static class DummyObserver implements GameStateObserver {
        // no-op implementation; tests never invoke callbacks directly
    }

    @Test
    void addObserverStoresObserver() throws Exception {
        DummyObserver obs = new DummyObserver();
        controller.addObserver(obs);

        Field observersField = GameController.class.getDeclaredField("observers");
        observersField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<GameStateObserver> observers = (List<GameStateObserver>) observersField.get(controller);
        Assertions.assertThat(observers).contains(obs);
    }

    @Test
    void removeObserverEliminatesObserver() throws Exception {
        DummyObserver obs = new DummyObserver();
        controller.addObserver(obs);
        controller.removeObserver(obs);

        Field observersField = GameController.class.getDeclaredField("observers");
        observersField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<GameStateObserver> observers = (List<GameStateObserver>) observersField.get(controller);
        Assertions.assertThat(observers).doesNotContain(obs);

        // Removing an observer that was never added should not throw
        controller.removeObserver(new DummyObserver());
        Assertions.assertThat(observers).isEmpty();
    }

    @Test
    void submitInputQueuesCommands() throws Exception {
        controller.submitInput("first");
        controller.submitInput("second");

        Field queueField = GameController.class.getDeclaredField("inputQueue");
        queueField.setAccessible(true);
        @SuppressWarnings("unchecked")
        BlockingQueue<String> queue = (BlockingQueue<String>) queueField.get(controller);
        String first = queue.poll(50, TimeUnit.MILLISECONDS);
        String second = queue.poll(50, TimeUnit.MILLISECONDS);
        Assertions.assertThat(first).isEqualTo("first");
        Assertions.assertThat(second).isEqualTo("second");
    }

    @Test
    void initialStateHasNoEngineAndNotRunning() {
        Assertions.assertThat(controller.isRunning()).isFalse();
        Assertions.assertThat(controller.getEngine()).isNull();
    }

    @Test
    void stopGameWhenNotRunningDoesNothing() throws Exception {
        // Ensure not running
        Assertions.assertThat(controller.isRunning()).isFalse();
        controller.stopGame();

        // The running flag should remain false
        Assertions.assertThat(controller.isRunning()).isFalse();

        // The input queue should remain empty
        Field queueField = GameController.class.getDeclaredField("inputQueue");
        queueField.setAccessible(true);
        @SuppressWarnings("unchecked")
        BlockingQueue<String> queue = (BlockingQueue<String>) queueField.get(controller);
        Assertions.assertThat(queue).isEmpty();
    }

    @Test
    void stopGameWhenRunningEnqueuesQuit() throws Exception {
        // Set the running flag directly via reflection
        Field runningField = GameController.class.getDeclaredField("running");
        runningField.setAccessible(true);
        runningField.setBoolean(controller, true);

        controller.stopGame();

        Assertions.assertThat(controller.isRunning()).isFalse();

        Field queueField = GameController.class.getDeclaredField("inputQueue");
        queueField.setAccessible(true);
        @SuppressWarnings("unchecked")
        BlockingQueue<String> queue = (BlockingQueue<String>) queueField.get(controller);
        String quit = queue.poll(50, TimeUnit.MILLISECONDS);
        Assertions.assertThat(quit).isEqualTo("quit");
        Assertions.assertThat(queue).isEmpty();
    }
}

/* ------------------------------------------------------------------------
 * Test support definitions
 *
 * A minimal observer interface is defined here because the production
 * implementation of GameStateObserver is not available in this test
 * compilation unit.  Only the methods required for type compatibility
 * with {@link GameController#addObserver} and
 * {@link GameController#removeObserver} are declared.  Each method
 * carries a default no-op implementation so test cases need not
 * implement all observer callbacks.
 */

interface GameStateObserver {
    default void onGameStarted(int boardSize) {}
    default void onBoardUpdated() {}
    default void onStateChanged(Object currentPlayer, boolean pieRuleAvailable,
                                boolean inProgress, int moveCount) {}
    default void onMoveExecuted(Object pos, Object player,
                                java.util.Set<Object> filled,
                                java.util.Set<Object> captured) {}
    default void onPieRuleApplied() {}
    default void onGameOver(Object winner) {}
    default void onMessage(String message) {}
}