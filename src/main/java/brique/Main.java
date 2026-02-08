package brique;

import brique.core.GameEngine;
import brique.ui.BriqueCLI;

public final class Main {
    private Main() {
        // prevent instantiation
    }

    public static void main(String[] args) {
            if(args[0].equalsIgnoreCase("cli")) {
                BriqueCLI cli = new BriqueCLI();
                cli.start();
            }
            else {
            System.out.println("GUI not yet implemented");
            }
    }
}