package brique.exceptions;

// Exception used to immediately terminate the current game.
// Typically thrown when the user quits or when input is no longer available.
public class AbortGame extends RuntimeException {

    public AbortGame() {
        super();
    }

    public AbortGame(String message) {
        super(message);
    }
}
