package service;

import enums.Direction;
import enums.MyCircle;
import model.GameModel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class GameService {

    private final GameModel model;

    public GameService(GameModel model) {
        this.model = model;
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
        for (int i = 0; i < GameModel.BOARD_SIZE; ++i) {
            for (int j = 0; j < GameModel.BOARD_SIZE; ++j) {
                if (model.getBoard()[i][j] != null && model.getBoard()[i][j] == MyCircle.BLUE){
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
        if (row < GameModel.BOARD_SIZE && row >= 0 && col < GameModel.BOARD_SIZE && col >= 0){
            return model.getBoard()[row][col] == MyCircle.NONE;
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
            return model.getBoard()[row][col] != MyCircle.BLUE;
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
        for (int i = 0; i < GameModel.BOARD_SIZE; ++i) {
            for (int j = 0; j < GameModel.BOARD_SIZE; ++j) {
                if (model.getBoard()[i][j] != null && model.getBoard()[i][j] == MyCircle.BLUE) {
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


    public void makeMove(int row, int col, Direction direction) {
        if (!isGameOver()) {
            model.move(row, col, direction);
        }
    }
}
