package brique.engine;

import brique.core.Board;
import brique.core.GameState;
import brique.core.Move;
import brique.core.Position;
import brique.core.Stone;
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

    public boolean isGameOver() {
        return !state.isInProgress();
    }

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