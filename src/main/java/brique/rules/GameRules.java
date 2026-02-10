package brique.rules;

import brique.core.*;
import java.util.List;

// Strategy interface defining the rule set of the game.
// Different implementations can provide alternative game variants.
public interface GameRules {

    boolean isValidMove(GameState state, Move move);

    void processMove(GameState state, Move move); // move is modified with side effects

    boolean checkWinCondition(GameState state, Stone player);

    List<Position> getEscorts(Position position, Board board);
}
