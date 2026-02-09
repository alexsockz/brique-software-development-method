package brique.core;

import java.util.ArrayList;
import java.util.List;

public class GameState {

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

    public void declareWinner(Stone winner) {
        if (winner == Stone.BLACK) {
            this.status = GameEnd.BLACK_WON;
        } else if (winner == Stone.WHITE) {
            this.status = GameEnd.WHITE_WON;
        }
    }

    public void abort() {
        this.status = GameEnd.ABORTED;
    }

    public boolean isInProgress() {
        return this.status == GameEnd.IN_PROGRESS;
    }

    public void applyPieRule() {
        // Ensure the swap may occur only at the correct time
        if (!this.isInProgress()) {
            throw new IllegalStateException("Cannot apply pie rule after the game has ended");
        }
        if (!this.isPieRuleAvailable() || this.getCurrentPlayer() != Stone.WHITE) {
            throw new IllegalStateException("Pie rule can only be used by White on her first turn");
        }

        this.board.setStone(this.moveHistory.get(0).getPosition(), currentPlayer);

        this.turnOffPieRule();
        if(this.getCurrentPlayer()==Stone.WHITE){
            this.switchPlayer();
        }
    }
    public List<Move> getMoveHistory() {
        return java.util.Collections.unmodifiableList(moveHistory);
    }

    public boolean isPieRuleAvailable() {
        return pieRuleAvailable;
    }
    public Stone getWinner() {
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
