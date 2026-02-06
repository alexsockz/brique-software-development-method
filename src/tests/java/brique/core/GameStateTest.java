package brique.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class GameStateEngineTest {

    private GameState state;

    @BeforeEach
    void setUp() {
        state = new GameState(5);
    }

    @Nested
    @DisplayName("Initialisation Tests")
    class InitialisationTests {
        @Test
        @DisplayName("Should start with Black to move and in-progress status")
        void shouldStartWithBlackAndInProgressStatus() {
            assertThat(state.getCurrentPlayer()).isEqualTo(Stone.BLACK);
            assertThat(state.getStatus()).isEqualTo(GameState.GameEnd.IN_PROGRESS);
            assertThat(state.ispieRuleAvailable()).isTrue();
            assertThat(state.getBoard()).isNotNull();
            assertThat(state.getBoard().getSize()).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("Player Switching Tests")
    class PlayerSwitchingTests {
        @Test
        @DisplayName("Should alternate players correctly")
        void shouldAlternatePlayersCorrectly() {
            assertThat(state.getCurrentPlayer()).isEqualTo(Stone.BLACK);
            state.switchPlayer();
            assertThat(state.getCurrentPlayer()).isEqualTo(Stone.WHITE);
            state.switchPlayer();
            assertThat(state.getCurrentPlayer()).isEqualTo(Stone.BLACK);
        }
    }

    @Nested
    @DisplayName("Pie Rule Tests")
    class PieRuleTests {
        @Test
        @DisplayName("Should disable pie rule when turned off")
        void shouldDisablePieRule() {
            assertThat(state.ispieRuleAvailable()).isTrue();
            state.turnOffPieRule();
            assertThat(state.ispieRuleAvailable()).isFalse();
        }
    }

    @Nested
    @DisplayName("Status Transition Tests")
    class StatusTransitionTests {
        @Test
        @DisplayName("Should declare winner correctly")
        void shouldDeclareWinnerCorrectly() {
            state.declareWinner(Stone.BLACK);
            assertThat(state.getStatus()).isEqualTo(GameState.GameEnd.BLACK_WON);
            state.declareWinner(Stone.WHITE);
            assertThat(state.getStatus()).isEqualTo(GameState.GameEnd.WHITE_WON);
        }

        @Test
        @DisplayName("Should abort game correctly")
        void shouldAbortGameCorrectly() {
            state.abort();
            assertThat(state.getStatus()).isEqualTo(GameState.GameEnd.ABORTED);
            assertThat(state.isInProgress()).isFalse();
        }
    }

    @Nested
    @DisplayName("Move History Tests")
    class MoveHistoryTests {
        @Test
        @DisplayName("Should record moves and expose unmodifiable history")
        void shouldRecordMovesAndExposeUnmodifiableHistory() {
            assertThat(state.getMoveHistory()).isEmpty();

            Move m1 = new Move(new Position(1, 1), Stone.BLACK);
            Move m2 = new Move(new Position(2, 2), Stone.WHITE);
            state.recordMove(m1);
            state.switchPlayer();
            state.recordMove(m2);

            assertThat(state.getMoveHistory()).hasSize(2);
            assertThat(state.getMoveHistory().get(0)).isEqualTo(m1);
            assertThat(state.getMoveHistory().get(1)).isEqualTo(m2);

            // Attempting to modify the returned list should throw
            assertThatThrownBy(() -> state.getMoveHistory().add(m1))
                .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}