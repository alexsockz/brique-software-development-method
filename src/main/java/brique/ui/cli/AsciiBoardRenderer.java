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

        // Print column indices at the top
        sb.append("   ");
        for (int c = 0; c < size; c++) {
            sb.append(c).append(' ');
        }
        sb.append('\n');

        // Iterate over each row of the board
        for (int r = 0; r < size; r++) {

            // Print the row index at the beginning of the line
            sb.append(r).append("  ");

            // Render each cell in the current row
            for (int c = 0; c < size; c++) {

                // Retrieve the stone at the current position
                Stone s = board.getStone(Position.of(r, c));

                // Map stone types to ASCII characters
                char ch;
                switch (s) {
                    case BLACK:
                        ch = 'B';
                        break;
                    case WHITE:
                        ch = 'W';
                        break;
                    default:
                        ch = '.';
                }

                // Append the character representation to the output
                sb.append(ch).append(' ');
            }

            // Move to the next line after each row
            sb.append('\n');
        }

        // Return the final ASCII representation of the board
        return sb.toString();
    }
}
