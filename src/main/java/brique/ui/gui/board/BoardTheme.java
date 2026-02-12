package brique.ui.gui.board;

import java.awt.Color;

public final class BoardTheme {

    // Inner value-object records — group related colours to eliminate data clumps

    public record SquareColors(Color light, Color dark,
                               Color lightHover, Color darkHover) {}

    public record StoneColors(Color black, Color blackHighlight,
                              Color white, Color whiteHighlight,
                              Color blackBorder, Color whiteBorder,
                              Color shadow,
                              Color blackPreview, Color whitePreview,
                              Color previewBorder) {}

    public record GridColors(Color line, Color label) {}

    public record HighlightColors(Color filled, Color captured, Color lastMove) {}

    public record EdgeColors(Color black, Color white) {}

    public record BackgroundColors(Color main, Color panel,
                                   Color statusBg, Color statusFg,
                                   Color accent) {}

    public record UIColors(Color quitButton, Color boardBorder,
                           Color statusLabel,
                           Color logText, Color logBg,
                           Color whiteStoneLegend, Color legendDesc) {}

    public record MenuColors(Color local, Color online, Color bot,
                             Color subtitle, Color footer,
                             Color descLabel) {}

    // --- Grouped fields ---------------------------------------

    private final String titleFont;
    // Font sizes
    private final int titleFontSize;
    private final int subtitleFontSize;
    private final int labelFontSize;
    private final int descFontSize;
    private final int buttonFontSize;
    private final int nameLabelFontSize;
    private final int smallFontSize;
    private final SquareColors squares;
    private final StoneColors stones;
    private final GridColors grid;
    private final HighlightColors highlights;
    private final EdgeColors edges;
    private final BackgroundColors backgrounds;
    private final UIColors ui;
    private final MenuColors menu;

    // Private constructor — assembles groups from flat Builder fields
    private BoardTheme(Builder b) {
        this.titleFont   = b.titleSubtitleFont;
        this.titleFontSize = b.titleFontSize;
        this.subtitleFontSize = b.subtitleFontSize;
        this.labelFontSize = b.labelFontSize;
        this.descFontSize = b.descFontSize;
        this.buttonFontSize = b.buttonFontSize;
        this.nameLabelFontSize = b.nameLabelFontSize;
        this.smallFontSize = b.smallFontSize;
        this.squares     = new SquareColors(b.lightSquare, b.darkSquare,
                                            b.lightSquareHover, b.darkSquareHover);
        this.stones      = new StoneColors(b.blackStone, b.blackStoneHighlight,
                                           b.whiteStone, b.whiteStoneHighlight,
                                           b.blackStoneBorder, b.whiteStoneBorder,
                                           b.stoneShadowColor,
                                           b.blackStonePreviewColor,
                                           b.whiteStonePreviewColor,
                                           b.stonePreviewBorderColor);
        this.grid        = new GridColors(b.gridLine, b.labelColor);
        this.highlights  = new HighlightColors(b.filledHighlight, b.capturedHighlight,
                                               b.lastMoveMarker);
        this.edges       = new EdgeColors(b.edgeBlack, b.edgeWhite);
        this.backgrounds = new BackgroundColors(b.background, b.panelBackground,
                                                b.statusBackground, b.statusForeground,
                                                b.accentColor);
        this.ui          = new UIColors(b.quitButtonColor, b.boardBorderColor,
                                        b.statusLabelColor, b.logTextColor,
                                        b.logBackgroundColor, b.whiteStoneLegendColor,
                                        b.legendDescColor);
        this.menu        = new MenuColors(b.localModeColor, b.onlineModeColor,
                                          b.botModeColor, b.subtitleColor,
                                          b.footerColor, b.descLabelColor);
    }

    public static BoardTheme defaultTheme() {
        return new Builder().build();
    }

    // --- Grouped accessors ------------------------------------

