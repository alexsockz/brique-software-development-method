package brique.ui.cli;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class ConsoleIOTest {
    @Test
    void shouldReadAndWriteThroughConsole() {
        String input = "hello" + System.lineSeparator();
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Preserve originals
        PrintStream originalOut = System.out;
        java.io.InputStream originalIn = System.in;

        try {
            System.setIn(in);
            System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));

            ConsoleIO console = new ConsoleIO();

            assertThat(console.readLine()).isEqualTo("hello");
            console.writeLine("world");

            String written = out.toString(StandardCharsets.UTF_8);
            assertThat(written).isEqualTo("world" + System.lineSeparator());
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }
}
