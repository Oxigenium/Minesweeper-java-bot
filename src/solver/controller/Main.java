package solver.controller;

import solver.model.Board;
import solver.model.Window;
import solver.view.Overlay;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws InterruptedException {



        int countMines = getUserInput("Enter the amount of mines");

        Window minesweeper = new Window();



        Board board = new Board(minesweeper, countMines);

        System.out.println(board);




    }

    private static int getUserInput(String msg) {
            boolean ok = false;
            int input = -1;

            System.out.println(msg);

            while (!ok) {
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                    input = Integer.parseInt(br.readLine());
                    ok = true;
                } catch (IOException | NumberFormatException e) {
                    System.err.println("Not a good number, try again!");
                }
            }

            return input;
        }
}