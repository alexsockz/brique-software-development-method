package brique.ui.cli;

import brique.core.Stone;
import brique.ui.BoardRendererInterface;
import brique.ui.gui.controller.ActionCommand;
import brique.core.GameEngine;
import brique.core.GameEngineFactory;
import brique.core.GameMode;


public class BriqueCLI {

    // Core game engine that manages rules and state
    private final GameEngine engine;
    // Abstraction for input/output (console, mock, file, etc.)
    private final ConsoleIO io;
    // Responsible for rendering the board in text form
    private final BoardRendererInterface renderer;
    // Controls the lifecycle of the CLI loop (thread-safe)
    private volatile boolean running;

    // Constructs a CLI using default console I/O and ASCII rendering.
    // Prompts the user for a board size before starting.
    public BriqueCLI() {
        this(new ConsoleIO(), new AsciiBoardRenderer());
    }

    public BriqueCLI(GameEngine engine, ConsoleIO io, BoardRendererInterface renderer) {
        this.engine = engine;
        this.io = io;
        this.renderer = renderer;
        this.running = false;
    }

    public BriqueCLI(ConsoleIO io, BoardRendererInterface renderer) {
    this(promptForEngine(io), io, renderer);  // delegate to full-injection constructor
}

    private static GameEngine promptForEngine(ConsoleIO io) {
        io.writeLine("Welcome to Brique! please enter board size:");
        String input = io.readLine();
        int size = 11;
        if (input != null && !input.isEmpty()) {
            try {
                size = Integer.parseInt(input.trim());
                if (size <= 0) size = 11;
            } catch (NumberFormatException e) {
                // keep default
            }
        }
        return GameEngineFactory.create(GameMode.LOCAL_1V1, size);
    }

    public void start() {
        running = true;
        String input;

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

            // End-of-stream (e.g. piped input exhausted) → treat as quit
            if (input == null) {
                engine.getState().abort();
                io.writeLine("Input closed. Game aborted.");
                break;
            }

            ActionCommand cmd = ActionCommand.parse(input);

            if (cmd == null) {
                io.writeLine("Invalid input. Please enter row and column separated by space.");
            } else if (cmd instanceof ActionCommand.Quit) {
                engine.getState().abort();
                io.writeLine("Game is over: quit the game");
                break;
            } else if (cmd instanceof ActionCommand.Swap) {
                try {
                    engine.getState().applyPieRule();
                    io.writeLine("Pie rule applied.");
                } catch (IllegalStateException e) {
                    io.writeLine("Cannot apply pie rule: " + e.getMessage());
                }
            } else if (cmd instanceof ActionCommand.PlaceStone place) {
                boolean success = engine.playMove(place.getPosition());
                if (!success) {
                    io.writeLine("Invalid move. Try again.");
                }
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
}
