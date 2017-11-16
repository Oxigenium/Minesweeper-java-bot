package solver.controller;

import solver.model.Board;
import solver.model.Status;
import solver.model.Window;

public class Main {
    public static void main(String[] args) {

        Window minesweeper = new Window();
        Board board = new Board(minesweeper, 9, 9, 10);
        System.out.println(board);

    }
}