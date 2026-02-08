package brique.rules;

import brique.core.*;
import java.util.List;

public interface GameRules {
    boolean isValidMove(GameState state, Move move);
    void processMove(GameState state, Move move); //we want the move itself to be modified
    boolean checkWinCondition(GameState state, Stone player);
    List<Position> getEscorts(Position position, Board board);
}