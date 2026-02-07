package brique.ui;

import brique.core.Position;
import brique.core.Stone;
import brique.core.GameEngine;

public class BriqueCLI {
    private final GameEngine engine;
    private final IOHandlerInterface io;
    private final BoardRendererInterface renderer;
    private volatile boolean running;

    public BriqueCLI(GameEngine engine) {
        this(engine, new ConsoleIO(), new AsciiBoardRenderer());
    }

    public BriqueCLI(GameEngine engine, IOHandlerInterface io, BoardRendererInterface renderer) {
        this.engine = engine;
        this.io = io;
        this.renderer = renderer;
        this.running = false;
    }

    public void start() {
        running = true;
        io.writeLine("Welcome to Brique!");
        while (running && !engine.isGameOver()) {
            // Show the current state of the board
            io.writeLine(renderer.render(engine.getState().getBoard()));
            // Prompt the user for action
            Stone current = engine.getState().getCurrentPlayer();
            if (current == Stone.WHITE && engine.getState().ispieRuleAvailable()) {
                io.writeLine("Current player: WHITE (swap available)");
                io.writeLine("Enter 'swap' to apply pie rule or specify move as 'row col', or 'quit' to exit:");
            } else {
                io.writeLine("Current player: " + current);
                io.writeLine("Enter move as 'row col', or 'quit' to exit:");
            }
            String input = io.readLine();
            if (input == null) {
                // Input stream closed; abort game
                engine.getState().abort();
                break;
            }
            input = input.trim();
            if (input.equalsIgnoreCase("quit")) {
                engine.getState().abort();
                break;
            }
            if (input.equalsIgnoreCase("swap")) {
                try {
                    engine.applyPieRule();
                } catch (Exception e) {
                    io.writeLine("Cannot apply pie rule: " + e.getMessage());
                }
                continue;
            }
            if (input.isEmpty()) {
                io.writeLine("No input detected. Please enter a command.");
                continue;
            }
            // Parse row and column
            String[] parts = input.split("\\s+");
            if (parts.length != 2) {
                io.writeLine("Invalid input. Please enter row and column separated by space.");
                continue;
            }
            try {
                int row = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]);
                // Send move to engine
                boolean moveResult;
                try {
                    moveResult = engine.playMove(new Position(row, col));
                } catch (IllegalStateException e) {
                    io.writeLine("Game is over: " + e.getMessage());
                    break;
                }
                if (!moveResult) {
                    io.writeLine("Invalid move. Try again.");
                }
            } catch (NumberFormatException nfe) {
                io.writeLine("Invalid numbers. Please enter numeric row and column.");
            }
        }

        // Game concluded: display final state and winner if any
        io.writeLine(renderer.render(engine.getState().getBoard()));
        if (engine.isGameOver()) {
            Stone winner = engine.getWinner();
            if (winner != Stone.EMPTY) {
                io.writeLine("Game over. Winner: " + winner);
            } else {
                io.writeLine("Game over. No winner.");
            }
        }
        running = false;
    }
}