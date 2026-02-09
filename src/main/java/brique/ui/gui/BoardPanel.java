package brique.ui.gui;

import brique.core.Board;
import brique.core.GameState;
import brique.core.Position;
import brique.core.Stone;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
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

    private GameState gameState;
    private int boardSize;
    private Position hoveredCell;
    private Stone currentPlayer = Stone.BLACK;
    private Position lastMovePosition;
    private final Set<Position> lastFilledPositions  = new HashSet<>();
    private final Set<Position> lastCapturedPositions = new HashSet<>();
    private final List<CellClickListener> listeners = new ArrayList<>();

    private final BoardTheme theme;

    private static final int MARGIN       = 40;
    private static final int LABEL_MARGIN = 25;

    public BoardPanel(BoardTheme theme) {
        this.theme = Objects.requireNonNull(theme);
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

    // --- Mouse handling (coordinate translation only) ---------

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
        Position cell = pixelToCell(x, y);
        if (cell != null) {
            for (CellClickListener l : listeners) {
                l.onCellClicked(cell.getRow(), cell.getCol());
            }
        }
    }

    private void updateHover(int x, int y) {
        Board board = getBoard();
        Position cell = pixelToCell(x, y);
        Position newHover = null;
        if (cell != null && board != null && board.getStone(cell) == Stone.EMPTY) {
            newHover = cell;
        }
        if (!Objects.equals(hoveredCell, newHover)) {
            hoveredCell = newHover;
            repaint();
        }
    }

    private Position pixelToCell(int x, int y) {
        if (gameState == null) return null;
        int cellSize = getCellSize();
        int ox = getGridOriginX();
        int oy = getGridOriginY();
        if (x < ox || y < oy) return null;
        int col = (x - ox) / cellSize;
        int row = (y - oy) / cellSize;
        if (row >= 0 && row < boardSize && col >= 0 && col < boardSize) {
            return new Position(row, col);
        }
        return null;
    }

    // --- Layout calculations ----------------------------------

    private int getCellSize() {
        int w = getWidth()  - 2 * MARGIN - LABEL_MARGIN;
        int h = getHeight() - 2 * MARGIN - LABEL_MARGIN;
        if (boardSize <= 0) return 40;
        return Math.max(20, Math.min(w / boardSize, h / boardSize));
    }

    private int getGridOriginX() {
        return (getWidth() - getCellSize() * boardSize) / 2 + LABEL_MARGIN / 2;
    }

    private int getGridOriginY() {
        return (getHeight() - getCellSize() * boardSize) / 2 + LABEL_MARGIN / 2;
    }

    // --- Painting ---------------------------------------------

    private Board getBoard() {
        return gameState != null ? gameState.getBoard() : null;
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

        int cell = getCellSize();
        int ox   = getGridOriginX();
        int oy   = getGridOriginY();

        drawEdgeIndicators(g2, cell, ox, oy);
        drawGrid(g2, cell, ox, oy);
        drawLabels(g2, cell, ox, oy);
        drawStones(g2, cell, ox, oy);
        drawHoverPreview(g2, cell, ox, oy);

        g2.dispose();
    }

    private void drawEdgeIndicators(Graphics2D g2, int cs, int ox, int oy) {
        int gridW = cs * boardSize;
        int gridH = cs * boardSize;
        int t = 5;

        // Top/bottom → BLACK goal
        g2.setColor(theme.getEdgeBlack());
        g2.fillRect(ox, oy - t, gridW, t);
        g2.fillRect(ox, oy + gridH, gridW, t);

        // Left/right → WHITE goal
        g2.setColor(theme.getEdgeWhite());
        g2.fillRect(ox - t, oy, t, gridH);
        g2.fillRect(ox + gridW, oy, t, gridH);
    }

    private void drawGrid(Graphics2D g2, int cs, int ox, int oy) {
        for (int r = 0; r < boardSize; r++) {
            for (int c = 0; c < boardSize; c++) {
                int x = ox + c * cs;
                int y = oy + r * cs;
                Position pos = new Position(r, c);
                boolean isLight = (r + c) % 2 == 0;

                Color bg;
                if (hoveredCell != null && hoveredCell.equals(pos)) {
                    bg = isLight ? theme.getLightSquareHover()
                                : theme.getDarkSquareHover();
                } else {
                    bg = isLight ? theme.getLightSquare()
                                : theme.getDarkSquare();
                }
                g2.setColor(bg);
                g2.fillRect(x, y, cs, cs);

                if (lastFilledPositions.contains(pos)) {
                    g2.setColor(theme.getFilledHighlight());
                    g2.fillRect(x, y, cs, cs);
                }
                if (lastCapturedPositions.contains(pos)) {
                    g2.setColor(theme.getCapturedHighlight());
                    g2.fillRect(x, y, cs, cs);
                }

                g2.setColor(theme.getGridLine());
                g2.drawRect(x, y, cs, cs);
            }
        }
    }

    private void drawLabels(Graphics2D g2, int cs, int ox, int oy) {
        g2.setColor(theme.getLabelColor());
        Font font = new Font("SansSerif", Font.BOLD, Math.max(10, cs / 3));
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();

        for (int c = 0; c < boardSize; c++) {
            String s = String.valueOf(c);
            g2.drawString(s,
                ox + c * cs + (cs - fm.stringWidth(s)) / 2,
                oy - 10);
        }
        for (int r = 0; r < boardSize; r++) {
            String s = String.valueOf(r);
            g2.drawString(s,
                ox - fm.stringWidth(s) - 8,
                oy + r * cs + (cs + fm.getAscent()) / 2 - 2);
        }
    }

    private void drawStones(Graphics2D g2, int cs, int ox, int oy) {
        Board board = getBoard();
        if (board == null) return;
        int margin = Math.max(2, cs / 8);
        int size   = cs - 2 * margin;

        for (int r = 0; r < boardSize; r++) {
            for (int c = 0; c < boardSize; c++) {
                Stone stone = board.getStone(new Position(r, c));
                if (stone == Stone.EMPTY) continue;

                int x = ox + c * cs + margin;
                int y = oy + r * cs + margin;
                Ellipse2D.Double shape = new Ellipse2D.Double(x, y, size, size);

                if (stone == Stone.BLACK) {
                    g2.setPaint(new GradientPaint(
                        x, y, theme.getBlackStoneHighlight(),
                        x + size, y + size, theme.getBlackStone()));
                    g2.fill(shape);
                    g2.setColor(theme.getBlackStoneBorder());
                } else {
                    g2.setPaint(new GradientPaint(
                        x, y, theme.getWhiteStone(),
                        x + size, y + size, theme.getWhiteStoneHighlight()));
                    g2.fill(shape);
                    g2.setColor(theme.getWhiteStoneBorder());
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.draw(shape);
                    g2.setColor(new Color(150, 150, 150));
                }
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(shape);

                // Last-move marker (gold dot)
                if (lastMovePosition != null
                    && lastMovePosition.getRow() == r
                    && lastMovePosition.getCol() == c) {
                    int dot = Math.max(4, size / 5);
                    g2.setColor(theme.getLastMoveMarker());
                    g2.fillOval(x + (size - dot) / 2,
                                y + (size - dot) / 2, dot, dot);
                }
            }
        }
    }

    private void drawHoverPreview(Graphics2D g2, int cs, int ox, int oy) {
        Board board = getBoard();
        if (hoveredCell == null || board == null
            || board.getStone(hoveredCell) != Stone.EMPTY) {
            return;
        }

        int margin = Math.max(2, cs / 8);
        int size   = cs - 2 * margin;
        int x = ox + hoveredCell.getCol() * cs + margin;
        int y = oy + hoveredCell.getRow() * cs + margin;
        Ellipse2D.Double shape = new Ellipse2D.Double(x, y, size, size);

        g2.setColor(currentPlayer == Stone.BLACK
            ? new Color(30, 30, 30, 80)
            : new Color(240, 240, 240, 120));
        g2.fill(shape);
        g2.setColor(new Color(150, 150, 150, 100));
        g2.setStroke(new BasicStroke(1.0f));
        g2.draw(shape);
    }
}
