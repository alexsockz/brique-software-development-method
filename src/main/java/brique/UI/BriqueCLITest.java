package brique.ui;

import brique.core.Position;
import brique.core.Stone;
import brique.engine.GameEngine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class BriqueCLITest {

    @Nested
    @DisplayName("Normal Play Scenarios")
    class NormalPlayTests {
        @Test
        @DisplayName("Should process a sequence of moves and quit on user command")
        void shouldProcessMovesAndQuit() {
            GameEngine engine = new GameEngine(3);
            TestIO io = new TestIO();
            // Sequence: Black plays 0 0, White plays 1 1, then quit
            io.addInput("0 0");
            io.addInput("1 1");
            io.addInput("quit");
            BriqueCLI cli = new BriqueCLI(engine, io, new AsciiBoardRenderer());
            cli.start();

            // Check game state: after two moves, board should reflect both
            assertThat(engine.getState().getBoard().getStone(new Position(0, 0))).isEqualTo(Stone.BLACK);
            assertThat(engine.getState().getBoard().getStone(new Position(1, 1))).isEqualTo(Stone.WHITE);
            // Game should be aborted after quit command
            assertThat(engine.isGameOver()).isTrue();
            assertThat(engine.getWinner()).isEqualTo(Stone.EMPTY);

            // Check that some expected messages were output
            assertThat(io.getOutputs()).anyMatch(line -> line.contains("Current player: WHITE"));
            assertThat(io.getOutputs()).anyMatch(line -> line.contains("Game over."));
        }

        @Test
        @DisplayName("Should detect win and announce winner")
        void shouldDetectWinAndAnnounceWinner() {
            GameEngine engine = new GameEngine(3);
            TestIO io = new TestIO();
            // Sequence: black 0 0, white 0 1, black 1 0, white 0 2, black 2 0 -> black wins
            io.addInput("0 0");
            io.addInput("0 1");
            io.addInput("1 0");
            io.addInput("0 2");
            io.addInput("2 0");
            BriqueCLI cli = new BriqueCLI(engine, io, new AsciiBoardRenderer());
            cli.start();

            // Game should be over with black as winner
            assertThat(engine.isGameOver()).isTrue();
            assertThat(engine.getWinner()).isEqualTo(Stone.BLACK);

            // Output should contain the winner announcement
            assertThat(io.getOutputs()).anyMatch(line -> line.contains("Winner: BLACK"));
        }
    }

    @Nested
    @DisplayName("Pie Rule Scenarios")
    class PieRuleTests {
        @Test
        @DisplayName("Should apply pie rule when user enters swap")
        void shouldApplyPieRuleWhenUserEntersSwap() {
            GameEngine engine = new GameEngine(3);
            TestIO io = new TestIO();
            // Black plays first move at 1 1, then white swaps, then quit
            io.addInput("1 1");
            io.addInput("swap");
            io.addInput("quit");
            BriqueCLI cli = new BriqueCLI(engine, io, new AsciiBoardRenderer());
            cli.start();

            // After swap, the stone at (1,1) should be white
            assertThat(engine.getState().getBoard().getStone(new Position(1, 1))).isEqualTo(Stone.WHITE);
            // Pie rule should no longer be available
            assertThat(engine.getState().ispieRuleAvailable()).isFalse();
            // Current player should still be White (original black now plays as White)
            assertThat(engine.getState().getCurrentPlayer()).isEqualTo(Stone.WHITE);
            // Game aborted due to quit command
            assertThat(engine.isGameOver()).isTrue();
            // Output should mention that swap occurred (error message not expected)
            assertThat(io.getOutputs()).noneMatch(line -> line.contains("Cannot apply pie rule"));
        }
    }

    @Nested
    @DisplayName("Invalid Input Handling")
    class InvalidInputTests {
        @Test
        @DisplayName("Should handle invalid move and prompt again")
        void shouldHandleInvalidMoveAndPromptAgain() {
            GameEngine engine = new GameEngine(3);
            TestIO io = new TestIO();
            // Black tries to play outside board, then plays valid, then quit
            io.addInput("10 10");
            io.addInput("0 0");
            io.addInput("quit");
            BriqueCLI cli = new BriqueCLI(engine, io, new AsciiBoardRenderer());
            cli.start();

            // After invalid input, board should remain empty until valid move
            assertThat(engine.getState().getBoard().getStone(new Position(0, 0))).isEqualTo(Stone.BLACK);
            // Invalid move should not change current player (still Black after invalid attempt)
            // Actually our CLI uses engine.playMove directly; invalid returns false but we don't switch player; current stays Black until valid move (0,0)
            // After valid move, current player becomes White and game aborted by quit
            assertThat(engine.getState().getCurrentPlayer()).isEqualTo(Stone.WHITE);
            // Check that an invalid message was output
            assertThat(io.getOutputs()).anyMatch(line -> line.contains("Invalid move"));
        }
    }
}