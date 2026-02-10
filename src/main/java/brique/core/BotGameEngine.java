package brique.core;

import brique.rules.GameRules;
import brique.rules.RuleType;
import brique.rules.RulesFactory;


public class BotGameEngine implements GameEngine {

    private final GameState state;
    private final GameRules rules;
    private final Stone humanPlayer;

    public BotGameEngine(int boardSize) {
        this(boardSize, Stone.BLACK);
    }

    public BotGameEngine(int boardSize, Stone humanPlayer) {
        this.state = new GameState(boardSize);
        this.rules = RulesFactory.createRules(RuleType.STANDARD);
        this.humanPlayer = humanPlayer;
    }

    @Override
    public GameState getState() {
        return state;
    }

    @Override
    public boolean playMove(Position position) {
        // TODO: 1. Validate and process human move using rules
        // TODO: 2. Check win condition after human move
        // TODO: 3. Generate bot move using AI strategy
        // TODO: 4. Validate and process bot move
        // TODO: 5. Check win condition after bot move
        throw new UnsupportedOperationException(
            "Bot opponent is not yet implemented. "
            + "This engine will process human moves and generate bot responses.");
    }

    @Override
    public boolean isGameOver() {
        return !state.isInProgress();
    }

    public Stone getHumanPlayer() {
        return humanPlayer;
    }
}
