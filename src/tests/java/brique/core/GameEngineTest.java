package brique.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class GameEngineTest {

    private GameEngine engine;

    @BeforeEach
    void setUp() {
        engine = new GameEngine(4); // use a small board for tests
    }

    @Nested
    @DisplayName("Initialization Tests")
    class InitializationTests {
        @Test
        @DisplayName("Should initialize with correct board size and current player")
        void shouldInitializeWithCorrectBoardSizeAndPlayer() {
            GameState state = engine.getState();
            assertThat(state).isNotNull();
            assertThat(state.getBoard()).isNotNull();
            assertThat(state.getBoard().getSize()).isEqualTo(4);
            assertThat(state.getCurrentPlayer()).isEqualTo(Stone.BLACK);
            assertThat(state.isPieRuleAvailable()).isTrue();
        }
    }

    @Nested
    @DisplayName("Play Move Tests")
    class PlayMoveTests {
        @Test
        @DisplayName("Should play a valid move and update state")
        void shouldPlayValidMoveAndUpdateState() {
            Position pos = new Position(0, 0);
            boolean result = engine.playMove(pos);
            assertThat(result).isTrue();

            GameState state = engine.getState();
            // Stone should be placed on the board
            assertThat(state.getBoard().getStone(pos)).isEqualTo(Stone.BLACK);
            // Move history should contain the move
            assertThat(state.getMoveHistory()).hasSize(1);
            Move recorded = state.getMoveHistory().get(0);
            assertThat(recorded.getPosition()).isEqualTo(pos);
            assertThat(recorded.getStone()).isEqualTo(Stone.BLACK);
            // Player should switch to White
            assertThat(state.getCurrentPlayer()).isEqualTo(Stone.WHITE);
        }

        @Test
        @DisplayName("Should reject move on occupied square")
        void shouldRejectMoveOnOccupiedSquare() {
            Position pos = new Position(1, 1);
            // First move is valid
            assertThat(engine.playMove(pos)).isTrue();
            GameState state = engine.getState();
            assertThat(state.getBoard().getStone(pos)).isEqualTo(Stone.BLACK);
            assertThat(state.getCurrentPlayer()).isEqualTo(Stone.WHITE);
            int historySize = state.getMoveHistory().size();

            // Attempt to play on the same square again (now White)
            boolean secondResult = engine.playMove(pos);
            // The move should be invalid and ignored
            assertThat(secondResult).isFalse();
            // Board should remain unchanged
            assertThat(state.getBoard().getStone(pos)).isEqualTo(Stone.BLACK);
            // Move history should not have grown
            assertThat(state.getMoveHistory()).hasSize(historySize);
            // Current player should remain White since no valid move was played
            assertThat(state.getCurrentPlayer()).isEqualTo(Stone.WHITE);
        }

        @Test
        @DisplayName("Should apply escort rule and fill squares")
        void shouldApplyEscortRuleAndFillSquares() {
            // Arrange: manually occupy escorts of (1,1) on a 3x3 board
            engine = new GameEngine(3);
            Board board = engine.getState().getBoard();
            // (1,1) is a light square (1+1=2), escorts are (0,1) and (1,0)
            board.setStone(new Position(0, 1), Stone.BLACK);
            board.setStone(new Position(1, 0), Stone.BLACK);

            // Act: play a move somewhere else to trigger the fill
            boolean result = engine.playMove(new Position(2, 2));
            assertThat(result).isTrue();

            // Assert: (1,1) should be filled with a black stone due to escort rule
            assertThat(board.getStone(new Position(1, 1))).isEqualTo(Stone.BLACK);
            // Current player should now be White
            assertThat(engine.getState().getCurrentPlayer()).isEqualTo(Stone.WHITE);
        }

        @Test
        @DisplayName("Should detect win condition and end game")
        void shouldDetectWinConditionAndEndGame() {
            // Arrange: set up a nearly complete vertical connection for Black on col 0
            engine = new GameEngine(3);
            Board board = engine.getState().getBoard();
            // occupy rows 1 and 2 in col 0 with black stones
            board.setStone(new Position(1, 0), Stone.BLACK);
            board.setStone(new Position(2, 0), Stone.BLACK);
            // It's Black's turn by default

            // Act: place the final connecting stone at the top (0,0)
            boolean result = engine.playMove(new Position(0, 0));
            assertThat(result).isTrue();

            // Assert: game should be over and Black should be winner
            assertThat(engine.isGameOver()).isTrue();
            assertThat(engine.getState().getWinner()).isEqualTo(Stone.BLACK);

            // Subsequent attempts to play should throw
            assertThatThrownBy(() -> engine.playMove(new Position(2, 2)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("game has ended");
        }
    }

    @Nested
    @DisplayName("Pie Rule Tests")
    class PieRuleTests {
        @Test
        @DisplayName("Should apply pie rule after first move")
        void shouldApplyPieRuleAfterFirstMove() {
            // Play Black's first move
            Position firstMove = new Position(1, 1);
            assertThat(engine.playMove(firstMove)).isTrue();

            // Now it's White's first turn and pie rule is available
            assertThat(engine.getState().getCurrentPlayer()).isEqualTo(Stone.WHITE);
            assertThat(engine.getState().isPieRuleAvailable()).isTrue();

            // Apply pie rule
            engine.getState().applyPieRule();

            // The stone at (1,1) should now be White after swap
            assertThat(engine.getState().getBoard().getStone(firstMove)).isEqualTo(Stone.WHITE);
            // Pie rule should be disabled
            assertThat(engine.getState().isPieRuleAvailable()).isFalse();
            // It should still be Black's turn
            assertThat(engine.getState().getCurrentPlayer()).isEqualTo(Stone.BLACK);
        }

        @Test
        @DisplayName("Should not allow pie rule before any move")
        void shouldNotAllowPieRuleBeforeAnyMove() {
            // It is Black's turn; applying pie rule should throw because only White can swap on first turn
            assertThatThrownBy(() -> engine.getState().applyPieRule())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Pie rule");
        }

        @Test
        @DisplayName("Should not allow pie rule after White plays normally")
        void shouldNotAllowPieRuleAfterWhitePlaysNormally() {
            // Black's first move
            assertThat(engine.playMove(new Position(0, 0))).isTrue();
            // White plays instead of swapping
            assertThat(engine.playMove(new Position(1, 1))).isTrue();
            // Pie rule should now be disabled
            assertThat(engine.getState().isPieRuleAvailable()).isFalse();
            // Attempting to apply pie rule should fail
            assertThatThrownBy(() -> engine.getState().applyPieRule())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Pie rule");
        }

        @Test
        @DisplayName("Should not allow pie rule after it has been used")
        void shouldNotAllowPieRuleAfterItHasBeenUsed() {
            // First move
            assertThat(engine.playMove(new Position(2, 2))).isTrue();
            // Apply pie rule
            engine.getState().applyPieRule();
            // Attempt to apply again should fail
            assertThatThrownBy(() -> engine.getState().applyPieRule())
                .isInstanceOf(IllegalStateException.class);
        }
    }
}