package brique;

import brique.core.GameEngine;
import brique.ui.BriqueCLI;

/**
 * Entry point for launching the Brique command‑line application.  This
 * class creates a {@link GameEngine} with a board size specified on
 * the command line (defaulting to 11) and then starts a
 * {@link BriqueCLI} to handle user interaction.  To run the game from
 * the terminal, compile the project and execute this class with
 * {@code java brique.Main [boardSize]}.
 */
public final class Main {
    private Main() {
        // prevent instantiation
    }

    public static void main(String[] args) {
        int size = 11;
        if (args != null && args.length > 0) {
            try {
                size = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid board size provided; using default size of 11.");
            }
        }
        GameEngine engine = new GameEngine(size);
        BriqueCLI cli = new BriqueCLI(engine);
        cli.start();
    }
}