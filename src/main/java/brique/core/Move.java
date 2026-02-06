package brique.core;

import java.util.ArrayList;
import java.util.List;

//TODO maybe separate position and stone into a separate class Move
public class Move {
    private final Position position;
    private final Stone stone;
    private final List<Position> capturedPositions;
    private final List<Position> filledPositions;
    
    public Move(Position position,Stone stone) {
        this.position = position;
        this.stone =stone;
        this.capturedPositions = new ArrayList<>();
        this.filledPositions = new ArrayList<>();
    }
    //when the move is processed this will be called to modify the move and up the captured positions found by the specific rule set
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