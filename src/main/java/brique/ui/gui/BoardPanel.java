package brique.ui.gui;

import brique.core.Board;
import brique.core.GameState;
import brique.core.Position;
import brique.core.Stone;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

// View component responsible for rendering the game board and handling user interactions with it.
public class BoardPanel extends JPanel {

    @FunctionalInterface
    public interface CellClickListener {
        void onCellClicked(int row, int col);
    }

    private transient GameState gameState; // The current game state, which contains the board and other relevant information
    private int boardSize; // The size of the board (number of rows/columns), derived from the game state for layout calculations
    private transient Position hoveredCell; // The cell currently hovered by the mouse, used for hover effects in the UI
    private Stone currentPlayer = Stone.BLACK; // The current player, used for hover preview and other UI elements that depend on whose turn it is
    private transient Position lastMovePosition; // The position of the last move played, used to highlight the most recent move on the board
    private final Set<Position> lastFilledPositions  = new HashSet<>(); // The set of positions that were filled as a result of the last move, used to highlight these positions on the board
    private final Set<Position> lastCapturedPositions = new HashSet<>(); // The set of positions that were captured as a result of the last move, used to highlight these positions on the board
    private final List<CellClickListener> listeners = new ArrayList<>(); // List of listeners that will be notified when a cell is clicked, allowing the controller to react to user input

    private transient final BoardTheme theme; // The theme used for rendering the board, which provides colors and styles for various elements of the board and stones

    private static final int MARGIN       = 40; // Margin around the grid for labels and edge indicators
    private static final int LABEL_MARGIN = 25; // Additional margin for labels to prevent overlap with the grid

    public BoardPanel(BoardTheme theme) {
        this.theme = Objects.requireNonNull(theme); // Store the theme reference for use in painting and layout
        setBackground(theme.getBackground()); // Set the background color of the panel to match the theme's background for visual consistency
        setPreferredSize(new Dimension(600, 600)); // Set a preferred size for the panel, which can be adjusted based on the expected board size and layout
        installMouseHandlers(); // Install mouse listeners to handle user interactions such as clicks and hover effects on the board cells
    }

    // --- Public API (controller pushes state here) ------------

    public void addCellClickListener(CellClickListener listener) {
        listeners.add(Objects.requireNonNull(listener)); // Add a listener to be notified when a cell is clicked, allowing the controller to react to user input by processing the move corresponding to the clicked cell
    }

    public void setGameState(GameState state) {
        this.gameState = state; // Store the reference to the game state, which will be used in the paintComponent method to read the current board configuration and render it accordingly
        this.boardSize = state.getBoard().getSize(); // Update the board size based on the new game state, which is necessary for layout calculations when rendering the grid and stones
        repaint(); // Request a repaint to reflect the new game state on the board, ensuring that the UI is updated to show the current board configuration and any changes that have occurred
    }

    public void refreshBoard() {
        repaint(); // Request a repaint to update the board display, typically called after the game state has been modified (e.g., after processing a move) to ensure that the UI reflects the latest changes to the board
    }

    public void setCurrentPlayer(Stone player) {
        this.currentPlayer = player; // Update the current player, which may affect the hover preview and other UI elements that indicate whose turn it is
    }

    public void setLastMovePosition(Position pos) {
        this.lastMovePosition = pos; // Update the position of the last move, which is used to highlight the most recent move on the board for better visual feedback to the players
    }

    public void setHighlightedPositions(Set<Position> filled,
                                        Set<Position> captured) {
        lastFilledPositions.clear(); // Clear the previous sets of highlighted positions before adding the new ones, ensuring that only the positions relevant to the most recent move are highlighted on the board
        lastCapturedPositions.clear(); // Clear the previous sets of highlighted positions before adding the new ones, ensuring that only the positions relevant to the most recent move are highlighted on the board
        if (filled != null)   lastFilledPositions.addAll(filled); // Add the new set of filled positions to be highlighted, which will be used in the paintComponent method to render these positions with a specific highlight color
        if (captured != null) lastCapturedPositions.addAll(captured); // Add the new set of captured positions to be highlighted, which will be used in the paintComponent method to render these positions with a specific highlight color
        repaint(); // Request a repaint to update the board display with the new highlighted positions, ensuring that players can visually identify which positions were affected by the last move (e.g., which stones were captured or filled)
    }