    public String titleFont()              { return titleFont; }
    public int titleFontSize()             { return titleFontSize; }
    public int subtitleFontSize()          { return subtitleFontSize; }
    public int labelFontSize()             { return labelFontSize; }
    public int descFontSize()              { return descFontSize; }
    public int buttonFontSize()            { return buttonFontSize; }
    public int nameLabelFontSize()         { return nameLabelFontSize; }
    public int smallFontSize()             { return smallFontSize; }
    public SquareColors squares()          { return squares; }
    public StoneColors stones()            { return stones; }
    public GridColors grid()               { return grid; }
    public HighlightColors highlights()    { return highlights; }
    public EdgeColors edges()              { return edges; }
    public BackgroundColors backgrounds()  { return backgrounds; }
    public UIColors ui()                   { return ui; }
    public MenuColors menu()               { return menu; }

    // --- Builder (flat setters for ergonomic construction) -----

    public static class Builder {

        // General
        private String titleSubtitleFont        = "SansSerif";
        // Font sizes (defaults)
        private int titleFontSize = 42;
        private int subtitleFontSize = 16;
        private int labelFontSize = 12;
        private int descFontSize = 12;
        private int buttonFontSize = 12;
        private int nameLabelFontSize = 12;
        private int smallFontSize = 11;
    public Builder titleFontSize(int size) { this.titleFontSize = size; return this; }
    public Builder subtitleFontSize(int size) { this.subtitleFontSize = size; return this; }
    public Builder labelFontSize(int size) { this.labelFontSize = size; return this; }
    public Builder descFontSize(int size) { this.descFontSize = size; return this; }
    public Builder buttonFontSize(int size) { this.buttonFontSize = size; return this; }
    public Builder nameLabelFontSize(int size) { this.nameLabelFontSize = size; return this; }
    public Builder smallFontSize(int size) { this.smallFontSize = size; return this; }

        // Board squares
        private Color lightSquare               = new Color(235, 220, 190);
        private Color darkSquare                = new Color(200, 180, 150);
        private Color lightSquareHover          = new Color(210, 235, 200);
        private Color darkSquareHover           = new Color(180, 210, 165);

        // Stones
        private Color blackStone                = new Color(30, 30, 30);
        private Color blackStoneHighlight       = new Color(60, 60, 60);
        private Color whiteStone                = new Color(245, 245, 245);
        private Color whiteStoneHighlight       = new Color(220, 220, 220);
        private Color blackStoneBorder          = new Color(40, 40, 40);
        private Color whiteStoneBorder          = new Color(220, 220, 220);
        private Color stoneShadowColor          = new Color(150, 150, 150);
        private Color blackStonePreviewColor    = new Color(30, 30, 30, 80);
        private Color whiteStonePreviewColor    = new Color(240, 240, 240, 120);
        private Color stonePreviewBorderColor   = new Color(150, 150, 150, 100);

        // Grid
        private Color gridLine                  = new Color(140, 120, 100);
        private Color labelColor                = new Color(80, 70, 60);

        // Move highlights
        private Color filledHighlight           = new Color(100, 200, 100, 80);
        private Color capturedHighlight         = new Color(200, 80, 80, 80);
        private Color lastMoveMarker            = new Color(255, 215, 0, 180);

        // Edge indicators
        private Color edgeBlack                 = new Color(50, 50, 50, 120);
        private Color edgeWhite                 = new Color(200, 200, 200, 160);

        // Backgrounds
        private Color background                = new Color(250, 245, 235);
        private Color panelBackground           = new Color(245, 240, 230);
        private Color statusBackground          = new Color(60, 55, 48);
        private Color statusForeground          = new Color(240, 235, 220);
        private Color accentColor               = new Color(120, 90, 60);

        // UI chrome
        private Color quitButtonColor           = new Color(180, 70, 70);
        private Color boardBorderColor          = new Color(180, 170, 155);
        private Color statusLabelColor          = new Color(200, 195, 180);
        private Color logTextColor              = new Color(60, 55, 48);
        private Color logBackgroundColor        = new Color(255, 252, 245);
        private Color whiteStoneLegendColor     = new Color(160, 160, 160);
        private Color legendDescColor           = new Color(100, 90, 80);

