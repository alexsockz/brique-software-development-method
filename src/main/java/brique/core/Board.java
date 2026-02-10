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

        // Direct access to the grid using row and column
        return grid[pos.getRow()][pos.getCol()];
    }

    public void setStone(Position pos, Stone stone) {

        // Update the grid with the provided stone
        grid[pos.getRow()][pos.getCol()] = stone;
    }

    public boolean isValidPosition(Position pos) {

        // Ensure row and column are within [0, size)
        return pos.getRow() >= 0 && pos.getRow() < size &&
               pos.getCol() >= 0 && pos.getCol() < size;
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
