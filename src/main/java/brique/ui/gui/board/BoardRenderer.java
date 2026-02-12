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

        g2.setColor(theme.edges().black());
        g2.fillRect(ox, oy - t, gridW, t);
        g2.fillRect(ox, oy + gridH, gridW, t);

        g2.setColor(theme.edges().white());
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
                Position pos = Position.of(r, c);
                boolean isLight = (r + c) % 2 == 0;

                Color bg;
                if (hoveredCell != null && hoveredCell.equals(pos)) {
                    bg = isLight ? theme.squares().lightHover() : theme.squares().darkHover();
                } else {
                    bg = isLight ? theme.squares().light() : theme.squares().dark();
                }

                g2.setColor(bg);
                g2.fillRect(x, y, cs, cs);

                if (filledPositions.contains(pos)) {
                    g2.setColor(theme.highlights().filled());
                    g2.fillRect(x, y, cs, cs);
                }
                if (capturedPositions.contains(pos)) {
                    g2.setColor(theme.highlights().captured());
                    g2.fillRect(x, y, cs, cs);
                }

                g2.setColor(theme.grid().line());
                g2.drawRect(x, y, cs, cs);
            }
        }
    }

    // --- Row / column labels ----------------------------------

    public void drawLabels(Graphics2D g2, int cs, int ox, int oy, int boardSize) {
        g2.setColor(theme.grid().label());
        Font font = new Font(theme.titleFont(), Font.BOLD, Math.max(10, cs / 3));
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
                Stone stone = board.getStone(Position.of(r, c));
                if (stone == Stone.EMPTY) continue;

                int x = ox + c * cs + margin;
                int y = oy + r * cs + margin;
                Ellipse2D.Double shape = new Ellipse2D.Double(x, y, size, size);

                if (stone == Stone.BLACK) {
                    g2.setPaint(new GradientPaint(
                        x, y, theme.stones().blackHighlight(),
                        ((float) x) + size, ((float) y) + size, theme.stones().black()));
                    g2.fill(shape);
                    g2.setColor(theme.stones().blackBorder());
                } else {
                    g2.setPaint(new GradientPaint(
                        x, y, theme.stones().white(),
                        ((float) x) + size, ((float) y) + size, theme.stones().whiteHighlight()));
                    g2.fill(shape);
                    g2.setColor(theme.stones().whiteBorder());
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.draw(shape);
                    g2.setColor(theme.stones().shadow());
                }

                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(shape);

                if (lastMovePosition != null
                    && lastMovePosition.row() == r
                    && lastMovePosition.col() == c) {
                    int dot = Math.max(4, size / 5);
                    g2.setColor(theme.highlights().lastMove());
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
        int x = ox + hoveredCell.col() * cs + margin;
        int y = oy + hoveredCell.row() * cs + margin;
        Ellipse2D.Double shape = new Ellipse2D.Double(x, y, size, size);

        g2.setColor(currentPlayer == Stone.BLACK
            ? theme.stones().blackPreview()
            : theme.stones().whitePreview());
        g2.fill(shape);

        g2.setColor(theme.stones().previewBorder());
        g2.setStroke(new BasicStroke(1.0f));
        g2.draw(shape);
    }
}
