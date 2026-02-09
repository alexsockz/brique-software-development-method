package brique.ui.gui;

import java.awt.Color;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class BoardThemeTest {

    @Nested
    @DisplayName("Default Theme Tests")
    class DefaultThemeTests {
        @Test
        @DisplayName("Should provide a non-null colour for every property")
        void shouldProvideNonNullColours() {
            BoardTheme theme = BoardTheme.defaultTheme();
            assertThat(theme).isNotNull();
            assertThat(theme.getLightSquare()).isNotNull();
            assertThat(theme.getDarkSquare()).isNotNull();
            assertThat(theme.getLightSquareHover()).isNotNull();
            assertThat(theme.getDarkSquareHover()).isNotNull();
            assertThat(theme.getBlackStone()).isNotNull();
            assertThat(theme.getBlackStoneHighlight()).isNotNull();
            assertThat(theme.getWhiteStone()).isNotNull();
            assertThat(theme.getWhiteStoneHighlight()).isNotNull();
            assertThat(theme.getBlackStoneBorder()).isNotNull();
            assertThat(theme.getWhiteStoneBorder()).isNotNull();
            assertThat(theme.getGridLine()).isNotNull();
            assertThat(theme.getLabelColor()).isNotNull();
            assertThat(theme.getFilledHighlight()).isNotNull();
            assertThat(theme.getCapturedHighlight()).isNotNull();
            assertThat(theme.getLastMoveMarker()).isNotNull();
            assertThat(theme.getEdgeBlack()).isNotNull();
            assertThat(theme.getEdgeWhite()).isNotNull();
            assertThat(theme.getBackground()).isNotNull();
            assertThat(theme.getPanelBackground()).isNotNull();
            assertThat(theme.getStatusBackground()).isNotNull();
            assertThat(theme.getStatusForeground()).isNotNull();
            assertThat(theme.getAccentColor()).isNotNull();
        }

        @Test
        @DisplayName("Should return new instance on each call")
        void shouldReturnNewInstanceEachTime() {
            BoardTheme t1 = BoardTheme.defaultTheme();
            BoardTheme t2 = BoardTheme.defaultTheme();
            assertThat(t1).isNotSameAs(t2);
        }
    }

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {
        @Test
        @DisplayName("Should override specific properties while keeping defaults for others")
        void shouldOverrideProperties() {
            Color light = Color.RED;
            Color dark  = Color.BLUE;
            BoardTheme custom = new BoardTheme.Builder()
                .lightSquare(light)
                .darkSquare(dark)
                .build();

            assertThat(custom.getLightSquare()).isEqualTo(light);
            assertThat(custom.getDarkSquare()).isEqualTo(dark);

            // Other values should match defaults
            BoardTheme defaults = BoardTheme.defaultTheme();
            assertThat(custom.getGridLine()).isEqualTo(defaults.getGridLine());
            assertThat(custom.getLabelColor()).isEqualTo(defaults.getLabelColor());
        }

        @Test
        @DisplayName("Should build distinct themes without sharing state")
        void shouldBuildDistinctThemes() {
            BoardTheme.Builder builder = new BoardTheme.Builder().lightSquare(Color.GREEN);
            BoardTheme first  = builder.build();
            // Modify the builder after the first build
            builder.darkSquare(Color.MAGENTA);
            BoardTheme second = builder.build();

            // Ensure the two themes are different objects
            assertThat(first).isNotSameAs(second);
            // The first theme should not be affected by later builder modifications
            assertThat(first.getLightSquare()).isEqualTo(Color.GREEN);
            assertThat(first.getDarkSquare()).isNotEqualTo(Color.MAGENTA);
            // The second theme should incorporate the updated property
            assertThat(second.getDarkSquare()).isEqualTo(Color.MAGENTA);
        }

        @Test
        @DisplayName("Should allow chaining multiple overrides")
        void shouldAllowChainingMultipleOverrides() {
            Color whiteStone = new Color(100, 100, 100);
            Color accent     = new Color(50, 50, 50);
            BoardTheme theme = new BoardTheme.Builder()
                .whiteStone(whiteStone)
                .accentColor(accent)
                .filledHighlight(new Color(10, 20, 30, 40))
                .build();

            assertThat(theme.getWhiteStone()).isEqualTo(whiteStone);
            assertThat(theme.getAccentColor()).isEqualTo(accent);
            assertThat(theme.getFilledHighlight()).isEqualTo(new Color(10, 20, 30, 40));

            // Unspecified properties should remain defaults
            BoardTheme defaults = BoardTheme.defaultTheme();
            assertThat(theme.getLightSquare()).isEqualTo(defaults.getLightSquare());
        }
    }
}