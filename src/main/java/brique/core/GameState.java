package brique.core;

import java.util.ArrayList;
import java.util.List;

//TODO create a relieve moveHistory, i guess as a new game engine that automatically expands to a certain point?
public class GameState {
    /**
     * Represents the current status of a game. When the game begins it is
     * {@link #IN_PROGRESS}. Once a player has connected their respective
     * edges, the status transitions to either {@link #BLACK_WON} or
     * {@link #WHITE_WON}. Should a game end prematurely (e.g. by a user
     * quitting) it becomes {@link #ABORTED}.  This enum lives inside
     * {@link GameState} to limit its visibility to the core model.
     */
    public enum GameEnd{
        /** The game is ongoing. */
        IN_PROGRESS,
        /** Black has satisfied the win condition. */
        BLACK_WON,
        /** White has satisfied the win condition. */
        WHITE_WON,
        /** The game was terminated before a winner was determined. */
        ABORTED
    }
    private final Board board;
    private Stone currentPlayer;
    private GameEnd status;
    private boolean pieRuleAvailable;
    private final List<Move> moveHistory;
    
    public GameState(int boardSize) {
        this.board = new Board(boardSize);
        this.currentPlayer = Stone.BLACK;
        this.status = GameEnd.IN_PROGRESS;
        this.pieRuleAvailable = true;
        this.moveHistory = new ArrayList<>();
    }
    
    // Getters and setters
    
    public void switchPlayer() {
        currentPlayer = currentPlayer.opposite();
    }
    
    public void recordMove(Move result) {
        moveHistory.add(result);
    }
    public Stone getCurrentPlayer() {
        return currentPlayer;
    }

    public Board getBoard() {
        return board;
    }

    public void turnOffPieRule() {
        pieRuleAvailable=false;
    }

    public GameEnd getStatus() {
        return status;
    }

    /**
     * Updates the current game status based on the winning colour. This method
     * is used by the {@code GameEngine} once the {@link GameRules} report a
     * win. Without this method the engine would need to break encapsulation
     * (e.g. via reflection) to change the state.  If {@code winner} is
     * {@link Stone#EMPTY} the status remains unchanged.
     *
     * @param winner the colour that has connected its opposing edges
     */
    public void declareWinner(Stone winner) {
        if (winner == Stone.BLACK) {
            this.status = GameEnd.BLACK_WON;
        } else if (winner == Stone.WHITE) {
            this.status = GameEnd.WHITE_WON;
        }
    }

    /**
     * Abort the current game. This can be invoked by a UI component when a
     * player quits or the match is stopped. Once aborted, no more moves
     * should be processed.
     */
    public void abort() {
        this.status = GameEnd.ABORTED;
    }

    /**
     * Returns {@code true} when the game has neither been won nor aborted.
     */
    public boolean isInProgress() {
        return this.status == GameEnd.IN_PROGRESS;
    }

    /**
     * Expose the move history for read‑only purposes. The returned list is
     * unmodifiable to prevent external mutation; callers wishing to examine
     * past moves can iterate over it.  Returning a defensive copy would
     * provide additional safety but the size of the history is modest and
     * modification through this view is intentionally unsupported.
     *
     * @return the list of moves played so far
     */
    public List<Move> getMoveHistory() {
        return java.util.Collections.unmodifiableList(moveHistory);
    }

    public boolean ispieRuleAvailable() {
        return pieRuleAvailable;
    }
}
