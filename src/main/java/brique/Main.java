package brique;

import brique.ui.cli.BriqueCLI;
import brique.ui.gui.BoardTheme;
import brique.ui.gui.MainMenuScreen;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

// Main entry point of the application. Determines whether to launch the CLI or GUI based on command-line arguments.
public final class Main {
    private Main() { }

    public static void main(String[] args) {
        
        // If the program is started with command-line arguments
        // and the first argument is "cli", run in command-line mode
        if (args != null && args.length > 0 && args[0].equalsIgnoreCase("cli")) {

            // Create and start the Command Line Interface
            BriqueCLI cli = new BriqueCLI();
            cli.start();
        } else {

            // Otherwise, start the Graphical User Interface (Swing)
            // Ensure all UI code runs on the Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {

                // Set the GUI look and feel to match the operating system
                try {
                    UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) { 
                    // If setting the look and feel fails, continue with default
                }

                BoardTheme theme = BoardTheme.defaultTheme();        // Create the default board theme
                MainMenuScreen menu = new MainMenuScreen(theme);     //shows the main menu screen to select game mode and therefore the gameEngine
                menu.setVisible(true);                            // Show the GUI window
            });
        }
    }
}
