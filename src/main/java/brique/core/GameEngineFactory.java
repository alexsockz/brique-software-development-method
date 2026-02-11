package brique.core;

public final class GameEngineFactory {

    private GameEngineFactory() { /* utility class */ }

    public static GameEngine create(GameMode mode, int boardSize) {
        switch (mode) {
            case LOCAL_1V1: return new LocalGameEngine(boardSize);
            case ONLINE:    return new OnlineGameEngine(boardSize);
            case VS_BOT:    return new BotGameEngine(boardSize);
            default:
                throw new IllegalArgumentException("Unknown game mode: " + mode);
        }
    }
}
