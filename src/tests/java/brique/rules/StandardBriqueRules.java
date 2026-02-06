package brique.rules;

import brique.core.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class StandardBriqueRulesTest {
    
    private StandardBriqueRules rules;
    private GameState gameState;
    
    @BeforeEach
    void setUp() {
        rules = new StandardBriqueRules();
        gameState = new GameState(8);
    }
    
    @Nested
    @DisplayName("Move Validation Tests")
    class MoveValidationTests {
        
        @Test
        @DisplayName("Should validate move on empty position")
        void shouldValidateMoveOnEmptyPosition() {
            Move move = new Move(new Position(3, 3), Stone.BLACK);
            
            assertThat(rules.isValidMove(gameState, move)).isTrue();
        }
        
        @Test
        @DisplayName("Should reject move on occupied position")
        void shouldRejectMoveOnOccupiedPosition() {
            Position pos = new Position(3, 3);
            gameState.getBoard().setStone(pos, Stone.BLACK);
            
            Move move = new Move(pos, Stone.WHITE);
            
            assertThat(rules.isValidMove(gameState, move)).isFalse();
        }
        
        @Test
        @DisplayName("Should reject move outside board")
        void shouldRejectMoveOutsideBoard() {
            Move move = new Move(new Position(10, 10), Stone.BLACK);
            
            assertThat(rules.isValidMove(gameState, move)).isFalse();
        }
        
        @Test
        @DisplayName("Should reject move by wrong player")
        void shouldRejectMoveByWrongPlayer() {
            Move move = new Move(new Position(3, 3), Stone.WHITE);
            // Current player is BLACK
            
            assertThat(rules.isValidMove(gameState, move)).isFalse();
        }
    }
    
    @Nested
    @DisplayName("Escort Tests")
    class EscortTests {
        
        @Test
        @DisplayName("Should get escorts for light square")
        void shouldGetEscortsForLightSquare() {
            // Position (2, 2) is a light square (sum is even)
            Position lightSquare = new Position(2, 2);
            
            List<Position> escorts = rules.getEscorts(lightSquare, gameState.getBoard());
            
            assertThat(escorts).hasSize(2);
            assertThat(escorts).containsExactlyInAnyOrder(
                new Position(1, 2), // Front
                new Position(2, 1)  // Left
            );
        }
        
        @Test
        @DisplayName("Should get escorts for dark square")
        void shouldGetEscortsForDarkSquare() {
            // Position (2, 3) is a dark square (sum is odd)
            Position darkSquare = new Position(2, 3);
            
            List<Position> escorts = rules.getEscorts(darkSquare, gameState.getBoard());
            
            assertThat(escorts).hasSize(2);
            assertThat(escorts).containsExactlyInAnyOrder(
                new Position(3, 3), // Behind
                new Position(2, 4)  // Right
            );
        }
        
        @Test
        @DisplayName("Should get one escort for edge square")
        void shouldGetOneEscortForEdgeSquare() {
            // Corner position
            Position corner = new Position(0, 0);
            
            List<Position> escorts = rules.getEscorts(corner, gameState.getBoard());
            
            assertThat(escorts).hasSize(0); // Top-left corner has no valid escorts
        }
        
        @Test
        @DisplayName("Should handle edge cases for escorts")
        void shouldHandleEdgeCasesForEscorts() {
            Position topEdge = new Position(0, 2);
            List<Position> escorts = rules.getEscorts(topEdge, gameState.getBoard());
            
            // Only left escort is valid (front would be off-board)
            assertThat(escorts).hasSize(1);
            assertThat(escorts).contains(new Position(0, 1));
        }
    }
    
    @Nested
    @DisplayName("Move Execution Tests")
    class MoveExecutionTests {
        
        @Test
        @DisplayName("Should place stone at position")
        void shouldPlaceStoneAtPosition() {
            Position pos = new Position(3, 3);
            Move move = new Move(pos, Stone.BLACK);
            
            rules.executeMove(gameState, move);
            
            assertThat(gameState.getBoard().getStone(pos)).isEqualTo(Stone.BLACK);
        }
        
        @Test
        @DisplayName("Should fill positions when escorts are occupied")
        void shouldFillPositionsWhenEscortsAreOccupied() {
            Board board = gameState.getBoard();
            
            // Set up a situation where two escorts are occupied
            // Position (2, 2) is light, escorts are (1, 2) and (2, 1)
            board.setStone(new Position(1, 2), Stone.BLACK);
            board.setStone(new Position(2, 1), Stone.BLACK);
            
            // Place stone that doesn't directly fill (2,2) but triggers check
            Move move = new Move(new Position(0, 0), Stone.BLACK);
            MoveResult result = rules.executeMove(gameState, move);
            
            // After the move, (2,2) should be filled
            assertThat(board.getStone(new Position(2, 2))).isEqualTo(Stone.BLACK);
            assertThat(result.getFilledPositions()).contains(new Position(2, 2));
        }
        
        @Test
        @DisplayName("Should capture enemy stone when filling")
        void shouldCaptureEnemyStoneWhenFilling() {
            Board board = gameState.getBoard();
            
            // Place white stone at position that will be filled
            Position toFill = new Position(2, 2);
            board.setStone(toFill, Stone.WHITE);
            
            // Set up escorts
            board.setStone(new Position(1, 2), Stone.BLACK);
            board.setStone(new Position(2, 1), Stone.BLACK);
            
            Move move = new Move(new Position(0, 0), Stone.BLACK);
            MoveResult result = rules.executeMove(gameState, move);
            
            assertThat(result.getCapturedPositions()).contains(toFill);
            assertThat(board.getStone(toFill)).isEqualTo(Stone.BLACK);
        }
    }
    
    @Nested
    @DisplayName("Win Condition Tests")
    class WinConditionTests {
        
        @Test
        @DisplayName("Should detect BLACK win with vertical connection")
        void shouldDetectBlackWinWithVerticalConnection() {
            Board board = gameState.getBoard();
            
            // Create vertical connection from top to bottom
            for (int row = 0; row < 8; row++) {
                board.setStone(new Position(row, 3), Stone.BLACK);
            }
            
            assertThat(rules.checkWinCondition(gameState, Stone.BLACK)).isTrue();
        }
        
        @Test
        @DisplayName("Should detect WHITE win with horizontal connection")
        void shouldDetectWhiteWinWithHorizontalConnection() {
            Board board = gameState.getBoard();
            
            // Create horizontal connection from left to right
            for (int col = 0; col < 8; col++) {
                board.setStone(new Position(3, col), Stone.WHITE);
            }
            
            assertThat(rules.checkWinCondition(gameState, Stone.WHITE)).isTrue();
        }
        
        @Test
        @DisplayName("Should not detect win with incomplete path")
        void shouldNotDetectWinWithIncompletePath() {
            Board board = gameState.getBoard();
            
            // Create incomplete vertical connection (missing one stone)
            for (int row = 0; row < 7; row++) {
                board.setStone(new Position(row, 3), Stone.BLACK);
            }
            
            assertThat(rules.checkWinCondition(gameState, Stone.BLACK)).isFalse();
        }
        
        @Test
        @DisplayName("Should detect win with zigzag path")
        void shouldDetectWinWithZigzagPath() {
            Board board = gameState.getBoard();
            
            // Create zigzag connection from top to bottom
            board.setStone(new Position(0, 3), Stone.BLACK);
            board.setStone(new Position(1, 3), Stone.BLACK);
            board.setStone(new Position(2, 3), Stone.BLACK);
            board.setStone(new Position(2, 4), Stone.BLACK);
            board.setStone(new Position(3, 4), Stone.BLACK);
            board.setStone(new Position(4, 4), Stone.BLACK);
            board.setStone(new Position(5, 4), Stone.BLACK);
            board.setStone(new Position(6, 4), Stone.BLACK);
            board.setStone(new Position(7, 4), Stone.BLACK);
            
            assertThat(rules.checkWinCondition(gameState, Stone.BLACK)).isTrue();
        }
        
        @Test
        @DisplayName("Should not detect win for wrong player")
        void shouldNotDetectWinForWrongPlayer() {
            Board board = gameState.getBoard();
            
            // Create vertical connection for BLACK
            for (int row = 0; row < 8; row++) {
                board.setStone(new Position(row, 3), Stone.BLACK);
            }
            
            // Check for WHITE win
            assertThat(rules.checkWinCondition(gameState, Stone.WHITE)).isFalse();
        }
    }
}
