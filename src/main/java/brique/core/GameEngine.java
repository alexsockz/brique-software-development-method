package brique.core;

import brique.rules.GameRules;
import brique.rules.RuleType;
import brique.rules.RulesFactory;

/**
 * Central coordinator for running a game of Brique.  This engine holds a
 * reference to a {@link GameState} representing the current board and
 * player turn as well as a {@link GameRules} strategy used to validate and
 * process moves.  It exposes operations to play a move, invoke the pie
 * (swap) rule, and query the game status.  By injecting the rule set via
 * {@link RulesFactory} the engine follows the {@code Strategy} pattern
 * allowing alternative rule sets to be plugged in without modifying the
 * engine itself (Open/Closed principle).  It depends on abstractions rather
 * than concrete rule implementations (Dependency Inversion) and delegates
 * responsibilities cleanly to its collaborators (Single Responsibility).
 */
public class GameEngine {
    private final GameState state;
    private final GameRules rules;

    /**
     * Creates a new game engine using the standard Brique rule set.  The
     * underlying {@link GameState} will be initialised with the given
     * {@code boardSize} and will always begin with black to move as per
     * the official rules of Brique【215399881283417†L18-L21】.
     *
     * @param boardSize the width and height of the square board
     */
    public GameEngine(int boardSize) {
        this(boardSize, RuleType.STANDARD);
    }

    /**
     * Creates a new game engine with a custom rule set.  This constructor
     * facilitates extensibility by allowing callers to choose between
     * different rule variants without altering the engine code.  The
     * {@code ruleType} argument is passed to {@link RulesFactory}, which
     * returns the appropriate {@link GameRules} instance.
     *
     * @param boardSize the width and height of the square board
     * @param ruleType  the variant of rules to apply
     */
    public GameEngine(int boardSize, RuleType ruleType) {
        this.state = new GameState(boardSize);
        this.rules = RulesFactory.createRules(ruleType);
    }

    /**
     * Retrieves the current game state.  A UI layer can use this to render
     * the board, determine whose turn it is, or examine the move history.
     *
     * @return the current {@link GameState}
     */
    public GameState getState() {
        return state;
    }

    /**
     * Attempt to play a move at the given board position.  The move is
     * validated by the active {@link GameRules} implementation; if the move
     * is illegal (e.g. the square is occupied or it is the wrong player's
     * turn), the method returns {@code false} and the state remains
     * unchanged.  Otherwise the move is processed, any escort captures and
     * fillings are applied, the move recorded, the win condition checked,
     * players switched and the pie rule disabled if appropriate.
     *
     * @param position the location on the board where the current player wants to place a stone
     * @return {@code true} if the move was successfully played; {@code false} if it was invalid
     * @throws IllegalStateException if the game has already concluded and no further moves may be played
     */
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
        // positions as per the escort rule【215399881283417†L18-L26】.
        rules.ProcessMove(state, move);

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
        if (player == Stone.WHITE && state.ispieRuleAvailable()) {
            state.turnOffPieRule();
        }

        // Advance to the next player's turn
        state.switchPlayer();

        return true;
    }

    /**
     * Applies the pie (swap) rule.  This operation is only legal on
     * White's first turn and only if the pie rule has not already been
     * exercised.  When invoked the colours of all occupied squares on the
     * board are swapped (black stones become white and vice versa) and the
     * pie rule is disabled.  The current player does not change because the
     * move counts as White's turn; after swapping, it is still the other
     * player's turn to place a stone【99388471637534†L381-L402】.  If the pie rule is not
     * available or it is not White's turn, an {@link IllegalStateException}
     * is thrown.
     */
    public void applyPieRule() {
        // Ensure the swap may occur only at the correct time
        if (!state.isInProgress()) {
            throw new IllegalStateException("Cannot apply pie rule after the game has ended");
        }
        if (!state.ispieRuleAvailable() || state.getCurrentPlayer() != Stone.WHITE) {
            throw new IllegalStateException("Pie rule can only be used by White on her first turn");
        }

        Board board = state.getBoard();
        int size = board.getSize();

        // Swap the colours of all stones on the board.  Empty squares remain empty.
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Position pos = new Position(row, col);
                Stone current = board.getStone(pos);
                if (current == Stone.BLACK) {
                    board.setStone(pos, Stone.WHITE);
                } else if (current == Stone.WHITE) {
                    board.setStone(pos, Stone.BLACK);
                }
            }
        }

        // Disable further use of the pie rule
        state.turnOffPieRule();

        // Do NOT switch the current player.  The second player has used her
        // turn to swap colours and it is now the original first player's turn
        // (who will play with the opposite colour)【99388471637534†L381-L402】.
    }

    /**
     * Returns {@code true} if the current game has concluded either by one
     * player winning or by being aborted.
     *
     * @return {@code true} when no further moves may be played
     */
    public boolean isGameOver() {
        return !state.isInProgress();
    }

    /**
     * Returns the winner of the current game if any.  If the game is
     * still in progress or aborted without a winner this returns
     * {@link Stone#EMPTY}.
     *
     * @return the winning colour or EMPTY
     */
    public Stone getWinner() {
        switch (state.getStatus()) {
            case BLACK_WON:
                return Stone.BLACK;
            case WHITE_WON:
                return Stone.WHITE;
            default:
                return Stone.EMPTY;
        }
    }
}