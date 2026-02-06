package brique.ui;

import brique.core.Board;

/**
 * Renders a {@link Board} into a human‑readable string.  Different
 * renderers may implement different visual styles; this interface allows
 * the {@code BriqueCLI} to remain agnostic of the specific output
 * representation.
 */
public interface BoardRenderer {
    /**
     * Convert the given board into a multi‑line string suitable for
     * display to a human player.
     *
     * @param board the board to render
     * @return a textual representation of the board
     */
    String render(Board board);
}