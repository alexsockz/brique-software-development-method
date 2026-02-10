package brique.ui.gui;

import javax.swing.*;

import brique.ui.IOHandlerInterface;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GUIIOHandler implements IOHandlerInterface {

    private final JTextArea logArea;

    private final BlockingQueue<String> inputQueue;

    public GUIIOHandler(JTextArea logArea) {
        this.logArea = logArea;
        this.inputQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public String readLine() {
        try {
            // Blocks until input is submitted from the GUI
            return inputQueue.take();
        } catch (InterruptedException e) {
            // Restore interrupt flag and return null on interruption
            Thread.currentThread().interrupt();
            return null;
        }
    }

    @Override
    public void writeLine(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            // Automatically scroll to the bottom to show latest message
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public void submitInput(String input) {
        // Non-blocking and thread-safe
        inputQueue.offer(input);
    }
}
