package brique.ui.cli;

import brique.core.Position;
import brique.core.Stone;
import brique.exceptions.*;
import brique.ui.BoardRendererInterface;
import brique.ui.IOHandlerInterface;
import brique.core.GameEngine;

public class BriqueCLI {

    // Core game engine that manages rules and state
    private final GameEngine engine;
    // Abstraction for input/output (console, mock, file, etc.)
    private final IOHandlerInterface io;
    // Responsible for rendering the board in text form
    private final BoardRendererInterface renderer;
    // Controls the lifecycle of the CLI loop (thread-safe)
    private volatile boolean running;

    // Constructs a CLI using default console I/O and ASCII rendering.
    // Prompts the user for a board size before starting.
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

        // Default board size
        int size = 11;

        // Ask the user for a board size
        io.writeLine("Welcome to Brique! please enter board size:");
        String input = io.readLine();

        // Try to parse user-provided size
        if (input != null && !input.isEmpty()) {
            try {
                size = Integer.parseInt(input);

                // Reject non-positive sizes
                if (size <= 0) {
                    throw new NumberFormatException("non positive num");
                }

            } catch (NumberFormatException e) {
                // Fall back to default size on invalid input
                io.writeLine("Invalid board size provided; using default size of 11.");
            }
        }

        // Create engine with validated board size
        this.engine = new GameEngine(size);
        this.io = io;
        this.renderer = renderer;
        this.running = false;
    }

    public void start() {
        running = true;
        String input;
        String[] parts;

        // Main game loop
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

            // Read user input
            input = io.readLine();

            try {
                // Validate and parse input
                parts = checkInput(input);
                int row = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]);
                
                // Send move to the engine
                boolean moveResult;
                moveResult = engine.playMove(new Position(row, col));

                // Inform user if move is illegal
                if (!moveResult) {
                    io.writeLine("Invalid move. Try again.");
                }
            } catch (AbortGame | IllegalStateException e) {
                // Game aborted due to quit or fatal error
                engine.getState().abort();
                io.writeLine("Game is over: " + e.getMessage());
                break;
            }
            catch (NumberFormatException nfe) {
                // Row or column was not numeric
                io.writeLine("Invalid numbers. Please enter numeric row and column.");
            }
            catch (SkipTurn e) {
                // Non-fatal command (swap, empty input, etc.)
                io.writeLine(e.getMessage());
            }
        }
        // Handle end-of-game cleanup and messaging
        concludeGame();
    }

    // Displays the final board state and winner, if any
    public void concludeGame() {

        // Game concluded: display final state and winner if any
        io.writeLine(renderer.render(engine.getState().getBoard()));

        // Announce winner or draw
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

        // End game if input stream is closed
        if (input == null) {
            throw new AbortGame("input stream closed");
        }


        input = input.trim();
        
        // User explicitly quits
        if (input.equalsIgnoreCase("quit")) {
            throw new AbortGame("quit the game");
        }

        // Apply pie rule (swap players)
        if (input.equalsIgnoreCase("swap")) {
            String message="pie rule applied";
            try {
                engine.getState().applyPieRule();
            } catch (Exception e) {
                message="Cannot apply pie rule: " + e.getMessage();
            }
            throw new SkipTurn(message);
        }

        // Ignore empty input
        if (input.isEmpty()) {
            throw new SkipTurn("No input detected. Please enter a command.");
        }

        // Expect exactly two values: row and column
        String[] parts = input.split("\\s+");
        if (parts.length != 2) {
            throw new SkipTurn("Invalid input. Please enter row and column separated by space.");
        }
        return parts;
    }
}