    public void clearHighlights() {
        lastFilledPositions.clear(); // Clear the sets of highlighted positions, typically called when starting a new turn or after the highlights are no longer relevant, to ensure that the board is displayed without any outdated highlights
        lastCapturedPositions.clear(); // Clear the sets of highlighted positions, typically called when starting a new turn or after the highlights are no longer relevant, to ensure that the board is displayed without any outdated highlights
        lastMovePosition = null; // Clear the last move position to remove the highlight from the most recent move, typically called when starting a new turn or after the highlights are no longer relevant, to ensure that the board is displayed without any outdated highlights
        repaint(); // Request a repaint to update the board display without any highlights, ensuring that the UI reflects the current state of the board without any visual indicators that are no longer relevant
    }

    // --- Mouse handling (coordinate translation only) ---------

    // Install mouse listeners to handle user interactions with the board, 
    // such as clicks for placing stones and hover effects for previewing moves, 
    // allowing the controller to react to user input by processing the move corresponding 
    // to the clicked cell and providing visual feedback when hovering over potential move locations
    private void installMouseHandlers() { 

        addMouseListener(new MouseAdapter() {
        
            @Override
            public void mouseClicked(MouseEvent e) {

                // Handle the mouse click event by translating the pixel coordinates to a board cell and notifying the registered listeners, 
                // allowing the controller to process the move corresponding to the clicked cell
                handleClick(e.getX(), e.getY()); 
            }

            @Override
            public void mouseExited(MouseEvent e) {
                
                // Clear the hovered cell when the mouse exits the panel, 
                // which will remove any hover effects from the board cells and ensure that the UI 
                // does not show a hover preview when the mouse is outside the board area
                hoveredCell = null; 
                
                // Request a repaint to update the board display without any hover effects, 
                // ensuring that the UI reflects the current state of the board without showing a hover 
                // preview when the mouse is outside the board area
                repaint(); 
            }
        });

        // Add a mouse motion listener to handle mouse movement events for hover effects on the board cells, 
        // allowing the UI to provide visual feedback when the user hovers over potential move locations by highlighting the corresponding cell on the board
        addMouseMotionListener(new MouseMotionAdapter() { 

            @Override
            public void mouseMoved(MouseEvent e) {
                
                // Update the hovered cell based on the current mouse position, 
                // which will trigger a repaint to show the hover effect on the corresponding cell if it is a valid move location 
                // (i.e., an empty cell on the board)
                updateHover(e.getX(), e.getY()); 
            }
        });
    }

    // Translate pixel coordinates to board cell and notify listeners of the click event, 
    // allowing the controller to process the move corresponding to the clicked cell
    private void handleClick(int x, int y) { 
        // Convert the pixel coordinates of the mouse click to a board cell position,
        // and if the click is within the bounds of the board, notify all registered listeners 
        // with the row and column of the clicked cell, allowing the controller to react to the user input by processing
        Position cell = pixelToCell(x, y);

        // If the click is within the bounds of the board (i.e., a valid cell was clicked), 
        // notify all registered listeners with the row and column of the clicked cell, 
        // allowing the controller to react to the user input by processing the move corresponding to the clicked cell
        if (cell != null) { 
            
            for (CellClickListener l : listeners) {
                // Notify the listener of the clicked cell's row and column, 
                // allowing the controller to process the move corresponding to the clicked cell
                l.onCellClicked(cell.getRow(), cell.getCol()); 
            }
        }
    }

    private void updateHover(int x, int y) {
        
        // Convenience accessor to get the current board from the game state for validating hover positions
        Board board = getBoard(); 
        
        // Convert the pixel coordinates of the mouse position to a board cell position, 
        // and if the cell is valid (i.e., within the bounds of the board and empty), 
        // update the hoveredCell variable to trigger a repaint with the hover effect on that cell
        Position cell = pixelToCell(x, y);

        // Initialize newHover to null, which will be used to determine if the hovered cell should be updated 
        // based on the current mouse position and the state of the board
        Position newHover = null; 

        if (cell != null && board != null && board.getStone(cell) == Stone.EMPTY) {
            // Update newHover to the current cell if it is a valid hover target 
            // (i.e., an empty cell on the board), which will be used to show the hover effect on that cell
            newHover = cell; 
        }
        if (!Objects.equals(hoveredCell, newHover)) {
            // Update the hovered cell to the new hover position, 
            // which will trigger a repaint to show the hover effect on the corresponding cell if it is a valid move location
            hoveredCell = newHover; 

            // Request a repaint to update the board display with the new hover effect, 
            // ensuring that the UI provides visual feedback when the user hovers 
            // over potential move locations on the board
            repaint(); 
        }
    }

