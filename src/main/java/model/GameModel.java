package model;

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
    public MyCircle[][] board = new MyCircle[BOARD_SIZE][BOARD_SIZE];

    /**
     * Constructor that fills up the {@link #board}
     * array with the necessary elements.
     */
    public GameModel() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(i == 0 && j == 4 || i == 2 && j == 2|| i == 4 && j == 0){
                    board[i][j] = MyCircle.BLUE;
                }
                else{
                    board[i][j] = MyCircle.RED;
                }
            }
        }
    }

    /**
     * Gives information about the game being over, and the winner.
     */
    public void gameOverLogs(){
        Logger.info("GAME OVER!");
        if (blueWon()) Logger.info("Blue Won!");
        if (redWon()) Logger.info("Red Won!");
    }

    /**
     * Moves an element of the {@link #board} array to a certain direction.
     * @param i - the row from which the element will be moved from
     * @param j- the column from which the element will be moved from
     * @param d - the direction of the movement
     */
    public void move(int i, int j, Direction d) {
        try {
            if(!isGameOver()) {
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
            }else gameOverLogs();
        } catch (IndexOutOfBoundsException e) {
            Logger.error("Stay on board!");
        }
    }

    /**
     * Checks if all the elements are the same in an Integer Arraylist.
     * @param rowsOrCols - An Integer arraylist
     * @return false if not all elements are the same, returns true otherwise
     */
    public boolean areAllElementsTheSame(@NotNull final ArrayList<Integer> rowsOrCols) {
        return rowsOrCols.stream().distinct().count() <= 1;
    }

    /**
     * Checks if red won the game, using the
     * {@link #areAllElementsTheSame(ArrayList)} method.
     * @return true if the rows list or the cols list have the same elements
     */
    public boolean redWon() {
        ArrayList<Integer> rows = new ArrayList<>();
        ArrayList<Integer> cols = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; ++i) {
            for (int j = 0; j < BOARD_SIZE; ++j) {
                if (board[i][j] != null && board[i][j] == MyCircle.BLUE){
                    rows.add(i);
                    cols.add(j);
                }
            }
        }
        return areAllElementsTheSame(rows) || areAllElementsTheSame(cols);
    }

    /**
     * Checks if a cell in the grid is empty.
     * @param row - row position of circle
     * @param col - column position of circle
     * @return true if cell is off the board, or if cell is empty.
     */
    public boolean isCellEmpty(final int row, final int col) {
        if (row <= BOARD_SIZE - 1 && row >= 0 && col <= BOARD_SIZE - 1 && col >= 0){
            return board[row][col] == MyCircle.NONE;
        }
        return true;
    }

    /**
     * Checks if a blue Node can move to a certain position or not.
     * @param row - row position of destination
     * @param col - column position of destination
     * @return true if blue can move in any direction
     */
    public boolean canBlueMoveTo(int row, int col){
        boolean b1 = isCellEmpty(row, col);
        if(!b1){
            return board[row][col] != MyCircle.BLUE;
        }
        return false;
    }

    /**
     * Checks if blue player won using {@link #canBlueMoveTo(int, int)}
     * method on each blue circle on board.
     * @return true if all neighbouring cells are empty,
     * or contain a blue circle
     */
    public boolean blueWon() {
        int sum = 0;
        for (int i = 0; i < BOARD_SIZE; ++i) {
            for (int j = 0; j < BOARD_SIZE; ++j) {
                if (board[i][j] != null && board[i][j] == MyCircle.BLUE) {
                    boolean b1 = canBlueMoveTo(i + 1, j);
                    boolean b2 = canBlueMoveTo(i - 1, j);
                    boolean b3 = canBlueMoveTo(i, j - 1);
                    boolean b4 = canBlueMoveTo(i, j + 1);
                    if (!b1 && !b2 && !b3 && !b4) sum++;
                }
            }
        }
        return sum == 3;
    }

    /**
     * Checks if game is over.
     * @return true if either of the players have won.
     */
    public boolean isGameOver(){
        return redWon() || blueWon();
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

    public static void main(String[] args) {
        var model = new GameModel();
        System.out.println(model);
        model.move(0, 4, Direction.LEFT);
        System.out.println(model);
//        model.move(1, 4, Direction.UP);
//        System.out.println(model);
//        model.move(0, 3, Direction.LEFT);
//        System.out.println(model);
//        model.move(1, 3, Direction.UP);
//        System.out.println(model);
//        model.move(4, 0, Direction.RIGHT);
//        System.out.println(model);
//        model.move(3, 0, Direction.DOWN);
//        System.out.println(model);
//        model.move(4, 1, Direction.RIGHT);
//        System.out.println(model);
//        model.move(4, 1, Direction.RIGHT);
    }
}
