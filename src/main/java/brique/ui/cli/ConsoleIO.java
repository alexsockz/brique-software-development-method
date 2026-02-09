package brique.ui.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import brique.ui.IOHandlerInterface;

public class ConsoleIO implements IOHandlerInterface {
    private final BufferedReader reader;
    private final PrintWriter writer;

    public ConsoleIO() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.writer = new PrintWriter(System.out, true);
    }

    @Override
    public String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            // Unexpected IO error; return null to signal termination
            return null;
        }
    }

    @Override
    public void writeLine(String message) {
        writer.println(message);
    }
}