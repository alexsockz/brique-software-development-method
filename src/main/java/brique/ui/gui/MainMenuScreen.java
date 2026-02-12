package brique.ui.gui;

import brique.core.GameMode;
import brique.ui.gui.board.BoardTheme;
import brique.ui.gui.controller.GameController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class MainMenuScreen extends JFrame {


    private final transient BoardTheme theme;

    public MainMenuScreen() {
        this(BoardTheme.defaultTheme());
    }

    public MainMenuScreen(BoardTheme theme) {
        super("Brique \u2014 Select Game Mode");
        this.theme = theme;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        add(createTitlePanel(), BorderLayout.NORTH);
        add(createButtonPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        setMinimumSize(new Dimension(520, 440));
        setPreferredSize(new Dimension(600, 500));
        pack();
        setLocationRelativeTo(null);
    }

    // --- Layout ------------------------------------------------

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(theme.backgrounds().statusBg());
        panel.setBorder(new EmptyBorder(30, 20, 20, 20));

        JLabel title = new JLabel("Brique");
        title.setFont(new Font(theme.titleFont(), Font.BOLD, theme.titleFontSize()));
        title.setForeground(theme.backgrounds().statusFg());
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("A strategy board game");

        subtitle.setFont(new Font(theme.titleFont(), Font.PLAIN, theme.subtitleFontSize()));
        subtitle.setForeground(theme.menu().subtitle());
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subtitle);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(theme.backgrounds().main());
        panel.setBorder(new EmptyBorder(30, 80, 10, 80));


        panel.add(createModeButton(
            "\u265F  1 vs 1 (Local)",
            "Play against a friend on this computer",
            theme.menu().local(),
            () -> launchGame(GameMode.LOCAL_1V1)));

        panel.add(Box.createVerticalStrut(16));


        panel.add(createModeButton(
            "\uD83C\uDF10  Online",
            "Play against an opponent over the network",
            theme.menu().online(),
            () -> showComingSoon("Online")));

        panel.add(Box.createVerticalStrut(16));


        panel.add(createModeButton(
            "\uD83E\uDD16  vs Bot",
            "Challenge the computer",
            theme.menu().bot(),
            () -> showComingSoon("vs Bot")));

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(theme.backgrounds().panel());
        panel.setBorder(new EmptyBorder(8, 0, 8, 0));

        JLabel label = new JLabel("Select a game mode to begin");
        label.setFont(new Font(theme.titleFont(), Font.ITALIC, theme.labelFontSize()));
        label.setForeground(theme.menu().footer());
        panel.add(label);
        return panel;
    }

    // --- Helpers -----------------------------------------------

    private JPanel createModeButton(String title, String description,
                                     Color baseColor, Runnable action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        card.setBackground(baseColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(baseColor.darker(), 1),
            new EmptyBorder(12, 20, 12, 20)));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(theme.titleFont(), Font.BOLD, theme.subtitleFontSize()));
        titleLabel.setForeground(Color.WHITE);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font(theme.titleFont(), Font.PLAIN, theme.descFontSize()));
        descLabel.setForeground(theme.menu().descLabel());

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descLabel, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { action.run(); }
            @Override public void mouseEntered(MouseEvent e) {
                card.setBackground(baseColor.brighter());
            }
            @Override public void mouseExited(MouseEvent e) {
                card.setBackground(baseColor);
            }
        });

        return card;
    }

    private void launchGame(GameMode mode) {
        dispose();
        SwingUtilities.invokeLater(() -> {
            GameController controller = new GameController();
            controller.setGameMode(mode);
            BriqueGUI gui = new BriqueGUI(controller, theme);
            gui.setVisible(true);
            gui.promptAndStartGame();
        });
    }

    private void showComingSoon(String modeName) {
        JOptionPane.showMessageDialog(this,
            modeName + " mode is coming soon!\nStay tuned for updates.",
            "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
    }
}
