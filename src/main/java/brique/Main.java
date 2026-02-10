package brique;

import brique.ui.cli.BriqueCLI;
import brique.ui.gui.BoardTheme;
import brique.ui.gui.MainMenuScreen;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class Main {
    private Main() { }

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

                BoardTheme theme = BoardTheme.defaultTheme();
                MainMenuScreen menu = new MainMenuScreen(theme);
                menu.setVisible(true);
            });
        }
    }
}
