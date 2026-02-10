package brique.exceptions;

// Exception used to signal that the current turn should be skipped
// without terminating the game (e.g. swap command, empty input).
public class SkipTurn extends RuntimeException {

    public SkipTurn() {
        super();
    }

    public SkipTurn(String message) {
        super(message);
    }
}
