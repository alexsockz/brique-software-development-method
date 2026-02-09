
package brique.exceptions;

public class AbortGame extends RuntimeException{
    
    public AbortGame() {
        super();
    }

    public AbortGame(String message) {
        super(message);
    }
}