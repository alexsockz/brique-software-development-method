package brique.core;

public record Position(int row, int col) {

    private static final int CACHE_SIZE = 21; // covers boards up to 21×21
    private static final Position[][] CACHE = new Position[CACHE_SIZE][CACHE_SIZE];

    static {
        for (int r = 0; r < CACHE_SIZE; r++) {
            for (int c = 0; c < CACHE_SIZE; c++) {
                CACHE[r][c] = new Position(r, c);
            }
        }
    }

    // Flyweight factory — returns a cached instance when possible.
    public static Position of(int row, int col) {
        if (row >= 0 && row < CACHE_SIZE && col >= 0 && col < CACHE_SIZE) {
            return CACHE[row][col];
        }
        return new Position(row, col);
    }

    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }
}
