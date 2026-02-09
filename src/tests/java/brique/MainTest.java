package brique;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class MainTest {

	@Test
	void shouldRunWithDefaultBoardSizeWhenNoArgs() {
		ByteArrayInputStream in = new ByteArrayInputStream(new byte[0]);
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		PrintStream originalOut = System.out;
		java.io.InputStream originalIn = System.in;
		try (PrintStream ps = new PrintStream(out, true, StandardCharsets.UTF_8)) {
			System.setIn(in);
			System.setOut(ps);

			Main.main(new String[]{"cli"});

			String output = out.toString(StandardCharsets.UTF_8);
			assertThat(output).contains("Welcome to Brique!");
		} finally {
			System.setIn(originalIn);
			System.setOut(originalOut);
		}
	}
}
