package brique.ui;

/**
 * Abstraction for input and output operations used by user interfaces.  The
 * {@link BriqueCLI} delegates all reading and writing to an {@code IOHandler}
 * to decouple itself from concrete input sources (e.g. console, network)
 * and output destinations (e.g. standard out, log).  This allows unit
 * tests to supply stub implementations and capture output without
 * manipulating global state.  It exemplifies the Dependency Inversion
 * principle.
 */
public interface IOHandlerInterface {
    /**
     * Reads a single line of input from the underlying source.  If no more
     * input is available this may return {@code null}.
     *
     * @return the next line of input, or {@code null} if there is none
     */
    String readLine();

    /**
     * Writes a message followed by a newline to the underlying destination.
     *
     * @param message the message to write
     */
    void writeLine(String message);
}