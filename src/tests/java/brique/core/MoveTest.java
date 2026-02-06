package brique.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

class MoveTest {
    
    @Test
    @DisplayName("Should create move result with primary move")
    void shouldCreateMoveResultWithPrimaryMove() {
        Move move = new Move(new Position(3, 3), Stone.BLACK);
        MoveResult result = new MoveResult(move);
        
        assertThat(result.getPrimaryMove()).isEqualTo(move);
        assertThat(result.getCapturedPositions()).isEmpty();
        assertThat(result.getFilledPositions()).isEmpty();
    }
    
    @Test
    @DisplayName("Should add captured positions")
    void shouldAddCapturedPositions() {
        Move move = new Move(new Position(3, 3), Stone.BLACK);
        MoveResult result = new MoveResult(move);
        
        Position captured1 = new Position(2, 2);
        Position captured2 = new Position(4, 4);
        
        result.addCapturedPosition(captured1);
        result.addCapturedPosition(captured2);
        
        assertThat(result.getCapturedPositions())
            .hasSize(2)
            .contains(captured1, captured2);
    }
    
    @Test
    @DisplayName("Should add filled positions")
    void shouldAddFilledPositions() {
        Move move = new Move(new Position(3, 3), Stone.BLACK);
        MoveResult result = new MoveResult(move);
        
        Position filled1 = new Position(2, 3);
        Position filled2 = new Position(3, 2);
        
        result.addFilledPosition(filled1);
        result.addFilledPosition(filled2);
        
        assertThat(result.getFilledPositions())
            .hasSize(2)
            .contains(filled1, filled2);
    }
}
