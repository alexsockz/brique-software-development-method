package brique.ui;

import brique.core.Board;
import brique.core.Position;
import brique.core.Stone;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsciiBoardRendererTest {
    @Test
    void shouldRenderBoardWithCoordinatesAndStones() {
        Board board = new Board(3);
        board.setStone(new Position(0, 0), Stone.BLACK);
        board.setStone(new Position(1, 1), Stone.WHITE);

        AsciiBoardRenderer renderer = new AsciiBoardRenderer();
        String rendered = renderer.render(board);

        String expected = "   0 1 2 \n" +
                "0  B . . \n" +
                "1  . W . \n" +
                "2  . . . \n";

        assertThat(rendered).isEqualTo(expected);
    }
}
