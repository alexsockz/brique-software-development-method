package brique.core;

public class Position {

    // Row index on the board
    private final int row;

    // Column index on the board
    private final int col;

    public Position(int row, int col) {

        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public boolean equals(Object obj) {

        // Same reference implies equality
        if (this == obj) {
            return true;
        }

        // Must be of the same type
        if (!(obj instanceof Position)) {
            return false;
        }

        // Compare coordinates
        Position other = (Position) obj;
        return row == other.row && col == other.col;
    }

    @Override
    public int hashCode() {

        return 31 * row + col;
    }

    @Override
    public String toString() {

        return "(" + row + ", " + col + ")";
    }
}