    private Position pixelToCell(int x, int y) {
        
        // If the game state is not set, 
        // we cannot determine the board configuration, 
        // so return null to indicate that no valid cell can be identified from the pixel coordinates
        if (gameState == null) return null; 
        
        int cellSize = getCellSize();      // Calculate the size of each cell based on the current board size and panel dimensions, which is necessary for translating pixel coordinates to board cell positions accurately
        int ox = getGridOriginX();         // Calculate the x-coordinate of the origin of the grid (top-left corner), which is necessary for translating pixel coordinates to board cell positions accurately by determining the offset from the panel's edge to the start of the grid
        int oy = getGridOriginY();         // Calculate the y-coordinate of the origin of the grid (top-left corner), which is necessary for translating pixel coordinates to board cell positions accurately by determining the offset from the panel's edge to the start of the grid
        if (x < ox || y < oy) return null; // If the pixel coordinates are above or to the left of the grid origin, return null to indicate that the click is outside the grid and does not correspond to a valid cell
        int col = (x - ox) / cellSize;     // Calculate the column index by determining how many cell widths fit between the grid origin and the x-coordinate of the click, which translates the horizontal pixel coordinate to a column index on the board
        int row = (y - oy) / cellSize;     // Calculate the row index by determining how many cell heights fit between the grid origin and the y-coordinate of the click, which translates the vertical pixel coordinate to a row index on the board
        
        if (row >= 0 && row < boardSize && col >= 0 && col < boardSize) {
            return new Position(row, col); // If the calculated row and column indices are within the bounds of the board, return a new Position object representing the corresponding cell on the board, allowing the controller to process the move corresponding to this cell when it is clicked
        }

        return null;
    }

    // --- Layout calculations ----------------------------------

    private int getCellSize() {
        // Calculate the available width for the grid by subtracting the margins and label space from the total panel width, 
        // which is necessary for determining how much horizontal space can be allocated to each cell on the board
        int w = getWidth()  - 2 * MARGIN - LABEL_MARGIN; 
        
        // Calculate the available height for the grid by subtracting the margins and label space from the total panel height, 
        // which is necessary for determining how much vertical space can be allocated to each cell on the board
        int h = getHeight() - 2 * MARGIN - LABEL_MARGIN; 
        
        // If the board size is not set or invalid, return a default cell size to ensure that the UI can still render something reasonable,
        // even if the game state has not been properly initialized
        if (boardSize <= 0) return 40;

        // Calculate the cell size by taking the minimum of the available width and height divided by the board size,
        // which ensures that the cells are square and fit within the available space on the panel, 
        // while also enforcing a minimum cell size for usability
        return Math.max(20, Math.min(w / boardSize, h / boardSize));
    }

    private int getGridOriginX() { // Calculate the x-coordinate of the origin of the grid (top-left corner) 
        return (getWidth() - getCellSize() * boardSize) / 2 + LABEL_MARGIN / 2;
    }

    private int getGridOriginY() { // Calculate the y-coordinate of the origin of the grid (top-left corner)
        return (getHeight() - getCellSize() * boardSize) / 2 + LABEL_MARGIN / 2;
    }

    // --- Painting ---------------------------------------------

    private Board getBoard() {
        return gameState != null ? gameState.getBoard() : null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Ensure the panel is properly rendered before we add our custom drawing on top of it
        Board board = getBoard(); // Get the current board from the game state to use for rendering the grid and stones; if the board is null, we cannot render anything, so we return early to avoid errors
        if (board == null) return; // If the board is not available, we cannot render anything, so we return early to avoid errors and ensure that the UI does not attempt to render an invalid state

        // Create a copy of the Graphics context to avoid side effects on the original, 
        // allowing us to modify rendering settings without affecting other components that may use the same Graphics object
        Graphics2D g2 = (Graphics2D) g.create(); 

        // Enable anti-aliasing for smoother edges on the grid lines and stones, 
        // improving the visual quality of the board rendering
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON); 

