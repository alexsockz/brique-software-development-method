package brique.core;

public class Board {

    // Size of the (square) board
    private final int size;

    // 2D grid storing the stone placed at each position
    private final Stone[][] grid;

    public Board(int size) {

        // Prevent creation of invalid boards
        if (size <= 0) {
            throw new IllegalStateException("0 or negative size boards can't exist");
        }

        this.size = size;

        // Allocate the grid
        this.grid = new Stone[size][size];

        // Initialize all cells as empty
        initializeEmpty();
    }

    private void initializeEmpty() {

        // Iterate over every cell and mark it as empty
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = Stone.EMPTY;
            }
        }
    }

    public Stone getStone(Position pos) {

        return grid[pos.row()][pos.col()];
    }

    public void setStone(Position pos, Stone stone) {

        grid[pos.row()][pos.col()] = stone;
    }

    public boolean isValidPosition(Position pos) {

        return pos.row() >= 0 && pos.row() < size &&
               pos.col() >= 0 && pos.col() < size;
    }

    public int getSize() {
        return size;
    }

    public Board copy() {

        // Create a new board with the same size
        Board copy = new Board(size);

        // Copy each row of the grid efficiently
        for (int i = 0; i < size; i++) {
            System.arraycopy(grid[i], 0, copy.grid[i], 0, size);
        }

        return copy;
    }
}
