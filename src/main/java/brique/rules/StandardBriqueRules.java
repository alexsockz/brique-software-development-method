package brique.rules;


import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import brique.core.*;

public class StandardBriqueRules implements GameRules {
    private enum SquareColor{
        LIGHT, DARK;
    
        public static SquareColor at(Position pos) {
            return (pos.getRow() + pos.getCol()) % 2 == 0 ? LIGHT : DARK;
        }
    }
    @Override
    public boolean isValidMove(GameState state, Move move) {
        Board board = state.getBoard();
        Position pos = move.getPosition();
        
        // Check if position is valid and empty
        if (!board.isValidPosition(pos)) {
            return false;
        }
        
        if (board.getStone(pos) != Stone.EMPTY) {
            return false;
        }
        
        // Check if it's the correct player's turn
        return move.getStone() == state.getCurrentPlayer();
    }
    
    @Override
    public void processMove(GameState state, Move move) {
        Board board = state.getBoard();
        
        // Place the primary stone
        board.setStone(move.getPosition(), move.getStone());
        
        // Find all positions that need to be filled due to escort rule
        List<Position> positionsToFill = findPositionsToFill(board, move.getStone());
        
        for (Position pos : positionsToFill) {
            Stone existingStone = board.getStone(pos);
            
            if (existingStone == move.getStone().opposite()) {
                move.addCapturedPosition(pos);
            }
            
            board.setStone(pos, move.getStone());
            move.addFilledPosition(pos);
        }
    }
    
    private List<Position> findPositionsToFill(Board board, Stone playerStone) {
        List<Position> toFill = new ArrayList<>();
        int size = board.getSize();
        
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Position pos = new Position(row, col);
                
                // Skip if already occupied by friendly stone
                if (board.getStone(pos) == playerStone) {
                    continue;
                }
                
                // Check if both escorts are occupied by friendly stones
                List<Position> escorts = getEscorts(pos, board);
                
                if (escorts.size() == 2 &&
                    board.getStone(escorts.get(0)) == playerStone &&
                    board.getStone(escorts.get(1)) == playerStone) {
                    toFill.add(pos);
                }
            }
        }
        
        return toFill;
    }
    
    @Override
    public List<Position> getEscorts(Position position, Board board) {
        List<Position> escorts = new ArrayList<>();
        SquareColor color = SquareColor.at(position);
        int row = position.getRow();
        int col = position.getCol();
        
        if (color == SquareColor.LIGHT) {
            // Front (row - 1) and Left (col - 1)
            Position front = new Position(row - 1, col);
            Position left = new Position(row, col - 1);
            
            if (board.isValidPosition(front)) escorts.add(front);
            if (board.isValidPosition(left)) escorts.add(left);
        } else {
            // Behind (row + 1) and Right (col + 1)
            Position behind = new Position(row + 1, col);
            Position right = new Position(row, col + 1);
            
            if (board.isValidPosition(behind)) escorts.add(behind);
            if (board.isValidPosition(right)) escorts.add(right);
        }
        
        return escorts;
    }
    
    @Override
    public boolean checkWinCondition(GameState state, Stone player) {
        Board board = state.getBoard();
        int size = board.getSize();
        
        if (player == Stone.BLACK) {
            // Check if top edge connects to bottom edge
            return isConnected(board, player, getTopEdgePositions(size), 
                             getBottomEdgePositions(size));
        } else {
            // Check if left edge connects to right edge
            return isConnected(board, player, getLeftEdgePositions(size), 
                             getRightEdgePositions(size));
        }
    }
    
    private boolean isConnected(Board board, Stone player, 
                               List<Position> startEdge, List<Position> endEdge) {
        Set<Position> visited = new HashSet<>();
        Queue<Position> queue = new LinkedList<>();
        
        // Start BFS from all positions on the start edge with player's stone
        for (Position pos : startEdge) {
            if (board.getStone(pos) == player) {
                queue.add(pos);
                visited.add(pos);
            }
        }
        
        while (!queue.isEmpty()) {
            Position current = queue.poll();
            
            // Check if we reached the end edge
            if (endEdge.contains(current)) {
                return true;
            }
            
            // Explore neighbors (orthogonally adjacent)
            for (Position neighbor : getOrthogonalNeighbors(current, board)) {
                if (!visited.contains(neighbor) && 
                    board.getStone(neighbor) == player) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        
        return false;
    }
    
    private List<Position> getOrthogonalNeighbors(Position pos, Board board) {
        List<Position> neighbors = new ArrayList<>();
        int[][] deltas = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        
        for (int[] delta : deltas) {
            Position neighbor = new Position(
                pos.getRow() + delta[0], 
                pos.getCol() + delta[1]
            );
            if (board.isValidPosition(neighbor)) {
                neighbors.add(neighbor);
            }
        }
        
        return neighbors;
    }
    
    private List<Position> getTopEdgePositions(int size) {
        List<Position> positions = new ArrayList<>();
        for (int col = 0; col < size; col++) {
            positions.add(new Position(0, col));
        }
        return positions;
    }
    
    private List<Position> getBottomEdgePositions(int size) {
        List<Position> positions = new ArrayList<>();
        for (int col = 0; col < size; col++) {
            positions.add(new Position(size - 1, col));
        }
        return positions;
    }
    
    private List<Position> getLeftEdgePositions(int size) {
        List<Position> positions = new ArrayList<>();
        for (int row = 0; row < size; row++) {
            positions.add(new Position(row, 0));
        }
        return positions;
    }
    
    private List<Position> getRightEdgePositions(int size) {
        List<Position> positions = new ArrayList<>();
        for (int row = 0; row < size; row++) {
            positions.add(new Position(row, size - 1));
        }
        return positions;
    }

}