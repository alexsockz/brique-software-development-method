package brique.core;

public enum Stone {

    // Black player's stone
    BLACK,

    // White player's stone
    WHITE,

    // Empty board intersection
    EMPTY;

    public Stone opposite() {

        // Swap BLACK and WHITE; EMPTY has no opposite
        return this == BLACK ? WHITE : this == WHITE ? BLACK : EMPTY;
    }
}