        // MainMenuScreen
        private Color localModeColor            = new Color(80, 140, 80);
        private Color onlineModeColor           = new Color(70, 130, 180);
        private Color botModeColor              = new Color(160, 100, 60);
        private Color subtitleColor             = new Color(180, 175, 155);
        private Color footerColor               = new Color(140, 130, 120);
        private Color descLabelColor            = new Color(255, 255, 255, 200);

        // --- Builder setters ---

        public Builder titleSubtitleFont(String f)          { this.titleSubtitleFont = f; return this; }
        public Builder lightSquare(Color c)                 { this.lightSquare = c; return this; }
        public Builder darkSquare(Color c)                  { this.darkSquare = c; return this; }
        public Builder lightSquareHover(Color c)            { this.lightSquareHover = c; return this; }
        public Builder darkSquareHover(Color c)             { this.darkSquareHover = c; return this; }
        public Builder blackStone(Color c)                  { this.blackStone = c; return this; }
        public Builder blackStoneHighlight(Color c)         { this.blackStoneHighlight = c; return this; }
        public Builder whiteStone(Color c)                  { this.whiteStone = c; return this; }
        public Builder whiteStoneHighlight(Color c)         { this.whiteStoneHighlight = c; return this; }
        public Builder blackStoneBorder(Color c)            { this.blackStoneBorder = c; return this; }
        public Builder whiteStoneBorder(Color c)            { this.whiteStoneBorder = c; return this; }
        public Builder stoneShadowColor(Color c)            { this.stoneShadowColor = c; return this; }
        public Builder blackStonePreviewColor(Color c)      { this.blackStonePreviewColor = c; return this; }
        public Builder whiteStonePreviewColor(Color c)      { this.whiteStonePreviewColor = c; return this; }
        public Builder stonePreviewBorderColor(Color c)     { this.stonePreviewBorderColor = c; return this; }
        public Builder gridLine(Color c)                    { this.gridLine = c; return this; }
        public Builder labelColor(Color c)                  { this.labelColor = c; return this; }
        public Builder filledHighlight(Color c)             { this.filledHighlight = c; return this; }
        public Builder capturedHighlight(Color c)           { this.capturedHighlight = c; return this; }
        public Builder lastMoveMarker(Color c)              { this.lastMoveMarker = c; return this; }
        public Builder edgeBlack(Color c)                   { this.edgeBlack = c; return this; }
        public Builder edgeWhite(Color c)                   { this.edgeWhite = c; return this; }
        public Builder background(Color c)                  { this.background = c; return this; }
        public Builder panelBackground(Color c)             { this.panelBackground = c; return this; }
        public Builder statusBackground(Color c)            { this.statusBackground = c; return this; }
        public Builder statusForeground(Color c)            { this.statusForeground = c; return this; }
        public Builder accentColor(Color c)                 { this.accentColor = c; return this; }
        public Builder quitButtonColor(Color c)             { this.quitButtonColor = c; return this; }
        public Builder boardBorderColor(Color c)            { this.boardBorderColor = c; return this; }
        public Builder statusLabelColor(Color c)            { this.statusLabelColor = c; return this; }
        public Builder logTextColor(Color c)                { this.logTextColor = c; return this; }
        public Builder logBackgroundColor(Color c)          { this.logBackgroundColor = c; return this; }
        public Builder whiteStoneLegendColor(Color c)       { this.whiteStoneLegendColor = c; return this; }
        public Builder legendDescColor(Color c)             { this.legendDescColor = c; return this; }
        public Builder localModeColor(Color c)              { this.localModeColor = c; return this; }
        public Builder onlineModeColor(Color c)             { this.onlineModeColor = c; return this; }
        public Builder botModeColor(Color c)                { this.botModeColor = c; return this; }
        public Builder subtitleColor(Color c)               { this.subtitleColor = c; return this; }
        public Builder footerColor(Color c)                 { this.footerColor = c; return this; }
        public Builder descLabelColor(Color c)              { this.descLabelColor = c; return this; }

        public BoardTheme build() {
            return new BoardTheme(this);
        }
    }
}
