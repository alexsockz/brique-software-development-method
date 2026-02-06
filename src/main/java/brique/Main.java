package brique;

import brique.core.GameEngine;
import brique.ui.BriqueCLI;

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