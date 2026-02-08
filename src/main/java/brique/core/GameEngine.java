package brique.core;

import brique.rules.GameRules;
import brique.rules.RuleType;
import brique.rules.RulesFactory;

public class GameEngine {
    private final GameState state;
    private final GameRules rules;

    public GameEngine(int boardSize) {
        this(boardSize, RuleType.STANDARD);
    }

    public GameEngine(int boardSize, RuleType ruleType) {
        this.state = new GameState(boardSize);
        this.rules = RulesFactory.createRules(ruleType);
    }

    public GameState getState() {
        return state;
    }

    public boolean playMove(Position position) {
        if (!state.isInProgress()) {
            throw new IllegalStateException("Cannot play a move after the game has ended");
        }

        Stone player = state.getCurrentPlayer();
        Move move = new Move(position, player);

        // Validate the move using the strategy; invalid moves are refused
        if (!rules.isValidMove(state, move)) {
            return false;
        }

        // Delegate processing to the rule implementation.  This call will
        // mutate the board and annotate the move with captured and filled
        // positions as per the escort rule.
        rules.processMove(state, move);

        // Record the move in the state for history tracking
        state.recordMove(move);

        // Check if the current player has won by connecting their edges【215399881283417†L30-L32】.
        if (rules.checkWinCondition(state, player)) {
            state.declareWinner(player);
            return true;
        }

        // If the move was White's first turn and the pie rule is still available,
        // it must be disabled because the player elected to place a stone rather
        // than swapping colours.  According to the pie rule, White has one
        // opportunity on her first turn to switch sides【215399881283417†L33-L36】; once she
        // chooses to play normally, that option vanishes.
        if (player == Stone.WHITE && state.isPieRuleAvailable()) {
            state.turnOffPieRule();
        }

        // Advance to the next player's turn
        state.switchPlayer();

        return true;
    }

    public boolean isGameOver() {
        return !state.isInProgress();
    }
}