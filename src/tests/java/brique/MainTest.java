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
		try {
			System.setIn(in);
			System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));

			Main.main(new String[]{});

			String output = out.toString(StandardCharsets.UTF_8);
			assertThat(output).contains("Welcome to Brique!");
		} finally {
			System.setIn(originalIn);
			System.setOut(originalOut);
		}
	}

	@Test
	void shouldReportInvalidBoardSizeAndFallbackToDefault() {
		ByteArrayInputStream in = new ByteArrayInputStream(new byte[0]);
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		PrintStream originalOut = System.out;
		java.io.InputStream originalIn = System.in;
		try {
			System.setIn(in);
			System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));

			Main.main(new String[]{"not-a-number"});

			String output = out.toString(StandardCharsets.UTF_8);
			assertThat(output)
				.contains("Invalid board size provided; using default size of 11.")
				.contains("Welcome to Brique!");
		} finally {
			System.setIn(originalIn);
			System.setOut(originalOut);
		}
	}
}
