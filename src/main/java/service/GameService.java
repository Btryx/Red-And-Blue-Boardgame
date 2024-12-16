package service;

import state.Direction;
import state.MyCircle;
import model.BoardModel;

import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * Service class responsible for game logic and state validation.
 */
public class GameService {
    private final BoardModel model;

    /**
     * Constructs a GameService with the given board model.
     *
     * @param model the board model to be used for game logic
     */
    public GameService(BoardModel model) {
        this.model = model;
    }

    /**
     * Checks if all elements in a list are the same.
     *
     * @param list List of integers to check
     * @return true if all elements are the same, false otherwise
     */
    public boolean areAllElementsTheSame(final ArrayList<Integer> list) {
        return list.stream().distinct().count() <= 1;
    }

    /**
     * Determines if the red player has won the game.
     *
     * @return true if red player has won, false otherwise
     */
    public boolean redWon() {
        return findBlueCirclePositions(this::collectPositions);
    }

    /**
     * Determines if the blue player has won the game.
     *
     * @return true if blue player has won, false otherwise
     */
    public boolean blueWon() {
        return findBlueCirclePositions(this::checkBlueCircleMovement);
    }

    /**
     * Generic method to process blue circle positions.
     *
     * @param positionProcessor Function to process blue circle positions
     * @return Result of position processing
     */
    private boolean findBlueCirclePositions(PositionProcessor positionProcessor) {
        ArrayList<Integer> rows = new ArrayList<>();
        ArrayList<Integer> cols = new ArrayList<>();

        for (int i = 0; i < BoardModel.BOARD_SIZE; i++) {
            for (int j = 0; j < BoardModel.BOARD_SIZE; j++) {
                if (model.getBoard()[i][j] == MyCircle.BLUE) {
                    rows.add(i);
                    cols.add(j);
                }
            }
        }

        return positionProcessor.process(rows, cols);
    }

    /**
     * Collect positions for red win condition.
     *
     * @param rows Rows of blue circles
     * @param cols Columns of blue circles
     * @return true if all blue circles are in the same row or column
     */
    private boolean collectPositions(ArrayList<Integer> rows, ArrayList<Integer> cols) {
        return areAllElementsTheSame(rows) || areAllElementsTheSame(cols);
    }

    /**
     * Check blue circle movement for blue win condition.
     *
     * @param rows Rows of blue circles
     * @param cols Columns of blue circles
     * @return true if blue circles are blocked
     */
    private boolean checkBlueCircleMovement(ArrayList<Integer> rows, ArrayList<Integer> cols) {
        int blockedCircles = 0;

        for (int i = 0; i < rows.size(); i++) {
            int row = rows.get(i);
            int col = cols.get(i);

            boolean[] movementBlocked = {
                    !canBlueMoveTo(row + 1, col),
                    !canBlueMoveTo(row - 1, col),
                    !canBlueMoveTo(row, col - 1),
                    !canBlueMoveTo(row, col + 1)
            };

            if (IntStream.range(0, movementBlocked.length).allMatch(j -> movementBlocked[j])) {
                blockedCircles++;
            }
        }

        return blockedCircles == 3;
    }

    /**
     * Checks if a cell is empty or out of bounds.
     *
     * @param row Row to check
     * @param col Column to check
     * @return true if cell is empty or out of bounds
     */
    public boolean isCellEmpty(final int row, final int col) {
        return !isWithinBounds(row, col) || model.getBoard()[row][col] == MyCircle.NONE;
    }

    /**
     * Checks if a cell is within board bounds.
     *
     * @param row Row to check
     * @param col Column to check
     * @return true if cell is within bounds
     */
    private boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < BoardModel.BOARD_SIZE
                && col >= 0 && col < BoardModel.BOARD_SIZE;
    }

    /**
     * Checks if a blue circle can move to the specified position.
     *
     * @param row Destination row
     * @param col Destination column
     * @return true if blue can move to the position
     */
    public boolean canBlueMoveTo(int row, int col) {
        return isWithinBounds(row, col)
                && model.getBoard()[row][col] != MyCircle.BLUE
                && !isCellEmpty(row, col);
    }

    /**
     * Checks if the game is over.
     *
     * @return true if either player has won
     */
    public boolean isGameOver() {
        return redWon() || blueWon();
    }

    /**
     * Makes a move on the board if the game is not over.
     *
     * @param row Starting row of the move
     * @param col Starting column of the move
     * @param direction Direction of the move
     */
    public void makeMove(int row, int col, Direction direction) {
        if (!isGameOver()) {
            model.move(row, col, direction);
        }
    }

    /**
     * Functional interface for processing blue circle positions.
     */
    @FunctionalInterface
    private interface PositionProcessor {
        boolean process(ArrayList<Integer> rows, ArrayList<Integer> cols);
    }
}

