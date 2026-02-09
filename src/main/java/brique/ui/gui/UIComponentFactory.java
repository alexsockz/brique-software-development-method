package brique.ui.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
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
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
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

    public JLabel createStatusLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(new Color(200, 195, 180));
        return label;
    }

    public JLabel createTurnIndicator() {
        JLabel label = new JLabel();
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(theme.getStatusForeground());
        return label;
    }

    // --- Stone preview -------------------------------------------

    public StonePreviewPanel createStonePreview() {
        return new StonePreviewPanel(theme);
    }

    // --- Log area -------------------------------------------

    public JTextArea createLogArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setForeground(new Color(60, 55, 48));
        area.setBackground(new Color(255, 252, 245));
        area.setMargin(new Insets(4, 8, 4, 8));
        return area;
    }

    public JScrollPane createLogScrollPane(JTextArea logArea) {
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setPreferredSize(new Dimension(0, 100));
        scroll.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(4, 8, 8, 8),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 170, 155)),
                " Game Log ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.PLAIN, 11),
                theme.getAccentColor()
            )
        ));
        scroll.getViewport().setBackground(new Color(255, 252, 245));
        return scroll;
    }

    // --- Legend / rules panel -------------------------------------------

    public JPanel createLegendPanel() {
        JPanel legend = new JPanel();
        legend.setLayout(new BoxLayout(legend, BoxLayout.Y_AXIS));
        legend.setBackground(theme.getPanelBackground());
        legend.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 170, 155), 1),
            new EmptyBorder(12, 12, 12, 12)
        ));
        legend.setPreferredSize(new Dimension(170, 0));

        addSectionTitle(legend, "How to Play", 14);
        legend.add(Box.createVerticalStrut(12));
        addLegendItem(legend, "\u25CF BLACK", "Connects TOP \u2194 BOTTOM",
                      theme.getBlackStone());
        legend.add(Box.createVerticalStrut(8));
        addLegendItem(legend, "\u25CB WHITE", "Connects LEFT \u2194 RIGHT",
                      new Color(160, 160, 160));
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

    private void addSectionTitle(JPanel panel, String text, int fontSize) {
        JLabel title = new JLabel(text);
        title.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        title.setForeground(theme.getAccentColor());
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
    }

    private void addLegendItem(JPanel panel, String name, String desc,
                               Color color) {
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        nameLabel.setForeground(color);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(nameLabel);

        JLabel descLabel = new JLabel(desc);
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        descLabel.setForeground(new Color(100, 90, 80));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(descLabel);
    }

    private void addRuleText(JPanel panel, String text) {
        JLabel label = new JLabel(
            "<html><body style='width:120px'>" + text + "</body></html>");
        label.setFont(new Font("SansSerif", Font.PLAIN, 11));
        label.setForeground(new Color(100, 90, 80));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(4));
    }
}
