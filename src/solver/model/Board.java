package solver.model;

import java.awt.*;
import java.util.Arrays;

public class Board {

    private static final int MIN_ROWS = 9;
    private static final int MAX_ROWS = 24;
    private static final int MIN_COLUMNS = 9;
    private static final int MAX_COLUMNS = 30;
    private static final int MAX_MINES = 668;
    private static final int MIN_MINES = 10;

    private static final int DEFAULT_MAX_MINES = 10;
    private static final int DEFAULT_COLUMNS = 9;
    private static final int DEFAULT_ROWS = 9;


    class BoardException extends Exception {

        public BoardException(String message) {
            super(message);
        }

    }

    Window window;

    final int columns;
    final int rows;
    final int fields;
    final int maxMines;
    int closedFields;
    int openFields;


    Status[][] cells;


    public Board(Window window) {
        this.window = window;
        this.columns = DEFAULT_COLUMNS;
        this.rows = DEFAULT_ROWS;
        this.fields = columns * rows;
        this.maxMines = DEFAULT_MAX_MINES;
        this.closedFields = fields;
        this.openFields = 0;
        this.cells = new Status[columns][rows];

        for (Status[] row : cells) Arrays.fill(row, Status.BLOCK_CLOSED);

    }

    public Board(Window window,
                 int columns,
                 int rows,
                 int maxMines) {

        try {
            if (columns < 9 || rows < 9) throw new BoardException("There is no such a small board exist");
            if (columns > 30 || rows > 24) throw new BoardException("There is no such a small board exist");
            if (maxMines > 668 || maxMines > columns * rows) throw new BoardException("Too many mines for board");
            if (maxMines < 10) throw new BoardException("Too few mines for board");

        } catch (Board.BoardException e) {
            System.err.println(e.getMessage());
        }
        this.window = window;
        this.columns = columns;
        this.rows = rows;
        this.fields = columns * rows;
        this.maxMines = maxMines;
        this.closedFields = fields;
        this.openFields = 0;
        this.cells = new Status[columns][rows];

        for (Status[] row : cells) Arrays.fill(row, Status.BLOCK_CLOSED);
    }

    public Board(Window window,
                 int columns,
                 int rows,
                 int maxMines,
                 int closedFields,
                 Status[][] cells) {
        try {
            if (columns < MIN_COLUMNS || rows < MIN_ROWS)
                throw new BoardException("There is no such a small board possible");
            if (columns > MAX_COLUMNS || rows > MAX_ROWS)
                throw new BoardException("There is no such a small board possible");
            if (maxMines > MAX_MINES || maxMines > columns * rows) throw new BoardException("Too many mines for board");
            if (maxMines < MIN_MINES) throw new BoardException("Too few mines for board");
            if (closedFields > rows * columns)
                throw new BoardException("Closed fields amount is more than fields at all");
            if (closedFields <= 0) throw new BoardException("Nothing to play with, no one closed field");

        } catch (Board.BoardException e) {
            System.err.println(e.getMessage());
        }
        this.window = window;
        this.columns = columns;
        this.rows = rows;
        this.fields = columns * rows;
        this.maxMines = maxMines;
        this.closedFields = closedFields;
        this.openFields = fields - closedFields;
        this.cells = cells;
    }

    public Window getWindow() {
        return window;
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public int getFields() {
        return fields;
    }

    public int getMaxMines() {
        return maxMines;
    }

    public int getClosedFields() {
        return closedFields;
    }

    public int getOpenFields() {
        return openFields;
    }

    public Status[][] getCells() {
        return cells;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public boolean setClosedFields(int closedFields) {
        if (closedFields < fields) {
            this.closedFields = closedFields;
            this.openFields = fields - closedFields;
            return true;
        }
        return false;
    }

    public boolean setOpenFields(int openFields) {
        if (openFields < fields) {
            this.openFields = openFields;
            this.closedFields = fields - openFields;
            return true;
        }
        return false;
    }

    public boolean setCells(Status[][] cells) {
        for (Status[] cell : cells) {
            if (cell.length != rows) {
                return false;
            }
        }

        if (cells.length != columns) {
            return false;
        }

        this.cells = cells;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Board: " +
                "columns=" + columns +
                ", rows=" + rows +
                ", fields=" + fields +
                ", maxMines=" + maxMines +
                ", closedFields=" + closedFields +
                ", openFields=" + openFields +
                "\n\n");

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {

                result.append(cells[x][y]);

            }
            result.append('\n');
        }

        return result.toString();
    }
}