        // Enable anti-aliasing for text to improve the readability of labels and any text rendered on the board, 
        // ensuring that the UI is visually appealing and easy to read
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON); 

        int cell = getCellSize();    // Calculate the size of each cell based on the current board size and panel dimensions, 
        int ox   = getGridOriginX(); // Calculate the x-coordinate 
        int oy   = getGridOriginY(); // Calculate the y-coordinate 

        // Draw the edge indicators (goals) around the grid, which visually indicate the areas 
        // where players can score points by filling their opponent's goal area,
        // and use the theme's colors for consistency with the overall design of the board

        drawEdgeIndicators(g2, cell, ox, oy);
        drawGrid(g2, cell, ox, oy);
        drawLabels(g2, cell, ox, oy);
        drawStones(g2, cell, ox, oy);
        drawHoverPreview(g2, cell, ox, oy);

        g2.dispose(); // Dispose of the Graphics context
    }

    private void drawEdgeIndicators(Graphics2D g2, int cs, int ox, int oy) {

        // Calculate the total width and height of the grid based on the cell size and board size
        int gridW = cs * boardSize;
        int gridH = cs * boardSize;
        int t = 5;

        // Top/bottom → BLACK goal
        g2.setColor(theme.getEdgeBlack());
        g2.fillRect(ox, oy - t, gridW, t);
        g2.fillRect(ox, oy + gridH, gridW, t);

        // Left/right → WHITE goal
        g2.setColor(theme.getEdgeWhite());
        g2.fillRect(ox - t, oy, t, gridH);
        g2.fillRect(ox + gridW, oy, t, gridH);
    }

    private void drawGrid(Graphics2D g2, int cs, int ox, int oy) { 

    // Draw the grid of the board, including the background colors for the squares, 
    // highlights for hovered cells and last move, and the grid lines, 
    // which visually structure the board and provide feedback to the players 
    // about the state of the game and their interactions
    
        for (int r = 0; r < boardSize; r++) { // Iterate over each row of the board to draw the grid and its elements
        
            for (int c = 0; c < boardSize; c++) { // Iterate over each column of the board to draw the grid and its elements for the current row
        
                int x = ox + c * cs;
                int y = oy + r * cs;
                Position pos = new Position(r, c);

                // Determine if the current cell should be a light or dark square based on its position,
                // which creates a checkerboard pattern on the board for better visual distinction between cells
                boolean isLight = (r + c) % 2 == 0; 

                Color bg; // Determine the background color for the current cell based on whether it is hovered or not,
                          // and whether it is a light or dark square, using the theme's colors for consistency

                // If the current cell is the one being hovered by the mouse, use the hover color from the theme;
                // otherwise, use the regular square color from the theme based on whether it is a light or dark square, 
                // providing visual feedback to the user about which cell 
                // they are hovering over and maintaining the overall aesthetic of the board

                if (hoveredCell != null && hoveredCell.equals(pos)) {
                    bg = isLight ? theme.getLightSquareHover()
                                : theme.getDarkSquareHover();
                } else {
                    bg = isLight ? theme.getLightSquare()
                                : theme.getDarkSquare();
                }

                // Set the color for the current cell's background based on the determined color, 
                // which will be used to fill the cell and provide visual feedback to the players about the state of the board
                g2.setColor(bg); 
                // Fill the current cell with the determined background color, which visually represents the state of the cell 
                // (e.g., whether it is hovered or not) and contributes to the overall appearance of the board
                g2.fillRect(x, y, cs, cs);

                // If the current cell is in the set of positions that were filled as a result of the last move,
                // use the filled highlight color from the theme to fill the cell, 
                // providing visual feedback to the players about which positions were affected by the last move 
                // (e.g., which stones were placed)

                if (lastFilledPositions.contains(pos)) {
                    g2.setColor(theme.getFilledHighlight());
                    g2.fillRect(x, y, cs, cs);
                }
                if (lastCapturedPositions.contains(pos)) {
                    g2.setColor(theme.getCapturedHighlight());
                    g2.fillRect(x, y, cs, cs);
                }

                // Set the color for the grid lines based on the theme, which will be used to draw the lines that separate the cells on the board for better visual structure
                g2.setColor(theme.getGridLine()); 
                // Draw the rectangle for the current cell, 
                // which creates the grid lines that visually separate the cells on the board 
                // and contribute to the overall structure and appearance of the board
                g2.drawRect(x, y, cs, cs); 
            }
        }
    }

    // Draw the labels for the rows and columns around the grid, which provide reference points for players 
    // to identify specific cells on the board,
    // and use the theme's label color for consistency with the overall design of the board
    private void drawLabels(Graphics2D g2, int cs, int ox, int oy) {

        // Set the color for the labels based on the theme, 
        // which will be used to draw the row and column labels around the grid for better visual consistency with the overall design of the board
        g2.setColor(theme.getLabelColor()); 

        // Calculate the font size for the labels based on the cell size, 
        // ensuring that the labels are large enough to be readable but also fit well within the layout of the board, 
        // and use a bold sans-serif font for better visibility and aesthetics
        Font font = new Font(theme.getTitleSubtitleFont(), Font.BOLD, Math.max(10, cs / 3));

        // Set the font for the labels to the calculated font, 
        // which will be used to draw the row and column labels around the grid 
        // for better visual consistency with the overall design of the board
        g2.setFont(font); 

        // Get the font metrics for the current font, 
        // which will be used to calculate the positioning of the labels based 
        // on their size for better visual alignment around the grid
        FontMetrics fm = g2.getFontMetrics(); 

        // Draw column labels (0 to boardSize-1) at the top of the grid
        for (int c = 0; c < boardSize; c++) {
            String s = String.valueOf(c);
            g2.drawString(s,
                ox + c * cs + (cs - fm.stringWidth(s)) / 2,
                oy - 10);
        }
        
        // Draw row labels (0 to boardSize-1) on the left side of the grid
        for (int r = 0; r < boardSize; r++) {
            String s = String.valueOf(r);
            g2.drawString(s,
                ox - fm.stringWidth(s) - 8,
                oy + r * cs + (cs + fm.getAscent()) / 2 - 2);
        }
    }

    private void drawStones(Graphics2D g2, int cs, int ox, int oy) {

        // Get the board from the game state; return early if it's not available
        Board board = getBoard();
        if (board == null) return;
        
        // Calculate the margin around stones and the actual stone size to fit within a cell
        int margin = Math.max(2, cs / 8);
        int size   = cs - 2 * margin;

        // Iterate over all cells in the board to render stones at non-empty positions
        for (int r = 0; r < boardSize; r++) {
            for (int c = 0; c < boardSize; c++) {
                // Get the stone at the current position; skip rendering if the cell is empty
                Stone stone = board.getStone(new Position(r, c));
                if (stone == Stone.EMPTY) continue;

                // Calculate the pixel coordinates for the stone's top-left corner
                int x = ox + c * cs + margin;
                int y = oy + r * cs + margin;
                // Create an ellipse shape for the stone with the calculated size
                Ellipse2D.Double shape = new Ellipse2D.Double(x, y, size, size);

                // Render black stones with a gradient from highlight to dark color
                if (stone == Stone.BLACK) {
                    g2.setPaint(new GradientPaint(
                        x, y, theme.getBlackStoneHighlight(),
                        ((float)x) + size, ((float)y) + size, theme.getBlackStone()));
                    g2.fill(shape);
                    // Set the border color for the black stone
                    g2.setColor(theme.getBlackStoneBorder());
                } else {
                    // Render white stones with a gradient from light to highlight color
                    g2.setPaint(new GradientPaint(
                        x, y, theme.getWhiteStone(),
                        ((float)x) + size, ((float)y) + size, theme.getWhiteStoneHighlight()));
                    g2.fill(shape);
                    // Set the border color and style for the white stone
                    g2.setColor(theme.getWhiteStoneBorder());
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.draw(shape);
                    // Set a generic gray color for additional styling
                    g2.setColor(new Color(150, 150, 150));
                }
                
                // Draw the border around the stone with a 1.5-pixel stroke width
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(shape);

                // If this stone is the last move played, render a gold marker dot on top
                if (lastMovePosition != null
                    && lastMovePosition.getRow() == r
                    && lastMovePosition.getCol() == c) {
                    int dot = Math.max(4, size / 5);
                    g2.setColor(theme.getLastMoveMarker());
                    // Center the dot on the stone
                    g2.fillOval(x + (size - dot) / 2,
                                y + (size - dot) / 2, dot, dot);
                }
            }
        }
    }

    private void drawHoverPreview(Graphics2D g2, int cs, int ox, int oy) {
        // Get the board from the game state
        Board board = getBoard();
        
        // Return early if there is no hovered cell, no board, or if the hovered cell is not empty
        // (only show preview for valid, empty positions)
        if (hoveredCell == null || board == null
            || board.getStone(hoveredCell) != Stone.EMPTY) {
            return;
        }

        // Calculate the margin around the stone preview and the actual stone size to fit within the cell
        int margin = Math.max(2, cs / 8);
        int size   = cs - 2 * margin;
        
        // Calculate the pixel coordinates for the preview stone's top-left corner
        int x = ox + hoveredCell.getCol() * cs + margin;
        int y = oy + hoveredCell.getRow() * cs + margin;
        
        // Create an ellipse shape for the preview stone with the calculated size
        Ellipse2D.Double shape = new Ellipse2D.Double(x, y, size, size);

        // Set the fill color based on the current player:
        // dark semi-transparent for black player, light semi-transparent for white player
        g2.setColor(currentPlayer == Stone.BLACK
            ? new Color(30, 30, 30, 80)
            : new Color(240, 240, 240, 120));
        
        // Fill the shape with the player's color to show a preview of where the stone will be placed
        g2.fill(shape);
        
        // Set the border color and stroke width for the preview outline
        g2.setColor(new Color(150, 150, 150, 100));
        g2.setStroke(new BasicStroke(1.0f));
        
        // Draw the border around the preview stone
        g2.draw(shape);
    }
}
