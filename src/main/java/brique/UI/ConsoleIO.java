package brique.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Implementation of {@link IOHandler} that reads from standard input and
 * writes to standard output.  This class isolates the console from the
 * rest of the application, making it trivial to supply alternate IO
 * strategies for testing or alternate frontends.  It follows the
 * Dependency Inversion principle.
 */
public class ConsoleIO implements IOHandler {
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