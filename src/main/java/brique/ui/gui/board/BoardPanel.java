package brique.ui.gui.board;

import brique.core.Board;
import brique.core.GameState;
import brique.core.Position;
import brique.core.Stone;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


public class BoardPanel extends JPanel {

    @FunctionalInterface
    public interface CellClickListener {
        void onCellClicked(int row, int col);
    }

    // --- State ------------------------------------------------

    private transient GameState gameState;
    private int boardSize;
    private transient Position hoveredCell;
    private Stone currentPlayer = Stone.BLACK;
    private transient Position lastMovePosition;
    private final Set<Position> lastFilledPositions   = new HashSet<>();
    private final Set<Position> lastCapturedPositions  = new HashSet<>();
    private final List<CellClickListener> listeners    = new ArrayList<>();

    // --- Delegates --------------------------------------------

    private final transient BoardRenderer renderer;

    public BoardPanel(BoardTheme theme) {
        Objects.requireNonNull(theme);
        this.renderer = new BoardRenderer(theme);
        setBackground(theme.getBackground());
        setPreferredSize(new Dimension(600, 600));
        installMouseHandlers();
    }

    // --- Public API (controller pushes state here) ------------

    public void addCellClickListener(CellClickListener listener) {
        listeners.add(Objects.requireNonNull(listener));
    }

    public void setGameState(GameState state) {
        this.gameState = state;
        this.boardSize = state.getBoard().getSize();
        repaint();
    }

    public void refreshBoard() {
        repaint();
    }

    public void setCurrentPlayer(Stone player) {
        this.currentPlayer = player;
    }

    public void setLastMovePosition(Position pos) {
        this.lastMovePosition = pos;
    }

    public void setHighlightedPositions(Set<Position> filled,
                                        Set<Position> captured) {
        lastFilledPositions.clear();
        lastCapturedPositions.clear();
        if (filled != null)   lastFilledPositions.addAll(filled);
        if (captured != null) lastCapturedPositions.addAll(captured);
        repaint();
    }

    public void clearHighlights() {
        lastFilledPositions.clear();
        lastCapturedPositions.clear();
        lastMovePosition = null;
        repaint();
    }

    // --- Mouse handling ---------------------------------------

    private void installMouseHandlers() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoveredCell = null;
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updateHover(e.getX(), e.getY());
            }
        });
    }

    private void handleClick(int x, int y) {
        Position cell = createTranslator().pixelToCell(x, y);
        if (cell != null) {
            for (CellClickListener l : listeners) {
                l.onCellClicked(cell.getRow(), cell.getCol());
            }
        }
    }

    private void updateHover(int x, int y) {
        Board board = getBoard();
        Position cell = createTranslator().pixelToCell(x, y);
        Position newHover = null;

        if (cell != null && board != null && board.getStone(cell) == Stone.EMPTY) {
            newHover = cell;
        }
        if (!Objects.equals(hoveredCell, newHover)) {
            hoveredCell = newHover;
            repaint();
        }
    }

    // --- Painting (delegates to BoardRenderer) ----------------

    private Board getBoard() {
        return gameState != null ? gameState.getBoard() : null;
    }

    private CoordinateTranslator createTranslator() {
        return new CoordinateTranslator(boardSize, getWidth(), getHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Board board = getBoard();
        if (board == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        CoordinateTranslator translator = createTranslator();
        int cell = translator.getCellSize();
        int ox   = translator.getGridOriginX();
        int oy   = translator.getGridOriginY();

        renderer.drawEdgeIndicators(g2, cell, ox, oy, boardSize);
        renderer.drawGrid(g2, cell, ox, oy, boardSize,
                          hoveredCell, lastFilledPositions, lastCapturedPositions);
        renderer.drawLabels(g2, cell, ox, oy, boardSize);
        renderer.drawStones(g2, cell, ox, oy, board, boardSize, lastMovePosition);
        renderer.drawHoverPreview(g2, cell, ox, oy, board, hoveredCell, currentPlayer);

        g2.dispose();
    }
}
