package brique.ui.cli;

import brique.core.GameEngine;
import brique.core.LocalGameEngine;
import brique.core.Position;
import brique.core.Stone;
import brique.ui.BoardRendererInterface;
import brique.ui.IOHandlerInterface;
import brique.ui.cli.BriqueCLI;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BriqueCLITest {

    @Test
    void shouldQuitGracefully() {
        FakeIO io = new FakeIO("quit");
        RecordingRenderer renderer = new RecordingRenderer();
        GameEngine engine = new LocalGameEngine(3);

        new BriqueCLI(engine, io, renderer).start();

        assertThat(engine.isGameOver()).isTrue();
        assertThat(io.writes).anyMatch(s -> s.contains("Game over. No winner."));
        assertThat(renderer.renderCount).isGreaterThanOrEqualTo(2); // at least loop + final
    }

    // @Test
    // void shouldReportInvalidNumbersAndContinue() {
    //     FakeIO io = new FakeIO("abc", "quit");
    //     RecordingRenderer renderer = new RecordingRenderer();
    //     GameEngine engine = new LocalGameEngine(3);

    //     new BriqueCLI(engine, io, renderer).start();

    //     assertThat(io.writes).anyMatch(s -> s.contains("Invalid numbers"));
    //     assertThat(io.writes).anyMatch(s -> s.contains("Game over. No winner."));
    // }

    @Test
    void shouldPlayValidMoveAndRecordIt() {
        FakeIO io = new FakeIO("0 0", "quit");
        RecordingRenderer renderer = new RecordingRenderer();
        GameEngine engine = new LocalGameEngine(3);

        new BriqueCLI(engine, io, renderer).start();

        assertThat(engine.getState().getBoard().getStone(new Position(0, 0))).isEqualTo(Stone.BLACK);
        assertThat(io.writes).noneMatch(s -> s.contains("Invalid move"));
        assertThat(renderer.renderCount).isGreaterThanOrEqualTo(2);
    }

    private static final class FakeIO implements IOHandlerInterface {
        private final Deque<String> inputs = new ArrayDeque<>();
        final List<String> writes = new ArrayList<>();

        FakeIO(String... lines) {
            for (String line : lines) {
                inputs.addLast(line);
            }
        }

        @Override
        public String readLine() {
            return inputs.isEmpty() ? null : inputs.removeFirst();
        }

        @Override
        public void writeLine(String message) {
            writes.add(message);
        }
    }

    private static final class RecordingRenderer implements BoardRendererInterface {
        int renderCount = 0;

        @Override
        public String render(brique.core.Board board) {
            renderCount++;
            return "RENDER";
        }
    }
}
