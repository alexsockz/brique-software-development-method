package brique.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

class GameStateTest {
    
    private GameState gameState;
    
    @BeforeEach
    void setUp() {
        gameState = new GameState(8);
    }
    
    @Test
    @DisplayName("Should initialize with BLACK as first player")
    void shouldInitializeWithBlackAsFirstPlayer() {
        assertThat(gameState.getCurrentPlayer()).isEqualTo(Stone.BLACK);
    }
    
    @Test
    @DisplayName("Should initialize with IN_PROGRESS status")
    void shouldInitializeWithInProgressStatus() {
        assertThat(gameState.getStatus()).isEqualTo(GameStatus.IN_PROGRESS);
    }
    
    @Test
    @DisplayName("Should have pie rule available initially")
    void shouldHavePieRuleAvailableInitially() {
        assertThat(gameState.isPieRuleAvailable()).isTrue();
    }
    
    @Test
    @DisplayName("Should switch player correctly")
    void shouldSwitchPlayerCorrectly() {
        assertThat(gameState.getCurrentPlayer()).isEqualTo(Stone.BLACK);
        
        gameState.switchPlayer();
        assertThat(gameState.getCurrentPlayer()).isEqualTo(Stone.WHITE);
        
        gameState.switchPlayer();
        assertThat(gameState.getCurrentPlayer()).isEqualTo(Stone.BLACK);
    }
    
    @Test
    @DisplayName("Should disable pie rule when set")
    void shouldDisablePieRuleWhenSet() {
        gameState.setPieRuleAvailable(false);
        assertThat(gameState.isPieRuleAvailable()).isFalse();
    }
    
    @Test
    @DisplayName("Should change game status")
    void shouldChangeGameStatus() {
        gameState.setStatus(GameStatus.BLACK_WON);
        assertThat(gameState.getStatus()).isEqualTo(GameStatus.BLACK_WON);
    }
    
    @Test
    @DisplayName("Should record moves in history")
    void shouldRecordMovesInHistory() {
        Move move1 = new Move(new Position(0, 0), Stone.BLACK);
        Move move2 = new Move(new Position(1, 1), Stone.WHITE);
        
        MoveResult result1 = new MoveResult(move1);
        MoveResult result2 = new MoveResult(move2);
        
        gameState.recordMove(result1);
        gameState.recordMove(result2);
        
        assertThat(gameState.getMoveHistory()).hasSize(2);
        assertThat(gameState.getMoveHistory().get(0)).isEqualTo(result1);
        assertThat(gameState.getMoveHistory().get(1)).isEqualTo(result2);
    }
    
    @Test
    @DisplayName("Should provide access to board")
    void shouldProvideAccessToBoard() {
        Board board = gameState.getBoard();
        
        assertThat(board).isNotNull();
        assertThat(board.getSize()).isEqualTo(8);
    }
}
