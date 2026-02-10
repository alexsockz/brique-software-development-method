package brique.core;

public class OnlineGameEngine implements GameEngine {

    private final GameState state;

    public OnlineGameEngine(int boardSize) {
        this.state = new GameState(boardSize);
    }

    @Override
    public GameState getState() {
        return state;
    }

    @Override
    public boolean playMove(Position position) {
        // TODO: 1. Serialise move and send to game server
        // TODO: 2. Receive validated game state from server
        // TODO: 3. Apply server state to local GameState
        // TODO: 4. Return success/failure based on server response
        throw new UnsupportedOperationException(
            "Online multiplayer is not yet implemented. "
            + "This engine will communicate with a game server to process moves.");
    }

    @Override
    public boolean isGameOver() {
        return !state.isInProgress();
    }
}
