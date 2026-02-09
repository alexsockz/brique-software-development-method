package brique.ui;

import brique.ui.cli.BriqueCLI;

public interface IOHandlerInterface {
    String readLine();

    void writeLine(String message);
}