package brique.core;

import brique.rules.GameRules;
import brique.rules.RuleType;
import brique.rules.RulesFactory;

public class LocalGameEngine implements GameEngine {

    private final GameState state;
    private final GameRules rules;

    public LocalGameEngine(int boardSize) {
        this(boardSize, RuleType.STANDARD);
    }

    public LocalGameEngine(int boardSize, RuleType ruleType) {
        this.state = new GameState(boardSize);
        this.rules = RulesFactory.createRules(ruleType);
    }

    @Override
    public GameState getState() {
        return state;
    }

    @Override
    public boolean playMove(Position position) {
        if (!state.isInProgress()) {
            throw new IllegalStateException("Cannot play a move after the game has ended");
        }

        Stone player = state.getCurrentPlayer();
        Move move = new Move(position, player);

        if (!rules.isValidMove(state, move)) {
            return false;
        }

        rules.processMove(state, move);
        state.recordMove(move);

        if (rules.checkWinCondition(state, player)) {
            state.declareWinner(player);
            return true;
        }

        if (player == Stone.WHITE && state.isPieRuleAvailable()) {
            state.turnOffPieRule();
        }

        state.switchPlayer();
        return true;
    }

    @Override
    public boolean isGameOver() {
        return !state.isInProgress();
    }
}
