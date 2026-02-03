

package brique.core;

public enum Stone {
    BLACK, WHITE, EMPTY;
    
    public Stone opposite() {
        return this == BLACK ? WHITE : this == WHITE ? BLACK : EMPTY;
    }
}

