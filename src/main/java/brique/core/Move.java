package brique.core;

import java.util.ArrayList;
import java.util.List;

// Represents a single move played in the game.
// A builder could be used here, but the current structure is simple
// enough to keep construction straightforward.
public class Move {

    // Position where the stone is placed
    private final Position position;

    // Stone (player colour) associated with this move
    private final Stone stone;

    // Positions of stones captured as a result of this move
    private final List<Position> capturedPositions;

    // Positions filled due to escort or filling rules
    private final List<Position> filledPositions;

    public Move(Position position, Stone stone) {

        this.position = position;
        this.stone = stone;

        // Initialize empty lists for side effects of the move
        this.capturedPositions = new ArrayList<>();
        this.filledPositions = new ArrayList<>();
    }

    public void addCapturedPosition(Position pos) {

        capturedPositions.add(pos);
    }

    public void addFilledPosition(Position pos) {

        filledPositions.add(pos);
    }

    public List<Position> getCapturedPositions() {

        return java.util.Collections.unmodifiableList(capturedPositions);
    }

    public List<Position> getFilledPositions() {

        return java.util.Collections.unmodifiableList(filledPositions);
    }

    public Position getPosition() {
        return position;
    }

    public Stone getStone() {
        return stone;
    }
}
