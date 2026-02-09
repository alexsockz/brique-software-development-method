package brique.ui.gui;

import brique.core.GameState;
import brique.core.Position;
import brique.core.Stone;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.assertj.core.api.Assertions;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BriqueGUITest {

    @BeforeAll
    void makeHeadless() {
        System.setProperty("java.awt.headless", "true");
    }

    private static void flushEdt() {
        try {
            SwingUtilities.invokeAndWait(() -> { /* no-op */ });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void addsObserverOnConstruction() {
        BoardTheme theme = BoardTheme.defaultTheme();
        brique.core.GameEngine engine = new brique.core.GameEngine(5);
        TestGameController controller = new TestGameController(engine);

        BriqueGUI gui = new BriqueGUI(controller, theme);

        Assertions.assertThat(controller.addedObserver).isSameAs(gui);
    }

    @Test
    void boardClickDelegatesSubmitInput() throws Exception {
        BoardTheme theme = BoardTheme.defaultTheme();
        brique.core.GameEngine engine = new brique.core.GameEngine(5);
        TestGameController controller = new TestGameController(engine);
        controller.running = true;
        BriqueGUI gui = new BriqueGUI(controller, theme);

        // Access the private boardPanel field on the GUI
        Field boardField = BriqueGUI.class.getDeclaredField("boardPanel");
        boardField.setAccessible(true);
        BoardPanel board = (BoardPanel) boardField.get(gui);

        // Access the private listeners list on the panel
        Field listenersField = BoardPanel.class.getDeclaredField("listeners");
        listenersField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<Object> listeners = (List<Object>) listenersField.get(board);
        Assertions.assertThat(listeners).isNotEmpty();

        // Retrieve the nested CellClickListener type
        Class<?> listenerType = Class.forName("brique.ui.gui.BoardPanel$CellClickListener");
        Method onCellClicked = listenerType.getMethod("onCellClicked", int.class, int.class);

        // Trigger a click on row 2, column 3
        onCellClicked.invoke(listeners.get(0), 2, 3);

        Assertions.assertThat(controller.submittedInputs)
            .containsExactly("2 3");
    }

    @Test
    void swapButtonWhenEnabledSubmitsSwap() throws Exception {
        BoardTheme theme = BoardTheme.defaultTheme();
        brique.core.GameEngine engine = new brique.core.GameEngine(4);
        TestGameController controller = new TestGameController(engine);
        controller.running = true;
        BriqueGUI gui = new BriqueGUI(controller, theme);

        // Ask the view to update to a state where the pie rule is available
        gui.onStateChanged(Stone.WHITE, true, true, 0);
        flushEdt();

        // Access the swap button and click it
        Field swapField = BriqueGUI.class.getDeclaredField("swapButton");
        swapField.setAccessible(true);
        JButton swap = (JButton) swapField.get(gui);

        // Sanity: the button should be visible and enabled for White's turn
        Assertions.assertThat(swap.isVisible()).isTrue();
        Assertions.assertThat(swap.isEnabled()).isTrue();

        swap.doClick();

        Assertions.assertThat(controller.submittedInputs)
            .containsExactly("swap");
    }

    @Test
    void newGameButtonStopsAndStartsGame() throws Exception {
        BoardTheme theme = BoardTheme.defaultTheme();
        brique.core.GameEngine engine = new brique.core.GameEngine(3);
        TestGameController controller = new TestGameController(engine);
        controller.running = true;

        // Override the prompt to bypass the JOptionPane
        class TestGUI extends BriqueGUI {
            TestGUI(GameController ctrl, BoardTheme thm) {
                super(ctrl, thm);
            }
            @Override
            public void promptAndStartGame() {
                // Simulate user entering 7
                controller.startNewGame(7);
            }
        }

        TestGUI gui = new TestGUI(controller, theme);
        // Fetch the new game button via reflection
        Field newField = BriqueGUI.class.getDeclaredField("newGameButton");
        newField.setAccessible(true);
        JButton newGame = (JButton) newField.get(gui);
        newGame.doClick();

        Assertions.assertThat(controller.stopCalled).isTrue();
        Assertions.assertThat(controller.startNewGameSize).isEqualTo(7);
    }

    @Test
    void onGameStartedConfiguresBoardPanelAndLogs() throws Exception {
        BoardTheme theme = BoardTheme.defaultTheme();
        brique.core.GameEngine engine = new brique.core.GameEngine(6);
        TestGameController controller = new TestGameController(engine);
        controller.running = true;
        BriqueGUI gui = new BriqueGUI(controller, theme);

        // Fire the callback with a custom board size
        gui.onGameStarted(6);
        flushEdt();

        // Inspect the boardPanel's cached board size
        Field boardField = BriqueGUI.class.getDeclaredField("boardPanel");
        boardField.setAccessible(true);
        BoardPanel panel = (BoardPanel) boardField.get(gui);
        Field sizeField = BoardPanel.class.getDeclaredField("boardSize");
        sizeField.setAccessible(true);
        int sz = sizeField.getInt(panel);
        Assertions.assertThat(sz).isEqualTo(6);

        // Inspect the log area for the new game header
        Field logField = BriqueGUI.class.getDeclaredField("logArea");
        logField.setAccessible(true);
        JTextArea log = (JTextArea) logField.get(gui);
        Assertions.assertThat(log.getText()).contains("New Game (6×6)");
    }

    @Test
    void onStateChangedUpdatesLabelsAndButtons() throws Exception {
        BoardTheme theme = BoardTheme.defaultTheme();
        brique.core.GameEngine engine = new brique.core.GameEngine(5);
        TestGameController controller = new TestGameController(engine);
        controller.running = true;
        BriqueGUI gui = new BriqueGUI(controller, theme);

        gui.onStateChanged(Stone.BLACK, true, true, 2);
        flushEdt();

        // Turn indicator should display BLACK's turn
        Field turnField = BriqueGUI.class.getDeclaredField("turnIndicator");
        turnField.setAccessible(true);
        JLabel turn = (JLabel) turnField.get(gui);
        Assertions.assertThat(turn.getText()).isEqualTo("BLACK's turn");

        // Stone preview should reflect the current player
        Field previewField = BriqueGUI.class.getDeclaredField("stonePreview");
        previewField.setAccessible(true);
        StonePreviewPanel preview = (StonePreviewPanel) previewField.get(gui);
        Field cpPreviewField = StonePreviewPanel.class.getDeclaredField("currentPlayer");
        cpPreviewField.setAccessible(true);
        Assertions.assertThat(cpPreviewField.get(preview)).isEqualTo(Stone.BLACK);

        // Board panel current player should also be updated
        Field boardField = BriqueGUI.class.getDeclaredField("boardPanel");
        boardField.setAccessible(true);
        BoardPanel panel = (BoardPanel) boardField.get(gui);
        Field cpField = BoardPanel.class.getDeclaredField("currentPlayer");
        cpField.setAccessible(true);
        Stone cp = (Stone) cpField.get(panel);
        Assertions.assertThat(cp).isEqualTo(Stone.BLACK);

        // Swap button should be hidden for BLACK's turn
        Field swapField = BriqueGUI.class.getDeclaredField("swapButton");
        swapField.setAccessible(true);
        JButton swap = (JButton) swapField.get(gui);
        Assertions.assertThat(swap.isVisible()).isFalse();
        Assertions.assertThat(swap.isEnabled()).isFalse();
    }

    @Test
    void onMoveExecutedSetsHighlights() throws Exception {
        BoardTheme theme = BoardTheme.defaultTheme();
        brique.core.GameEngine engine = new brique.core.GameEngine(5);
        TestGameController controller = new TestGameController(engine);
        controller.running = true;
        BriqueGUI gui = new BriqueGUI(controller, theme);

        Position movePos = new Position(1, 1);
        Set<Position> filled = new HashSet<>();
        filled.add(new Position(0, 0));
        Set<Position> captured = new HashSet<>();
        captured.add(new Position(2, 2));

        gui.onMoveExecuted(movePos, Stone.BLACK, filled, captured);
        flushEdt();

        Field boardField = BriqueGUI.class.getDeclaredField("boardPanel");
        boardField.setAccessible(true);
        BoardPanel panel = (BoardPanel) boardField.get(gui);

        // Check last move position
        Field lastPosField = BoardPanel.class.getDeclaredField("lastMovePosition");
        lastPosField.setAccessible(true);
        Position lastPos = (Position) lastPosField.get(panel);
        Assertions.assertThat(lastPos.getRow()).isEqualTo(1);
        Assertions.assertThat(lastPos.getCol()).isEqualTo(1);

        // Check filled positions
        Field filledField = BoardPanel.class.getDeclaredField("lastFilledPositions");
        filledField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Set<Position> fp = (Set<Position>) filledField.get(panel);
        Assertions.assertThat(fp).containsExactly(new Position(0, 0));

        // Check captured positions
        Field capField = BoardPanel.class.getDeclaredField("lastCapturedPositions");
        capField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Set<Position> cp = (Set<Position>) capField.get(panel);
        Assertions.assertThat(cp).containsExactly(new Position(2, 2));
    }

    @Test
    void onPieRuleAppliedClearsHighlights() throws Exception {
        BoardTheme theme = BoardTheme.defaultTheme();
        brique.core.GameEngine engine = new brique.core.GameEngine(5);
        TestGameController controller = new TestGameController(engine);
        controller.running = true;
        BriqueGUI gui = new BriqueGUI(controller, theme);

        // Prepopulate some highlight state
        gui.onMoveExecuted(new Position(0, 0), Stone.BLACK,
                           Set.of(new Position(1, 1)),
                           Set.of(new Position(2, 2)));
        flushEdt();

        gui.onPieRuleApplied();
        flushEdt();

        Field boardField = BriqueGUI.class.getDeclaredField("boardPanel");
        boardField.setAccessible(true);
        BoardPanel panel = (BoardPanel) boardField.get(gui);

        Field lastPosField = BoardPanel.class.getDeclaredField("lastMovePosition");
        lastPosField.setAccessible(true);
        Position lastPos = (Position) lastPosField.get(panel);
        Assertions.assertThat(lastPos).isNull();

        Field filledField = BoardPanel.class.getDeclaredField("lastFilledPositions");
        filledField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Set<Position> fp = (Set<Position>) filledField.get(panel);
        Assertions.assertThat(fp).isEmpty();

        Field capField = BoardPanel.class.getDeclaredField("lastCapturedPositions");
        capField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Set<Position> cp = (Set<Position>) capField.get(panel);
        Assertions.assertThat(cp).isEmpty();
    }

    @Test
    void onGameOverDisplaysWinnerMessage() throws Exception {
        BoardTheme theme = BoardTheme.defaultTheme();
        brique.core.GameEngine engine = new brique.core.GameEngine(5);
        TestGameController controller = new TestGameController(engine);
        controller.running = true;
        BriqueGUI gui = new BriqueGUI(controller, theme);

        gui.onGameOver(Stone.BLACK);
        flushEdt();

        Field statusField = BriqueGUI.class.getDeclaredField("statusLabel");
        statusField.setAccessible(true);
        JLabel status = (JLabel) statusField.get(gui);
        Assertions.assertThat(status.getText()).contains("BLACK wins");

        Field logField = BriqueGUI.class.getDeclaredField("logArea");
        logField.setAccessible(true);
        JTextArea log = (JTextArea) logField.get(gui);
        Assertions.assertThat(log.getText()).contains("Game Over");
    }

    @Test
    void onMessageAppendsToLog() throws Exception {
        BoardTheme theme = BoardTheme.defaultTheme();
        brique.core.GameEngine engine = new brique.core.GameEngine(5);
        TestGameController controller = new TestGameController(engine);
        controller.running = true;
        BriqueGUI gui = new BriqueGUI(controller, theme);

        gui.onMessage("Hello testers!");
        flushEdt();

        Field logField = BriqueGUI.class.getDeclaredField("logArea");
        logField.setAccessible(true);
        JTextArea log = (JTextArea) logField.get(gui);
        Assertions.assertThat(log.getText()).contains("Hello testers!");
    }
}

/* ------------------------------------------------------------------------
 * Test support classes
 *
 * The following package-private classes reside in the same package as
 * {@link BriqueGUI} to satisfy unresolved dependencies at compile time.
 * They implement only the minimal API required for the GUI to
 * function in isolation during testing.  In particular the
 * {@link UIComponentFactory} returns simple Swing widgets and the
 * {@link StonePreviewPanel} records the current player for
 * verification.
 */

// Removed: using the real GameStateObserver from production code

class TestGameController extends GameController {
    private final brique.core.GameEngine testEngine;
    GameStateObserver addedObserver;
    final List<String> submittedInputs = new ArrayList<>();
    boolean running = false;
    boolean stopCalled = false;
    int startNewGameSize = -1;

    TestGameController(brique.core.GameEngine engine) {
        this.testEngine = engine;
    }

    @Override
    public void addObserver(GameStateObserver observer) {
        this.addedObserver = observer;
        super.addObserver(observer);
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void submitInput(String input) {
        submittedInputs.add(input);
    }

    @Override
    public void stopGame() {
        stopCalled = true;
        running = false;
    }

    @Override
    public void startNewGame(int size) {
        startNewGameSize = size;
        running = true;
    }

    @Override
    public brique.core.GameEngine getEngine() {
        return testEngine;
    }
}