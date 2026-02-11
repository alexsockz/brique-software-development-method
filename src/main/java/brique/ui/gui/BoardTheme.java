package brique.ui.gui;

import java.awt.Color;

public final class BoardTheme {

    // genereal constant
    private final String titleSubtitleFont;
    // Board squares
    private final Color lightSquare;
    private final Color darkSquare;
    private final Color lightSquareHover;
    private final Color darkSquareHover;

    // Stones
    private final Color blackStone;
    private final Color blackStoneHighlight;
    private final Color whiteStone;
    private final Color whiteStoneHighlight;
    private final Color blackStoneBorder;
    private final Color whiteStoneBorder;

    // Grid
    private final Color gridLine;
    private final Color labelColor;

    // Move highlights
    private final Color filledHighlight;
    private final Color capturedHighlight;
    private final Color lastMoveMarker;

    // Edge indicators
    private final Color edgeBlack;
    private final Color edgeWhite;

    // Backgrounds
    private final Color background;
    private final Color panelBackground;
    private final Color statusBackground;
    private final Color statusForeground;
    private final Color accentColor;

    // Private constructor used by the Builder
    private BoardTheme(Builder b) {
        this.titleSubtitleFont  = b.titleSubtitleFont;
        this.lightSquare        = b.lightSquare;
        this.darkSquare         = b.darkSquare;
        this.lightSquareHover   = b.lightSquareHover;
        this.darkSquareHover    = b.darkSquareHover;
        this.blackStone         = b.blackStone;
        this.blackStoneHighlight = b.blackStoneHighlight;
        this.whiteStone         = b.whiteStone;
        this.whiteStoneHighlight = b.whiteStoneHighlight;
        this.blackStoneBorder   = b.blackStoneBorder;
        this.whiteStoneBorder   = b.whiteStoneBorder;
        this.gridLine           = b.gridLine;
        this.labelColor         = b.labelColor;
        this.filledHighlight    = b.filledHighlight;
        this.capturedHighlight  = b.capturedHighlight;
        this.lastMoveMarker     = b.lastMoveMarker;
        this.edgeBlack          = b.edgeBlack;
        this.edgeWhite          = b.edgeWhite;
        this.background         = b.background;
        this.panelBackground    = b.panelBackground;
        this.statusBackground   = b.statusBackground;
        this.statusForeground   = b.statusForeground;
        this.accentColor        = b.accentColor;
    }

    public static BoardTheme defaultTheme() {
        return new Builder().build();
    }

    // --- Getters ----------------------------------------------

    public String getTitleSubtitleFont() { return titleSubtitleFont; }
    public Color getLightSquare()        { return lightSquare; }
    public Color getDarkSquare()         { return darkSquare; }
    public Color getLightSquareHover()    { return lightSquareHover; }
    public Color getDarkSquareHover()    { return darkSquareHover; }
    public Color getBlackStone()         { return blackStone; }
    public Color getBlackStoneHighlight(){ return blackStoneHighlight; }
    public Color getWhiteStone()         { return whiteStone; }
    public Color getWhiteStoneHighlight(){ return whiteStoneHighlight; }
    public Color getBlackStoneBorder()   { return blackStoneBorder; }
    public Color getWhiteStoneBorder()   { return whiteStoneBorder; }
    public Color getGridLine()           { return gridLine; }
    public Color getLabelColor()         { return labelColor; }
    public Color getFilledHighlight()    { return filledHighlight; }
    public Color getCapturedHighlight()  { return capturedHighlight; }
    public Color getLastMoveMarker()     { return lastMoveMarker; }
    public Color getEdgeBlack()          { return edgeBlack; }
    public Color getEdgeWhite()          { return edgeWhite; }
    public Color getBackground()         { return background; }
    public Color getPanelBackground()    { return panelBackground; }
    public Color getStatusBackground()   { return statusBackground; }
    public Color getStatusForeground()   { return statusForeground; }
    public Color getAccentColor()        { return accentColor; }

    // --- Builder ----------------------------------------------

    public static class Builder {

        private String titleSubtitleFont = "SansSerif";
        private Color lightSquare        = new Color(235, 220, 190);
        private Color darkSquare         = new Color(200, 180, 150);
        private Color lightSquareHover   = new Color(210, 235, 200);
        private Color darkSquareHover    = new Color(180, 210, 165);
        private Color blackStone         = new Color(30, 30, 30);
        private Color blackStoneHighlight = new Color(60, 60, 60);
        private Color whiteStone         = new Color(245, 245, 245);
        private Color whiteStoneHighlight = new Color(220, 220, 220);
        private Color blackStoneBorder   = new Color(40, 40, 40);
        private Color whiteStoneBorder   = new Color(220, 220, 220);
        private Color gridLine           = new Color(140, 120, 100);
        private Color labelColor         = new Color(80, 70, 60);
        private Color filledHighlight    = new Color(100, 200, 100, 80);
        private Color capturedHighlight  = new Color(200, 80, 80, 80);
        private Color lastMoveMarker     = new Color(255, 215, 0, 180);
        private Color edgeBlack          = new Color(50, 50, 50, 120);
        private Color edgeWhite          = new Color(200, 200, 200, 160);
        private Color background         = new Color(250, 245, 235);
        private Color panelBackground    = new Color(245, 240, 230);
        private Color statusBackground   = new Color(60, 55, 48);
        private Color statusForeground   = new Color(240, 235, 220);
        private Color accentColor        = new Color(120, 90, 60);

        public Builder titleSubtitleFont(String f)  { this.titleSubtitleFont = f; return this; }
        public Builder lightSquare(Color c)        { this.lightSquare = c; return this; }
        public Builder darkSquare(Color c)         { this.darkSquare = c; return this; }
        public Builder lightSquareHover(Color c)   { this.lightSquareHover = c; return this; }
        public Builder darkSquareHover(Color c)    { this.darkSquareHover = c; return this; }
        public Builder blackStone(Color c)         { this.blackStone = c; return this; }
        public Builder blackStoneHighlight(Color c){ this.blackStoneHighlight = c; return this; }
        public Builder whiteStone(Color c)         { this.whiteStone = c; return this; }
        public Builder whiteStoneHighlight(Color c){ this.whiteStoneHighlight = c; return this; }
        public Builder blackStoneBorder(Color c)   { this.blackStoneBorder = c; return this; }
        public Builder whiteStoneBorder(Color c)   { this.whiteStoneBorder = c; return this; }
        public Builder gridLine(Color c)           { this.gridLine = c; return this; }
        public Builder labelColor(Color c)         { this.labelColor = c; return this; }
        public Builder filledHighlight(Color c)    { this.filledHighlight = c; return this; }
        public Builder capturedHighlight(Color c)  { this.capturedHighlight = c; return this; }
        public Builder lastMoveMarker(Color c)     { this.lastMoveMarker = c; return this; }
        public Builder edgeBlack(Color c)          { this.edgeBlack = c; return this; }
        public Builder edgeWhite(Color c)          { this.edgeWhite = c; return this; }
        public Builder background(Color c)         { this.background = c; return this; }
        public Builder panelBackground(Color c)    { this.panelBackground = c; return this; }
        public Builder statusBackground(Color c)   { this.statusBackground = c; return this; }
        public Builder statusForeground(Color c)   { this.statusForeground = c; return this; }
        public Builder accentColor(Color c)        { this.accentColor = c; return this; }

        public BoardTheme build() {
            return new BoardTheme(this);
        }
    }
}
