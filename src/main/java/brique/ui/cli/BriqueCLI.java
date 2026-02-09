package brique.ui.cli;

import brique.core.Position;
import brique.core.Stone;
import brique.exceptions.*;
import brique.ui.BoardRendererInterface;
import brique.ui.IOHandlerInterface;
import brique.core.GameEngine;

public class BriqueCLI {
    private final GameEngine engine;
    private final IOHandlerInterface io;
    private final BoardRendererInterface renderer;
    private volatile boolean running;

    public BriqueCLI() {
        this(new ConsoleIO(), new AsciiBoardRenderer());
    }

    public BriqueCLI(GameEngine engine, IOHandlerInterface io, BoardRendererInterface renderer) {
        this.engine = engine;
        this.io = io;
        this.renderer = renderer;
        this.running = false;
    }

    public BriqueCLI(IOHandlerInterface io, BoardRendererInterface renderer) {
        int size = 11;
        io.writeLine("Welcome to Brique! please enter board size:");
        String input = io.readLine();
        if (input != null && !input.isEmpty()) {
            try {
                size = Integer.parseInt(input);

                if (size <= 0) {
                    throw new NumberFormatException("non positive num");
                }

            } catch (NumberFormatException e) {
                io.writeLine("Invalid board size provided; using default size of 11.");
            }
        }
        this.engine = new GameEngine(size);
        this.io = io;
        this.renderer = renderer;
        this.running = false;
    }

    public void start() {
        running = true;
        String input;
        String[] parts;
        while (running && !engine.isGameOver()) {
            // Show the current state of the board
            io.writeLine(renderer.render(engine.getState().getBoard()));

            // Prompt the user for action
            Stone current = engine.getState().getCurrentPlayer();
            if (current == Stone.WHITE && engine.getState().isPieRuleAvailable()) {
                io.writeLine("Current player: WHITE (swap available)");
                io.writeLine("Enter 'swap' to apply pie rule or specify move as 'row col', or 'quit' to exit:");
            } else {
                io.writeLine("Current player: " + current);
                io.writeLine("Enter move as 'row col', or 'quit' to exit:");
            }
            input = io.readLine();

            try {
                parts = checkInput(input);
                int row = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]);
                // Send move to engine
                boolean moveResult;
                moveResult = engine.playMove(new Position(row, col));
                if (!moveResult) {
                    io.writeLine("Invalid move. Try again.");
                }
            } catch (AbortGame | IllegalStateException e) {
                engine.getState().abort();
                io.writeLine("Game is over: " + e.getMessage());
                break;
            }
            catch (NumberFormatException nfe) {
                io.writeLine("Invalid numbers. Please enter numeric row and column.");
            }
            catch (SkipTurn e) {
                io.writeLine(e.getMessage());
            }
        }
        concludeGame();
    }

    public void concludeGame() {
        // Game concluded: display final state and winner if any
        io.writeLine(renderer.render(engine.getState().getBoard()));
        if (engine.isGameOver()) {
            Stone winner = engine.getState().getWinner();
            if (winner != Stone.EMPTY) {
                io.writeLine("Game over. Winner: " + winner);
            } else {
                io.writeLine("Game over. No winner.");
            }
        }
        running = false;
    }

    private String[] checkInput(String input) throws AbortGame {

        if (input == null) {
            // Input stream closed; abort game
            throw new AbortGame("input stream closed");
        }
        input = input.trim();
        if (input.equalsIgnoreCase("quit")) {

            throw new AbortGame("quit the game");
        }
        if (input.equalsIgnoreCase("swap")) {
            String message="pie rule applied";
            try {
                engine.getState().applyPieRule();
            } catch (Exception e) {
                message="Cannot apply pie rule: " + e.getMessage();
            }
            throw new SkipTurn(message);
        }
        if (input.isEmpty()) {
            
            throw new SkipTurn("No input detected. Please enter a command.");
        }
        // Parse row and column
        String[] parts = input.split("\\s+");
        if (parts.length != 2) {
            throw new SkipTurn("Invalid input. Please enter row and column separated by space.");
        }
        return parts;
    }
}
