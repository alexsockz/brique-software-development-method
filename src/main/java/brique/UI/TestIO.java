package brique.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Test implementation of {@link IOHandler} that accepts a predefined
 * sequence of inputs and captures all outputs for later inspection.  It
 * provides deterministic input to the {@link BriqueCLI} during unit
 * testing and allows assertions to be made on the text output.  This
 * class is intentionally simple and not thread‑safe; it is intended
 * solely for use in tests.
 */
public class TestIO implements IOHandler {
    private final Queue<String> inputs = new LinkedList<>();
    private final List<String> outputs = new ArrayList<>();

    /**
     * Adds a line of input to the queue.  The first added line will be
     * returned on the first call to {@link #readLine()}, the second on the
     * second call, and so forth.
     *
     * @param line the input line to supply
     */
    public void addInput(String line) {
        inputs.add(line);
    }

    @Override
    public String readLine() {
        return inputs.poll();
    }

    @Override
    public void writeLine(String message) {
        outputs.add(message);
    }

    /**
     * Returns all messages that have been written via {@link #writeLine(String)}.
     *
     * @return a list of output messages in the order they were written
     */
    public List<String> getOutputs() {
        return outputs;
    }
}