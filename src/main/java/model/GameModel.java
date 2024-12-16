package model;

import enums.Direction;
import enums.MyCircle;
import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Class responsible for making the base model of the game.
 */
public class GameModel {

    /**
     * Variable tht represents the size of the board.
     */
    public static int BOARD_SIZE = 5;

    /**
     * Represents the board in a 2D array.
     */
    private final MyCircle[][] board;
    
    public GameModel() {
        board = new MyCircle[BOARD_SIZE][BOARD_SIZE];
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if ((i == 0 && j == 4) || (i == 2 && j == 2) || (i == 4 && j == 0)) {
                    board[i][j] = MyCircle.BLUE;
                } else {
                    board[i][j] = MyCircle.RED;
                }
            }
        }
    }

    public MyCircle[][] getBoard() {
        return board;
    }

    public MyCircle getPieceAt(int row, int col) {
        if (isWithinBounds(row, col)) {
            return board[row][col];
        }
        return MyCircle.NONE;
    }

    public boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }

    /**
     * Moves an element of the {@link #board} array to a certain direction.
     * @param i - the row from which the element will be moved from
     * @param j- the column from which the element will be moved from
     * @param d - the direction of the movement
     */
    public void move(int i, int j, Direction d) {
        try {
            int x = i + d.getRowChange();
            int y = j + d.getColChange();

            if(board[i][j] == MyCircle.BLUE){
                if (Objects.equals(board[x][y], MyCircle.RED)) {
                    board[i][j] = MyCircle.NONE;
                    board[x][y] = MyCircle.BLUE;
                    Logger.info("Moving blue circle at ({}, {}) {}",i, j, d);
                }
            }else if (board[i][j] == MyCircle.RED) {
                if (Objects.equals(board[x][y], MyCircle.NONE)){
                    board[i][j] = MyCircle.NONE;
                    board[x][y] = MyCircle.RED;
                    Logger.info("Moving red circle at ({}, {}) {}",i, j, d);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            Logger.error("Stay on board!");
        }
    }

    /**
     * toString method for printing out boardmodel to the console.
     * @return stringBuilder
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (var i = 0; i < BOARD_SIZE; i++) {
            for (var j = 0; j < BOARD_SIZE; j++) {
                sb.append(board[i][j].name()).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

}
