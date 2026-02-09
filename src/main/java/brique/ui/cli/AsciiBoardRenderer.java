package brique.ui.cli;

import brique.core.Board;
import brique.core.Position;
import brique.core.Stone;
import brique.ui.BoardRendererInterface;

public class AsciiBoardRenderer implements BoardRendererInterface {
    @Override
    public String render(Board board) {
        int size = board.getSize();
        StringBuilder sb = new StringBuilder();

        // Header row: column indices
        sb.append("   ");
        for (int c = 0; c < size; c++) {
            sb.append(c).append(' ');
        }
        sb.append('\n');

        for (int r = 0; r < size; r++) {
            // Row index
            sb.append(r).append("  ");
            for (int c = 0; c < size; c++) {
                Stone s = board.getStone(new Position(r, c));
                char ch;
                switch (s) {
                    case BLACK: ch = 'B'; break;
                    case WHITE: ch = 'W'; break;
                    default: ch = '.';
                }
                sb.append(ch).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}