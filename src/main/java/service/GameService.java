package service;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.tinylog.Logger;
import state.Direction;
import state.GameState;
import state.MyCircle;

import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * Service class responsible for game logic and state validation.
 */
public class GameService {
    private final GameState gameState;

    /**
     * Constructs a GameService with the given board model.
     *
     * @param gameState the board model to be used for game logic
     */
    public GameService(GameState gameState) {
        this.gameState = gameState;
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

        for (int i = 0; i < GameState.BOARD_SIZE; i++) {
            for (int j = 0; j < GameState.BOARD_SIZE; j++) {
                if (gameState.getBoard()[i][j] == MyCircle.BLUE) {
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
     * Checks if a cell is within board bounds.
     *
     * @param row Row to check
     * @param col Column to check
     * @return true if cell is within bounds
     */
    private boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < GameState.BOARD_SIZE
                && col >= 0 && col < GameState.BOARD_SIZE;
    }

    /**
     * Checks if a blue circle can move to the specified position.
     *
     * @param row Destination row
     * @param col Destination column
     * @return true if blue can move to the position
     */
    public boolean canBlueMoveTo(int row, int col) {
        return isWithinBounds(row, col) && gameState.getPieceAt(row, col) == MyCircle.RED;
    }

    public boolean isPieceBlue(int row, int col) {
        return gameState.getPieceAt(row, col) == MyCircle.BLUE;
    }

    public boolean isPieceRed(int row, int col) {
        return gameState.getPieceAt(row, col) == MyCircle.RED;
    }

    public boolean isPieceEmpty(int row, int col) {
        return gameState.getPieceAt(row, col) == MyCircle.NONE;
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
     * @param newRow Ending row of the move
     * @param newCol Ending column of the move
     */
    public void makeMove(int newRow, int newCol, boolean blueMove) {
        if (!isGameOver()) {
            Direction direction = getDirection(gameState.getRow(), gameState.getCol(), newRow, newCol);
            gameState.move(gameState.getRow(), gameState.getCol(), direction, blueMove);
            deselectPreviousCircle();
            turnChange(!blueMove);
        }
    }

    private Direction getDirection(int row, int col, int newRow, int newCol) {
        if (row == newRow) {
            if (col < newCol) return Direction.RIGHT;
            else return Direction.LEFT;
        }
        if (col == newCol && row < newRow) {
            return Direction.DOWN;
        }
        return Direction.UP;
    }

    public void handleNewSelection(Circle clickedCircle, int clickedRow, int clickedCol) {
        deselectPreviousCircle();
        setSelectedCircle(clickedCircle, clickedRow, clickedCol, isBlueTurn() ? Color.DARKBLUE : Color.DARKRED);
        Logger.debug("Selected piece at (" + clickedRow + ", " + clickedCol + ")");
    }

    private void deselectPreviousCircle() {
        if (isAnythingSelected()) {
            gameState.getSelectedCircle().setFill(isBlueTurn() ? Color.BLUE : Color.RED);
        }
    }

    public boolean isValidNeighbor(int clickedRow, int clickedCol) {
        int currentRow = gameState.getRow();
        int currentCol = gameState.getCol();

        return (Math.abs(currentRow - clickedRow) == 1 && currentCol == clickedCol) ||
                (Math.abs(currentCol - clickedCol) == 1 && currentRow == clickedRow);
    }

    public void turnChange(boolean blueTurnNext) {
        if (blueTurnNext) {
            gameState.setBlueTurn(true);
            gameState.incrementRedMoves();
        } else {
            gameState.setBlueTurn(false);
            gameState.incrementBlueMoves();
        }
    }

    public void setSelectedCircle(Circle circle, int row, int col, Color highlightColor) {
        gameState.setSelectedCircle(circle);
        gameState.setRow(row);
        gameState.setCol(col);
        circle.setFill(highlightColor);
    }

    public Circle getSelectedCircle() {
        return gameState.getSelectedCircle();
    }

    public int getRedMoves() {
        return gameState.getCountRedMoves();
    }

    public int getBlueMoves() {
        return gameState.getCountBlueMoves();
    }

    public boolean isBlueTurn() {
        return gameState.isBlueTurn();
    }

    public void resetSelection() {
        gameState.setSelectedCircle(null);
    }

    public boolean isAnythingSelected() {
        return gameState.getSelectedCircle() != null;
    }

    /**
     * Functional interface for processing blue circle positions.
     */
    @FunctionalInterface
    private interface PositionProcessor {
        boolean process(ArrayList<Integer> rows, ArrayList<Integer> cols);
    }
}

