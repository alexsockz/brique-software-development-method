package brique.ui.gui;

import brique.core.GameState;
import brique.core.Position;
import brique.core.Stone;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

class BoardPanelTest {

    private static final int PANEL_SIZE = 600;
    private static final int MARGIN = 40;
    private static final int LABEL_MARGIN = 25;

    @Nested
    @DisplayName("Coordinate Translation Tests")
    class CoordinateTranslationTests {
        @Test
        @DisplayName("Should fire click listener with correct row and column")
        void shouldFireClickListenerWithCorrectCell() {
            BoardTheme theme = BoardTheme.defaultTheme();
            BoardPanel panel = new BoardPanel(theme);
            panel.setSize(PANEL_SIZE, PANEL_SIZE);
            GameState state = new GameState(5);
            panel.setGameState(state);

            AtomicInteger clickedRow = new AtomicInteger(-1);
            AtomicInteger clickedCol = new AtomicInteger(-1);
            panel.addCellClickListener((r, c) -> {
                clickedRow.set(r);
                clickedCol.set(c);
            });

            int boardSize = state.getBoard().getSize();
            int w = PANEL_SIZE - 2 * MARGIN - LABEL_MARGIN;
            int h = PANEL_SIZE - 2 * MARGIN - LABEL_MARGIN;
            int cellSize = Math.max(20, Math.min(w / boardSize, h / boardSize));
            int ox = (PANEL_SIZE - cellSize * boardSize) / 2 + LABEL_MARGIN / 2;
            int oy = (PANEL_SIZE - cellSize * boardSize) / 2 + LABEL_MARGIN / 2;

            // Choose a cell to click: row 2, column 3
            int targetRow = 2;
            int targetCol = 3;
            int clickX = ox + targetCol * cellSize + cellSize / 2;
            int clickY = oy + targetRow * cellSize + cellSize / 2;

            // Dispatch a synthetic mouse click on the panel
            MouseListener listener = panel.getMouseListeners()[0];
            MouseEvent event = new MouseEvent(panel, MouseEvent.MOUSE_CLICKED,
                System.currentTimeMillis(), 0, clickX, clickY, 1, false);
            listener.mouseClicked(event);

            assertThat(clickedRow.get()).isEqualTo(targetRow);
            assertThat(clickedCol.get()).isEqualTo(targetCol);
        }

        @Test
        @DisplayName("Should ignore clicks outside the board grid")
        void shouldIgnoreClicksOutsideBoard() {
            BoardTheme theme = BoardTheme.defaultTheme();
            BoardPanel panel = new BoardPanel(theme);
            panel.setSize(PANEL_SIZE, PANEL_SIZE);
            GameState state = new GameState(5);
            panel.setGameState(state);

            AtomicBoolean called = new AtomicBoolean(false);
            panel.addCellClickListener((r, c) -> called.set(true));

            // Coordinates well outside the board area
            int outsideX = 0;
            int outsideY = 0;
            MouseListener listener = panel.getMouseListeners()[0];
            MouseEvent event = new MouseEvent(panel, MouseEvent.MOUSE_CLICKED,
                System.currentTimeMillis(), 0, outsideX, outsideY, 1, false);
            listener.mouseClicked(event);

            assertThat(called.get()).isFalse();
        }
    }

    @Nested
    @DisplayName("Game State Integration Tests")
    class GameStateIntegrationTests {
        @Test
        @DisplayName("Should adapt coordinate mapping when game state changes board size")
        void shouldAdaptCoordinateMappingOnGameStateChange() {
            BoardTheme theme = BoardTheme.defaultTheme();
            BoardPanel panel = new BoardPanel(theme);
            panel.setSize(PANEL_SIZE, PANEL_SIZE);

            // First bind to a larger board
            GameState state5 = new GameState(5);
            panel.setGameState(state5);
            // Click near the centre: should map to some cell on 5x5 board
            AtomicInteger r1 = new AtomicInteger(-1);
            AtomicInteger c1 = new AtomicInteger(-1);
            panel.addCellClickListener((r, c) -> {
                r1.set(r);
                c1.set(c);
            });
            int boardSize5 = state5.getBoard().getSize();
            int cell5 = Math.max(20, Math.min((PANEL_SIZE - 2 * MARGIN - LABEL_MARGIN) / boardSize5,
                                              (PANEL_SIZE - 2 * MARGIN - LABEL_MARGIN) / boardSize5));
            int ox5 = (PANEL_SIZE - cell5 * boardSize5) / 2 + LABEL_MARGIN / 2;
            int oy5 = (PANEL_SIZE - cell5 * boardSize5) / 2 + LABEL_MARGIN / 2;
            int clickX5 = ox5 + 2 * cell5 + cell5 / 2;
            int clickY5 = oy5 + 1 * cell5 + cell5 / 2;
            MouseListener listener = panel.getMouseListeners()[0];
            MouseEvent event5 = new MouseEvent(panel, MouseEvent.MOUSE_CLICKED,
                System.currentTimeMillis(), 0, clickX5, clickY5, 1, false);
            listener.mouseClicked(event5);
            assertThat(r1.get()).isEqualTo(1);
            assertThat(c1.get()).isEqualTo(2);

            // Now rebind to a smaller board and repeat
            GameState state3 = new GameState(3);
            panel.setGameState(state3);
            AtomicInteger r2 = new AtomicInteger(-1);
            AtomicInteger c2 = new AtomicInteger(-1);
            panel.addCellClickListener((r, c) -> {
                r2.set(r);
                c2.set(c);
            });
            int boardSize3 = state3.getBoard().getSize();
            int cell3 = Math.max(20, Math.min((PANEL_SIZE - 2 * MARGIN - LABEL_MARGIN) / boardSize3,
                                              (PANEL_SIZE - 2 * MARGIN - LABEL_MARGIN) / boardSize3));
            int ox3 = (PANEL_SIZE - cell3 * boardSize3) / 2 + LABEL_MARGIN / 2;
            int oy3 = (PANEL_SIZE - cell3 * boardSize3) / 2 + LABEL_MARGIN / 2;
            int clickX3 = ox3 + 1 * cell3 + cell3 / 2;
            int clickY3 = oy3 + 2 * cell3 + cell3 / 2;
            MouseEvent event3 = new MouseEvent(panel, MouseEvent.MOUSE_CLICKED,
                System.currentTimeMillis(), 0, clickX3, clickY3, 1, false);
            listener.mouseClicked(event3);
            assertThat(r2.get()).isEqualTo(2);
            assertThat(c2.get()).isEqualTo(1);
        }
    }
}