package brique.ui.gui.board;

import brique.core.Position;


public class CoordinateTranslator {

    private static final int MARGIN       = 40;
    private static final int LABEL_MARGIN = 25;

    private final int boardSize;
    private final int panelWidth;
    private final int panelHeight;

    public CoordinateTranslator(int boardSize, int panelWidth, int panelHeight) {
        this.boardSize   = boardSize;
        this.panelWidth  = panelWidth;
        this.panelHeight = panelHeight;
    }

    public int getCellSize() {
        int w = panelWidth  - 2 * MARGIN - LABEL_MARGIN;
        int h = panelHeight - 2 * MARGIN - LABEL_MARGIN;
        if (boardSize <= 0) return 40;
        return Math.max(20, Math.min(w / boardSize, h / boardSize));
    }

    public int getGridOriginX() {
        return (panelWidth - getCellSize() * boardSize) / 2 + LABEL_MARGIN / 2;
    }

    public int getGridOriginY() {
        return (panelHeight - getCellSize() * boardSize) / 2 + LABEL_MARGIN / 2;
    }

    
    public Position pixelToCell(int x, int y) {
        int cellSize = getCellSize();
        int ox = getGridOriginX();
        int oy = getGridOriginY();

        if (x < ox || y < oy) return null;

        int col = (x - ox) / cellSize;
        int row = (y - oy) / cellSize;

        if (row >= 0 && row < boardSize && col >= 0 && col < boardSize) {
            return Position.of(row, col);
        }
        return null;
    }
}
