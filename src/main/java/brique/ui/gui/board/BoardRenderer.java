package brique.ui.gui.board;

import brique.core.Board;
import brique.core.Position;
import brique.core.Stone;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Set;


public class BoardRenderer {

    private final BoardTheme theme;

    public BoardRenderer(BoardTheme theme) {
        this.theme = theme;
    }

    // --- Edge indicators (goal strips) ------------------------

    public void drawEdgeIndicators(Graphics2D g2, int cs, int ox, int oy, int boardSize) {
        int gridW = cs * boardSize;
        int gridH = cs * boardSize;
        int t = 5;

        g2.setColor(theme.getEdgeBlack());
        g2.fillRect(ox, oy - t, gridW, t);
        g2.fillRect(ox, oy + gridH, gridW, t);

        g2.setColor(theme.getEdgeWhite());
        g2.fillRect(ox - t, oy, t, gridH);
        g2.fillRect(ox + gridW, oy, t, gridH);
    }

    // --- Checkerboard grid with hover / highlight overlays ----

    public void drawGrid(Graphics2D g2, int cs, int ox, int oy, int boardSize,
                         Position hoveredCell,
                         Set<Position> filledPositions,
                         Set<Position> capturedPositions) {

        for (int r = 0; r < boardSize; r++) {
            for (int c = 0; c < boardSize; c++) {
                int x = ox + c * cs;
                int y = oy + r * cs;
                Position pos = new Position(r, c);
                boolean isLight = (r + c) % 2 == 0;

                Color bg;
                if (hoveredCell != null && hoveredCell.equals(pos)) {
                    bg = isLight ? theme.getLightSquareHover() : theme.getDarkSquareHover();
                } else {
                    bg = isLight ? theme.getLightSquare() : theme.getDarkSquare();
                }

                g2.setColor(bg);
                g2.fillRect(x, y, cs, cs);

                if (filledPositions.contains(pos)) {
                    g2.setColor(theme.getFilledHighlight());
                    g2.fillRect(x, y, cs, cs);
                }
                if (capturedPositions.contains(pos)) {
                    g2.setColor(theme.getCapturedHighlight());
                    g2.fillRect(x, y, cs, cs);
                }

                g2.setColor(theme.getGridLine());
                g2.drawRect(x, y, cs, cs);
            }
        }
    }

    // --- Row / column labels ----------------------------------

    public void drawLabels(Graphics2D g2, int cs, int ox, int oy, int boardSize) {
        g2.setColor(theme.getLabelColor());
        Font font = new Font(theme.getTitleSubtitleFont(), Font.BOLD, Math.max(10, cs / 3));
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

    // --- Stones with gradients, borders, last-move marker -----

    public void drawStones(Graphics2D g2, int cs, int ox, int oy,
                           Board board, int boardSize, Position lastMovePosition) {
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
                        ((float) x) + size, ((float) y) + size, theme.getBlackStone()));
                    g2.fill(shape);
                    g2.setColor(theme.getBlackStoneBorder());
                } else {
                    g2.setPaint(new GradientPaint(
                        x, y, theme.getWhiteStone(),
                        ((float) x) + size, ((float) y) + size, theme.getWhiteStoneHighlight()));
                    g2.fill(shape);
                    g2.setColor(theme.getWhiteStoneBorder());
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.draw(shape);
                    g2.setColor(new Color(150, 150, 150));
                }

                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(shape);

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

    // --- Semi-transparent hover preview -----------------------

    public void drawHoverPreview(Graphics2D g2, int cs, int ox, int oy,
                                 Board board, Position hoveredCell, Stone currentPlayer) {
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
