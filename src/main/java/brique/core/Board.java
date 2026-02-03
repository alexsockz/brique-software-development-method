package brique.core;
//TODO pos is used only for 
public class Board {
    private final int size;
    private final Stone[][] grid;
    
    public Board(int size) {
        this.size = size;
        this.grid = new Stone[size][size];
        initializeEmpty();
    }
    
    private void initializeEmpty() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = Stone.EMPTY;
            }
        }
    }
    
    public Stone getStone(Position pos) {
        return grid[pos.getRow()][pos.getCol()];
    }
    
    public void setStone(Position pos, Stone stone) {
        grid[pos.getRow()][pos.getCol()] = stone;
    }
    
    public boolean isValidPosition(Position pos) {
        return pos.getRow() >= 0 && pos.getRow() < size &&
               pos.getCol() >= 0 && pos.getCol() < size;
    }
    
    public int getSize() {
        return size;
    }
    
    public Board copy() {
        Board copy = new Board(size);
        for (int i = 0; i < size; i++) {
            System.arraycopy(grid[i], 0, copy.grid[i], 0, size);
        }
        return copy;
    }
}
