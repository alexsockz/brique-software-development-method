package brique.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TestIO implements IOHandler {
    private final Queue<String> inputs = new LinkedList<>();
    private final List<String> outputs = new ArrayList<>();

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

    public List<String> getOutputs() {
        return outputs;
    }
}