package brique.ui.gui.controller;

import brique.core.Position;

public abstract class ActionCommand {

    private ActionCommand() { } // this is so only these classes are part of this

    //place stone to specific position
    public static final class PlaceStone extends ActionCommand {
        private final Position position;

        public PlaceStone(int row, int col) {
            this.position = Position.of(row, col);
        }

        public Position getPosition() { return position; }
        public int getRow() { return position.row(); }
        public int getCol() { return position.col(); }
    }

    
    public static final class Swap extends ActionCommand {
        public static final Swap INSTANCE = new Swap();
    }

    
    public static final class Quit extends ActionCommand {
        public static final Quit INSTANCE = new Quit();
    }

    public static ActionCommand parse(String input) {
        if (input == null) return null;
        String trimmed = input.trim();

        if (trimmed.equalsIgnoreCase("quit")) {
            return Quit.INSTANCE;
        }
        if (trimmed.equalsIgnoreCase("swap")) {
            return Swap.INSTANCE;
        }

        String[] parts = trimmed.split("\\s+");
        if (parts.length == 2) {
            try {
                int row = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]);
                return new PlaceStone(row, col);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
