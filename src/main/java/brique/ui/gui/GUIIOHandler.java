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
            return inputQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    @Override
    public void writeLine(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            // Auto-scroll to bottom
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public void submitInput(String input) {
        inputQueue.offer(input);
    }
}
