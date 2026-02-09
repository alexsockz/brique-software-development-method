package brique;

import brique.ui.cli.BriqueCLI;
import brique.ui.gui.BoardTheme;
import brique.ui.gui.BriqueGUI;
import brique.ui.gui.GameController;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class Main {
    private Main() {
        // prevent instantiation
    }

    public static void main(String[] args) {
        if (args != null && args.length > 0 && args[0].equalsIgnoreCase("cli")) {
            BriqueCLI cli = new BriqueCLI();
            cli.start();
        } else {
            SwingUtilities.invokeLater(() -> {
                try {
                    UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) { }

                // Composition root: wire dependencies
                BoardTheme theme          = BoardTheme.defaultTheme();
                GameController controller = new GameController();
                BriqueGUI gui             = new BriqueGUI(controller, theme);

                gui.setVisible(true);
                gui.promptAndStartGame();
            });
        }
    }
}