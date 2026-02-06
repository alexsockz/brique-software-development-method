package brique.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

class MoveTest {
    
    @Test
    @DisplayName("Should create move with empty updates")
    void shouldCreateMoveWithEmptyUpdates() {
        Move move = new Move(new Position(3, 3), Stone.BLACK);

        assertThat(move.getPosition()).isEqualTo(new Position(3, 3));
        assertThat(move.getStone()).isEqualTo(Stone.BLACK);
        assertThat(move.getCapturedPositions()).isEmpty();
        assertThat(move.getFilledPositions()).isEmpty();
    }
    
    @Test
    @DisplayName("Should add captured positions to move")
    void shouldAddCapturedPositions() {
        Move move = new Move(new Position(3, 3), Stone.BLACK);
        
        Position captured1 = new Position(2, 2);
        Position captured2 = new Position(4, 4);
        
        move.addCapturedPosition(captured1);
        move.addCapturedPosition(captured2);
        
        assertThat(move.getCapturedPositions())
            .hasSize(2)
            .contains(captured1, captured2);
    }
    
    @Test
    @DisplayName("Should add filled positions to move")
    void shouldAddFilledPositions() {
        Move move = new Move(new Position(3, 3), Stone.BLACK);
        
        Position filled1 = new Position(2, 3);
        Position filled2 = new Position(3, 2);
        
        move.addFilledPosition(filled1);
        move.addFilledPosition(filled2);
        
        assertThat(move.getFilledPositions())
            .hasSize(2)
            .contains(filled1, filled2);
    }
}
