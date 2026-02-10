package brique.ui.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import brique.ui.IOHandlerInterface;

public class ConsoleIO implements IOHandlerInterface {

    // Reads text input from the console
    private final BufferedReader reader;

    // Writes text output to the console
    private final PrintWriter writer;

    public ConsoleIO() {

        // Wrap System.in to read full lines of input
        this.reader = new BufferedReader(new InputStreamReader(System.in));

        // Auto-flushing writer for immediate console output
        this.writer = new PrintWriter(System.out, true);
    }

    @Override
    public String readLine() {

        try {
            // Attempt to read a line from standard input
            return reader.readLine();
        } catch (IOException e) {
            // On unexpected I/O failure, signal termination to the caller
            return null;
        }
    }

    @Override
    public void writeLine(String message) {

        // Print the message followed by a newline
        writer.println(message);
    }
}
