package brique.ui.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class UIComponentFactory {

    private final BoardTheme theme; // Reference to the theme for consistent styling

    public UIComponentFactory(BoardTheme theme) {
        this.theme = theme; // Store the theme reference for use in creating components with consistent styling
    }

    // --- Buttons -------------------------------------------

    public JButton createStyledButton(String text, Color baseColor) {

        JButton btn = new JButton(text); // Create a new JButton with the specified text
        btn.setFont(new Font("SansSerif", Font.BOLD, 12)); // Set a bold sans-serif font for better readability and a modern look
        btn.setForeground(Color.WHITE); // Set the text color to white for good contrast against the base color
        btn.setBackground(baseColor); // Set the background color to the provided base color, which should be a theme color for consistency
        btn.setFocusPainted(false); // Disable the default focus painting to maintain a cleaner look when the button is focused
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(baseColor.darker(), 1), // Add a darker border around the button for better definition and to make it stand out against the background
            new EmptyBorder(6, 16, 6, 16) // Add padding inside the button to increase the clickable area and improve aesthetics
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Change the cursor to a hand when hovering over the button to indicate it's clickable

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(baseColor.brighter()); // Lighten the button color when the mouse enters to provide visual feedback and enhance interactivity
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(baseColor); // Reset the button color when the mouse exits to return to the default state
            }
        });
        return btn;
    }

    // --- Labels -------------------------------------------

    public JLabel createStatusLabel(String text) { // Create a JLabel for displaying status messages, with styling that matches the overall theme and is suitable for the status bar
        
        JLabel label = new JLabel(text); // Create a new JLabel with the specified text to display status messages to the user
        
        label.setFont(new Font("SansSerif", Font.PLAIN, 13)); // Set a plain sans-serif font for clear and unobtrusive status messages
        
        label.setForeground(new Color(200, 195, 180)); // Set the text color to a light, desaturated color that contrasts well with the status background and fits the overall theme
        
        return label; // Return the styled JLabel for use in the status bar or other parts of the UI where status messages are displayed
    }

    public JLabel createTurnIndicator() { 
        // Create a JLabel for indicating the current player's turn, 
        // with styling that makes it stand out in the status bar and matches the theme

        JLabel label = new JLabel();
        
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        label.setForeground(theme.getStatusForeground());
        
        return label; // Return the styled JLabel for use in the status bar to indicate whose turn it is
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

        // Set the text area to be non-editable since it's only for displaying log messages, not for user input
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setForeground(new Color(60, 55, 48));
        area.setBackground(new Color(255, 252, 245));
        area.setMargin(new Insets(4, 8, 4, 8));

        // Enable line wrapping to ensure that long log messages are wrapped within the visible area of the text area, improving readability
        return area;
    }

    public JScrollPane createLogScrollPane(JTextArea logArea) {
        
        // Create a JScrollPane that wraps the provided JTextArea (logArea) 
        // to allow scrolling when the log messages exceed the visible area, 
        // ensuring that all messages can be accessed by the user
        JScrollPane scroll = new JScrollPane(logArea); 

        // Set a preferred height for the log area while allowing it to expand horizontally as needed, 
        // providing enough space for multiple log messages without taking up too much screen space        
        scroll.setPreferredSize(new Dimension(0, 100)); 

        // Set a compound border that includes an empty border for padding and a titled border with a line border and title text,
        // to visually separate the log area from other UI components and provide a clear label for the log section, while using theme colors for consistency
        scroll.setBorder(BorderFactory.createCompoundBorder(
        
            new EmptyBorder(4, 8, 8, 8),
        
            // Create a titled border with a line border and title text to visually separate 
            // the log area and provide a clear label
            BorderFactory.createTitledBorder( 
                BorderFactory.createLineBorder(new Color(180, 170, 155)),
                " Game Log ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.PLAIN, 11),
                theme.getAccentColor()
            )
        ));
        
        // Set the background color of the viewport to match the log area's background for a cohesive look
        scroll.getViewport().setBackground(new Color(255, 252, 245)); 
        
        // Return the configured JScrollPane that contains the log area, 
        // ready to be added to the UI where the game log should be displayed
        return scroll; 
    }

    // --- Legend / rules panel -------------------------------------------

    public JPanel createLegendPanel() {

        JPanel legend = new JPanel(); // Create a new JPanel to serve as the legend and rules panel, which will provide instructions and information about the game to the user

        legend.setLayout(new BoxLayout(legend, BoxLayout.Y_AXIS)); // Set the layout to BoxLayout with a vertical axis to stack components vertically in the panel

        legend.setBackground(theme.getPanelBackground()); // Set the background color of the legend panel to match the theme's panel background for visual consistency

        legend.setBorder(BorderFactory.createCompoundBorder( // Set a compound border that includes a line border and an empty border for padding, to visually separate the legend panel from other UI components and provide spacing around its content
            BorderFactory.createLineBorder(new Color(180, 170, 155), 1),
            new EmptyBorder(12, 12, 12, 12)
        ));

        legend.setPreferredSize(new Dimension(170, 0)); // Set a preferred width for the legend panel while allowing it to expand vertically as needed, providing enough space for the instructions without taking up too much horizontal space

        addSectionTitle(legend, "How to Play", 14); // Add a section title to the legend panel with the text "How to Play" and a larger font size to clearly indicate the purpose of the section
        
        legend.add(Box.createVerticalStrut(12)); // Add vertical spacing after the section title to separate it from the content below for better readability
        
        // Add a legend item for the black player, using a filled circle symbol, a description of their objective, and the theme's black stone color for visual consistency
        addLegendItem(legend, "\u25CF BLACK", "Connects TOP \u2194 BOTTOM",
                      theme.getBlackStone()); 
        legend.add(Box.createVerticalStrut(8)); // Add vertical spacing between the legend items to improve readability and prevent them from appearing too crowded

        // Add a legend item for the white player, using an open circle symbol, a description of their objective, and a light grey color to represent the white stone while ensuring it is visible against the background
        addLegendItem(legend, "\u25CB WHITE", "Connects LEFT \u2194 RIGHT",
                      new Color(160, 160, 160)); 

        // Add vertical spacing before the rules section to visually separate it from the legend items and indicate a new section of information
        legend.add(Box.createVerticalStrut(16));

        // Add a section title for the rules with a slightly smaller font size than the main title to indicate a new subsection of information
        addSectionTitle(legend, "Rules", 13); 

        // Add vertical spacing after the rules section title to separate it from the list of rules for better readability
        legend.add(Box.createVerticalStrut(6)); 

        // Add a rule text item with a bullet point to explain the basic action of placing a stone on the board, providing clear instructions to the user
        addRuleText(legend, "\u2022 Click a cell to place a stone"); 

        // Add a rule text item to explain the escort rule, which is a key mechanic of the game, using a bullet point for clarity and formatting the text to fit within the panel
        addRuleText(legend, "\u2022 Escort rule: a cell with both escorts "
                          + "friendly is auto-filled"); 

        // Add a rule text item to explain the pie rule, which is an important strategic option for the second player, using a bullet point for consistency with the other rules
        addRuleText(legend, "\u2022 Pie rule: White can swap on first turn"); 
        // Add a rule text item to explain the orientation of escorts on light squares, which is essential for understanding how the escort rule works, using a bullet point for clarity
        addRuleText(legend, "\u2022 Light squares: escorts are UP and LEFT"); 
        // Add a rule text item to explain the orientation of escorts on dark squares, which is essential for understanding how the escort rule works, using a bullet point for clarity
        addRuleText(legend, "\u2022 Dark squares: escorts are DOWN and RIGHT"); 

        // Add vertical glue at the end to push the content to the top of the panel and allow for flexible spacing if the panel is resized,
        // ensuring that the instructions remain grouped together at the top for better readability
        legend.add(Box.createVerticalGlue());

        // Return the fully constructed legend panel, which contains instructions and rules for the game, ready to be added to the UI
        return legend; 
    }

    // --- Private helpers -------------------------------------------

    // Helper method to add a section title to a panel with specified text and font size, 
    // using the theme's accent color for visual consistency and alignment to the left for better readability

    private void addSectionTitle(JPanel panel, String text, int fontSize) {
        JLabel title = new JLabel(text);
        title.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        title.setForeground(theme.getAccentColor());
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
    }

    // Helper method to add a legend item to a panel, consisting of a name with a specific color and a description,
    // with styling that differentiates the name from the description and uses the provided color for the name 
    // to visually link it to the corresponding player or element in the game

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

    // Helper method to add a rule text item to a panel, with a bullet point and styling that makes it easy to read,
    // using a smaller font size and a desaturated color to differentiate it from section titles while maintaining readability, 
    // and formatting the text to fit within the panel for better presentation of the rules
    
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
