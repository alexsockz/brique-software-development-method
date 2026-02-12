package brique.core;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.IntFunction;

// Factory responsible for creating GameEngine instances.
// Uses a registry so new game modes can be added without modifying this class (OCP).
public final class GameEngineFactory {

    private static final Map<GameMode, IntFunction<GameEngine>> REGISTRY = new EnumMap<>(GameMode.class);

    static {
        // Register built-in game modes
        REGISTRY.put(GameMode.LOCAL_1V1, LocalGameEngine::new);
        REGISTRY.put(GameMode.ONLINE,    OnlineGameEngine::new);
        REGISTRY.put(GameMode.VS_BOT,    BotGameEngine::new);
    }

    private GameEngineFactory() { /* utility class */ }

    // Allows external code to register additional game modes at startup.
    public static void register(GameMode mode, IntFunction<GameEngine> factory) {
        REGISTRY.put(mode, factory);
    }

    public static GameEngine create(GameMode mode, int boardSize) {
        IntFunction<GameEngine> factory = REGISTRY.get(mode);
        if (factory == null) {
            throw new IllegalArgumentException("Unknown game mode: " + mode);
        }
        return factory.apply(boardSize);
    }
}
