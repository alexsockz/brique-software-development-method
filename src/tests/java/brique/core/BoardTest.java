package brique.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    
    private Board board;
    private static final int DEFAULT_SIZE = 19;
    
    @BeforeEach
    void setUp() {
        board = new Board(DEFAULT_SIZE);
    }
    
    @Nested
    @DisplayName("Constructor and Initialization Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("Should create board with correct size")
        void shouldCreateBoardWithCorrectSize() {
            assertEquals(DEFAULT_SIZE, board.getSize());
        }
        
        @ParameterizedTest
        @ValueSource(ints = {9, 13, 19, 21})
        @DisplayName("Should create boards of various sizes")
        void shouldCreateBoardsOfVariousSizes(int size) {
            Board testBoard = new Board(size);
            assertEquals(size, testBoard.getSize());
        }
        
        @Test
        @DisplayName("Should initialize all positions as EMPTY")
        void shouldInitializeAllPositionsAsEmpty() {
            for (int row = 0; row < DEFAULT_SIZE; row++) {
                for (int col = 0; col < DEFAULT_SIZE; col++) {
                    Position pos = new Position(row, col);
                    assertEquals(Stone.EMPTY, board.getStone(pos));
                }
            }
        }
    }
    
    @Nested
    @DisplayName("Stone Placement Tests")
    class StonePlacementTests {
        
        @Test
        @DisplayName("Should set and get stone at position")
        void shouldSetAndGetStone() {
            Position pos = new Position(5, 10);
            board.setStone(pos, Stone.BLACK);
            assertEquals(Stone.BLACK, board.getStone(pos));
        }
        
        @Test
        @DisplayName("Should overwrite existing stone")
        void shouldOverwriteExistingStone() {
            Position pos = new Position(3, 7);
            board.setStone(pos, Stone.BLACK);
            board.setStone(pos, Stone.WHITE);
            assertEquals(Stone.WHITE, board.getStone(pos));
        }
        
        @Test
        @DisplayName("Should set stone back to EMPTY")
        void shouldSetStoneBackToEmpty() {
            Position pos = new Position(8, 12);
            board.setStone(pos, Stone.BLACK);
            board.setStone(pos, Stone.EMPTY);
            assertEquals(Stone.EMPTY, board.getStone(pos));
        }
        
        @Test
        @DisplayName("Should handle corner positions")
        void shouldHandleCornerPositions() {
            Position topLeft = new Position(0, 0);
            Position topRight = new Position(0, DEFAULT_SIZE - 1);
            Position bottomLeft = new Position(DEFAULT_SIZE - 1, 0);
            Position bottomRight = new Position(DEFAULT_SIZE - 1, DEFAULT_SIZE - 1);
            
            board.setStone(topLeft, Stone.BLACK);
            board.setStone(topRight, Stone.WHITE);
            board.setStone(bottomLeft, Stone.WHITE);
            board.setStone(bottomRight, Stone.BLACK);
            
            assertEquals(Stone.BLACK, board.getStone(topLeft));
            assertEquals(Stone.WHITE, board.getStone(topRight));
            assertEquals(Stone.WHITE, board.getStone(bottomLeft));
            assertEquals(Stone.BLACK, board.getStone(bottomRight));
        }
    }
    
    @Nested
    @DisplayName("Position Validation Tests")
    class PositionValidationTests {
        
        @ParameterizedTest
        @CsvSource({
            "0, 0",
            "0, 18",
            "18, 0",
            "18, 18",
            "9, 9",
            "5, 10"
        })
        @DisplayName("Should validate valid positions")
        void shouldValidateValidPositions(int row, int col) {
            Position pos = new Position(row, col);
            assertTrue(board.isValidPosition(pos));
        }
        
        @ParameterizedTest
        @CsvSource({
            "-1, 0",
            "0, -1",
            "-1, -1",
            "19, 0",
            "0, 19",
            "19, 19",
            "20, 20",
            "100, 100"
        })
        @DisplayName("Should invalidate out-of-bounds positions")
        void shouldInvalidateOutOfBoundsPositions(int row, int col) {
            Position pos = new Position(row, col);
            assertFalse(board.isValidPosition(pos));
        }
        
        @Test
        @DisplayName("Should validate boundary positions correctly")
        void shouldValidateBoundaryPositions() {
            assertTrue(board.isValidPosition(new Position(0, 0)));
            assertTrue(board.isValidPosition(new Position(DEFAULT_SIZE - 1, DEFAULT_SIZE - 1)));
            assertFalse(board.isValidPosition(new Position(-1, 0)));
            assertFalse(board.isValidPosition(new Position(DEFAULT_SIZE, 0)));
        }
    }
    
    @Nested
    @DisplayName("Board Copy Tests")
    class BoardCopyTests {
        
        @Test
        @DisplayName("Should create independent copy of board")
        void shouldCreateIndependentCopy() {
            Position pos1 = new Position(5, 5);
            Position pos2 = new Position(10, 10);
            
            board.setStone(pos1, Stone.BLACK);
            board.setStone(pos2, Stone.WHITE);
            
            Board copy = board.copy();
            
            // Verify copy has same values
            assertEquals(board.getStone(pos1), copy.getStone(pos1));
            assertEquals(board.getStone(pos2), copy.getStone(pos2));
            assertEquals(board.getSize(), copy.getSize());
            
            // Modify original
            board.setStone(pos1, Stone.WHITE);
            
            // Verify copy is unchanged
            assertEquals(Stone.BLACK, copy.getStone(pos1));
            assertEquals(Stone.WHITE, board.getStone(pos1));
        }
        
        @Test
        @DisplayName("Should copy all stones correctly")
        void shouldCopyAllStonesCorrectly() {
            // Place various stones
            for (int i = 0; i < DEFAULT_SIZE; i++) {
                board.setStone(new Position(i, i), Stone.BLACK);
                board.setStone(new Position(i, DEFAULT_SIZE - 1 - i), Stone.WHITE);
            }
            
            Board copy = board.copy();
            
            // Verify all stones match
            for (int row = 0; row < DEFAULT_SIZE; row++) {
                for (int col = 0; col < DEFAULT_SIZE; col++) {
                    Position pos = new Position(row, col);
                    assertEquals(board.getStone(pos), copy.getStone(pos));
                }
            }
        }
        
        @Test
        @DisplayName("Should create deep copy, not reference")
        void shouldCreateDeepCopy() {
            Board copy = board.copy();
            assertNotSame(board, copy);
        }
        
        @Test
        @DisplayName("Should copy empty board correctly")
        void shouldCopyEmptyBoardCorrectly() {
            Board copy = board.copy();
            
            for (int row = 0; row < DEFAULT_SIZE; row++) {
                for (int col = 0; col < DEFAULT_SIZE; col++) {
                    Position pos = new Position(row, col);
                    assertEquals(Stone.EMPTY, copy.getStone(pos));
                }
            }
        }
    }
    
    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle minimum board size")
        void shouldHandleMinimumBoardSize() {
            Board smallBoard = new Board(1);
            assertEquals(1, smallBoard.getSize());
            Position pos = new Position(0, 0);
            smallBoard.setStone(pos, Stone.BLACK);
            assertEquals(Stone.BLACK, smallBoard.getStone(pos));
        }
        
        @Test
        @DisplayName("Should handle large board size")
        void shouldHandleLargeBoardSize() {
            Board largeBoard = new Board(100);
            assertEquals(100, largeBoard.getSize());
            Position pos = new Position(50, 50);
            largeBoard.setStone(pos, Stone.WHITE);
            assertEquals(Stone.WHITE, largeBoard.getStone(pos));
        }
        
        @Test
        @DisplayName("Should handle multiple consecutive operations")
        void shouldHandleMultipleConsecutiveOperations() {
            Position pos = new Position(7, 7);
            
            for (int i = 0; i < 1000; i++) {
                Stone stone = (i % 2 == 0) ? Stone.BLACK : Stone.WHITE;
                board.setStone(pos, stone);
                assertEquals(stone, board.getStone(pos));
            }
        }
    }
}
