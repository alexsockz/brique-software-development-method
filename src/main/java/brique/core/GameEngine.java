package brique.core;

public interface GameEngine {

    boolean playMove(Position position);

    boolean isGameOver();

    GameState getState();
}
