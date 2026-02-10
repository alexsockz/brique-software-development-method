package brique.core;

import brique.rules.GameRules;
import brique.rules.RuleType;
import brique.rules.RulesFactory;

public class GameEngine {

    // Holds the mutable state of the game (board, turn, history, winner)
    private final GameState state;

    // Strategy object encapsulating the active rule set
    private final GameRules rules;

    public GameEngine(int boardSize) {

        // Delegate to the configurable constructor with default rules
        this(boardSize, RuleType.STANDARD);
    }

    public GameEngine(int boardSize, RuleType ruleType) {

        // Initialize the game state with an empty board
        this.state = new GameState(boardSize);

        // Create the appropriate rules strategy via the factory
        this.rules = RulesFactory.createRules(ruleType);
    }

    public GameState getState() {

        // Expose state for read-only use by UI and controllers
        return state;
    }

    public boolean playMove(Position position) {

        // Prevent moves after the game has ended
        if (!state.isInProgress()) {
            throw new IllegalStateException("Cannot play a move after the game has ended");
        }

        // Determine the current player
        Stone player = state.getCurrentPlayer();

        // Create a move object combining position and player
        Move move = new Move(position, player);

        // Validate the move using the active rule strategy
        if (!rules.isValidMove(state, move)) {
            return false; // Illegal move, state unchanged
        }

        // Apply the move effects (stone placement, captures, fillings, etc.)
        rules.processMove(state, move);

        // Store the move in the game history
        state.recordMove(move);

        // Check whether this move causes the current player to win
        if (rules.checkWinCondition(state, player)) {

            // Mark the game as won by the current player
            state.declareWinner(player);
            return true;
        }

        // Disable the pie rule if White plays a stone instead of swapping
        if (player == Stone.WHITE && state.isPieRuleAvailable()) {
            state.turnOffPieRule();
        }

        // Switch to the other player's turn
        state.switchPlayer();

        return true;
    }

    public boolean isGameOver() {

        // Game is over when it is no longer in progress
        return !state.isInProgress();
    }
}
