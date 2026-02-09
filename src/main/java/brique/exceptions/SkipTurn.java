package brique.exceptions;

public class SkipTurn extends RuntimeException{
    public SkipTurn() {
        super();
    }
    public SkipTurn(String message) {
        super(message);
    }
}
