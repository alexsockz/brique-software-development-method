package brique.core;

import java.util.ArrayList;
import java.util.List;

//TODO create a relieve moveHistory, i guess as a new game engine that automatically expands to a certain point?
public class GameState {
    private enum GameEnd{
        IN_PROGRESS,
        BLACK_WON,
        WHITE_WON,
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

    public boolean ispieRuleAvailable() {
        return pieRuleAvailable;
    }
}
