package brique.core;

import java.util.ArrayList;
import java.util.List;

public class GameState {


    // The board representing the current placement of stones
    private final Board board;

    // The player whose turn it currently is
    private Stone currentPlayer;

    // Current status of the game (in progress, won, aborted)
    private GameEnd status;

    // Whether the pie (swap) rule can still be applied
    private boolean pieRuleAvailable;

    // History of all moves played so far
    private final List<Move> moveHistory;

    public GameState(int boardSize) {

        // Initialize board with given size
        this.board = new Board(boardSize);

        // Black always starts according to the rules
        this.currentPlayer = Stone.BLACK;

        // Game starts in progress
        this.status = GameEnd.IN_PROGRESS;

        // Pie rule is available until White decides otherwise
        this.pieRuleAvailable = true;

        // Initialize empty move history
        this.moveHistory = new ArrayList<>();
    }

    public void switchPlayer() {

        // Toggle between BLACK and WHITE
        currentPlayer = currentPlayer.opposite();
    }

    public void recordMove(Move result) {

        // Append move to the history list
        moveHistory.add(result);
    }

    public Stone getCurrentPlayer() {
        return currentPlayer;
    }

    public Board getBoard() {
        return board;
    }

    public void turnOffPieRule() {

        pieRuleAvailable = false;
    }

    public GameEnd getStatus() {
        return status;
    }

    public void declareWinner(Stone winner) {

        // Set the status according to the winning player
        if (winner == Stone.BLACK) {
            this.status = GameEnd.BLACK_WON;
        } else if (winner == Stone.WHITE) {
            this.status = GameEnd.WHITE_WON;
        }
    }

    public void abort() {

        // Mark the game as aborted
        this.status = GameEnd.ABORTED;
    }

    public boolean isInProgress() {

        return this.status == GameEnd.IN_PROGRESS;
    }

    public void applyPieRule() {

        // Pie rule cannot be applied after game completion
        if (!this.isInProgress()) {
            throw new IllegalStateException("Cannot apply pie rule after the game has ended");
        }

        // Pie rule is only available to White on her first turn
        if (!this.isPieRuleAvailable() || this.getCurrentPlayer() != Stone.WHITE) {
            throw new IllegalStateException(
                "Pie rule can only be used by White on her first turn"
            );
        }

        // Re-colour Black's first move to White
        this.board.setStone(
            this.moveHistory.get(0).getPosition(),
            currentPlayer
        );

        // Disable further use of the pie rule
        this.turnOffPieRule();

        // Yield the turn back to Black
        if (this.getCurrentPlayer() == Stone.WHITE) {
            this.switchPlayer();
        }
    }

    public List<Move> getMoveHistory() {

        // Prevent external modification of history
        return java.util.Collections.unmodifiableList(moveHistory);
    }

    public boolean isPieRuleAvailable() {
        return pieRuleAvailable;
    }

    public Stone getWinner() {

        // Map the game status to a winning stone
        switch (this.status) {
            case BLACK_WON:
                return Stone.BLACK;
            case WHITE_WON:
                return Stone.WHITE;
            default:
                return Stone.EMPTY;
        }
    }
}
