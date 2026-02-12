package brique.ui.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import brique.ui.gui.board.BoardTheme;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class UIComponentFactory {

    private final BoardTheme theme;

    public UIComponentFactory(BoardTheme theme) {
        this.theme = theme;
    }

    // --- Buttons -------------------------------------------

    public JButton createStyledButton(String text, Color baseColor) {

        JButton btn = new JButton(text); // Create a new JButton with the specified text
        btn.setFont(new Font(theme.titleFont(), Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(baseColor);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(baseColor.darker(), 1),
            new EmptyBorder(6, 16, 6, 16)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(baseColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(baseColor);
            }
        });
        return btn;
    }

    // --- Labels -------------------------------------------

    public JLabel createStatusLabel(String text) { // Create a JLabel for displaying status messages, with styling that matches the overall theme and is suitable for the status bar
        JLabel label = new JLabel(text);
        label.setFont(new Font(theme.titleFont(), Font.PLAIN, 13));
        label.setForeground(theme.ui().statusLabel());
        return label;
    }

    public JLabel createTurnIndicator() {
        // Create a JLabel for indicating the current player's turn, 
        // with styling that makes it stand out in the status bar and matches the theme

        JLabel label = new JLabel();
        label.setFont(new Font(theme.titleFont(), Font.BOLD, 14));
        label.setForeground(theme.backgrounds().statusFg());
        return label;
    }

    // --- Stone preview -------------------------------------------

    public StonePreviewPanel createStonePreview() {
        return new StonePreviewPanel(theme);
        // Create and return a StonePreviewPanel, 
        // which is a custom component that displays a preview of the current player's stone colour, 
        // using the theme for consistent colouring
    }

    // --- Log area -------------------------------------------

    public JTextArea createLogArea() {

        // Create a JTextArea for displaying the game log, 
        // with styling that makes it easy to read and fits the overall theme of the application
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setForeground(theme.ui().logText());
        area.setBackground(theme.ui().logBg());
        area.setMargin(new Insets(4, 8, 4, 8));
        return area;
    }

    public JScrollPane createLogScrollPane(JTextArea logArea) {
        
        // Create a JScrollPane that wraps the provided JTextArea (logArea) 
        // to allow scrolling when the log messages exceed the visible area, 
        // ensuring that all messages can be accessed by the user
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setPreferredSize(new Dimension(0, 100));
        scroll.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(4, 8, 8, 8),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(theme.ui().boardBorder()),
                " Game Log ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font(theme.titleFont(), Font.PLAIN, 11),
                theme.backgrounds().accent()
            )
        ));
        scroll.getViewport().setBackground(theme.ui().logBg());
        return scroll;
    }

    // --- Legend / rules panel -------------------------------------------

    public JPanel createLegendPanel() {
        JPanel legend = new JPanel(); // Create a new JPanel to serve as the legend and rules panel, which will provide instructions and information about the game to the user
        legend.setLayout(new BoxLayout(legend, BoxLayout.Y_AXIS));
        legend.setBackground(theme.backgrounds().panel());
        legend.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(theme.ui().boardBorder(), 1),
            new EmptyBorder(12, 12, 12, 12)
        ));
        legend.setPreferredSize(new Dimension(170, 0));

        addSectionTitle(legend, "How to Play", 14);
        legend.add(Box.createVerticalStrut(12));

        addLegendItem(legend, "\u25CF BLACK", "Connects TOP \u2194 BOTTOM",
                      theme.stones().black());
        legend.add(Box.createVerticalStrut(8));

        addLegendItem(legend, "\u25CB WHITE", "Connects LEFT \u2194 RIGHT",
                  theme.ui().whiteStoneLegend());

        legend.add(Box.createVerticalStrut(16));
        addSectionTitle(legend, "Rules", 13);
        legend.add(Box.createVerticalStrut(6));

        addRuleText(legend, "\u2022 Click a cell to place a stone");
        addRuleText(legend, "\u2022 Escort rule: a cell with both escorts "
                          + "friendly is auto-filled");
        addRuleText(legend, "\u2022 Pie rule: White can swap on first turn");
        addRuleText(legend, "\u2022 Light squares: escorts are UP and LEFT");
        addRuleText(legend, "\u2022 Dark squares: escorts are DOWN and RIGHT");

        legend.add(Box.createVerticalGlue());
        return legend;
    }

    // --- Private helpers -------------------------------------------

    // Helper method to add a section title to a panel with specified text and font size, 
    // using the theme's accent color for visual consistency and alignment to the left for better readability

    private void addSectionTitle(JPanel panel, String text, int fontSize) {
        JLabel title = new JLabel(text);
        title.setFont(new Font(theme.titleFont(), Font.BOLD, fontSize));
        title.setForeground(theme.backgrounds().accent());
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
    }

    // Helper method to add a legend item to a panel, consisting of a name with a specific color and a description,
    // with styling that differentiates the name from the description and uses the provided color for the name 
    // to visually link it to the corresponding player or element in the game

    private void addLegendItem(JPanel panel, String name, String desc,
                               Color color) {
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font(theme.titleFont(), Font.BOLD, 12));
        nameLabel.setForeground(color);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(nameLabel);

        JLabel descLabel = new JLabel(desc);
        descLabel.setFont(new Font(theme.titleFont(), Font.PLAIN, 11));
        descLabel.setForeground(theme.ui().legendDesc());
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(descLabel);
    }

    // Helper method to add a rule text item to a panel, with a bullet point and styling that makes it easy to read,
    // using a smaller font size and a desaturated color to differentiate it from section titles while maintaining readability, 
    // and formatting the text to fit within the panel for better presentation of the rules

    private void addRuleText(JPanel panel, String text) {
        JLabel label = new JLabel(
            "<html><body style='width:120px'>" + text + "</body></html>");
        label.setFont(new Font(theme.titleFont(), Font.PLAIN, 11));
        label.setForeground(theme.ui().legendDesc());
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(4));
    }
}
