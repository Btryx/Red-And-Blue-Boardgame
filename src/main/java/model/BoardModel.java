package model;

import state.Direction;
import state.MyCircle;
import org.tinylog.Logger;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents the game board model with game piece management.
 */
public class BoardModel {
    /**
     * Constant representing the standard board size.
     */
    public static final int BOARD_SIZE = 5;

    /**
     * Internal representation of the game board.
     */
    private final MyCircle[][] board;

    /**
     * Constructs a new BoardModel with the initial game setup.
     */
    public BoardModel() {
        board = new MyCircle[BOARD_SIZE][BOARD_SIZE];
        initializeBoard();
    }

    /**
     * Initializes the board with the starting configuration of blue and red circles.
     */
    private void initializeBoard() {
        // Define initial positions for blue and red circles
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = determineInitialCircle(i, j);
            }
        }
    }

    /**
     * Determines the initial circle type for a given board position.
     *
     * @param row Row index
     * @param col Column index
     * @return MyCircle representing the initial circle type
     */
    private MyCircle determineInitialCircle(int row, int col) {
        // Specific initial positions for blue circles
        if ((row == 0 && col == 4) || (row == 2 && col == 2) || (row == 4 && col == 0)) {
            return MyCircle.BLUE;
        }
        return MyCircle.RED;
    }

    /**
     * Creates a deep copy of the current board state.
     *
     * @return A new BoardModel with the same configuration
     */
    public BoardModel copy() {
        BoardModel copiedModel = new BoardModel();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                copiedModel.board[i][j] = this.board[i][j];
            }
        }
        return copiedModel;
    }

    /**
     * Retrieves the current board state.
     *
     * @return A copy of the current board
     */
    public MyCircle[][] getBoard() {
        MyCircle[][] boardCopy = new MyCircle[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.arraycopy(board[i], 0, boardCopy[i], 0, BOARD_SIZE);
        }
        return boardCopy;
    }

    /**
     * Gets the piece at a specific board position.
     *
     * @param row Row index
     * @param col Column index
     * @return MyCircle at the specified position
     */
    public MyCircle getPieceAt(int row, int col) {
        return isWithinBounds(row, col) ? board[row][col] : MyCircle.NONE;
    }

    /**
     * Checks if the given coordinates are within the board boundaries.
     *
     * @param row Row index
     * @param col Column index
     * @return true if coordinates are valid, false otherwise
     */
    public boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }

    /**
     * Moves a piece on the board in the specified direction.
     *
     * @param row Row of the piece to move
     * @param col Column of the piece to move
     * @param direction Direction of movement
     * @throws IllegalArgumentException if move is invalid
     */
    public void move(int row, int col, Direction direction) {
        // Validate input
        if (!isWithinBounds(row, col)) {
            throw new IllegalArgumentException("Invalid starting position");
        }

        MyCircle currentPiece = board[row][col];
        if (currentPiece == MyCircle.NONE) {
            throw new IllegalArgumentException("No piece at the specified position");
        }

        int newRow = row + direction.getRowChange();
        int newCol = col + direction.getColChange();

        // Validate new position
        if (!isWithinBounds(newRow, newCol)) {
            Logger.error("Move out of board bounds");
            return;
        }

        // Perform move based on piece type
        if (currentPiece == MyCircle.BLUE && board[newRow][newCol] == MyCircle.RED) {
            performBlueMove(row, col, newRow, newCol);
        } else if (currentPiece == MyCircle.RED && board[newRow][newCol] == MyCircle.NONE) {
            performRedMove(row, col, newRow, newCol);
        } else {
            Logger.warn("Invalid move attempted");
        }
    }

    /**
     * Handles movement of a blue piece.
     */
    private void performBlueMove(int oldRow, int oldCol, int newRow, int newCol) {
        board[oldRow][oldCol] = MyCircle.NONE;
        board[newRow][newCol] = MyCircle.BLUE;
        Logger.info("Moving blue circle from ({}, {}) to ({}, {})", oldRow, oldCol, newRow, newCol);
    }

    /**
     * Handles movement of a red piece.
     */
    private void performRedMove(int oldRow, int oldCol, int newRow, int newCol) {
        board[oldRow][oldCol] = MyCircle.NONE;
        board[newRow][newCol] = MyCircle.RED;
        Logger.info("Moving red circle from ({}, {}) to ({}, {})", oldRow, oldCol, newRow, newCol);
    }

    /**
     * Generates a string representation of the board for debugging.
     *
     * @return Formatted string of the board state
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (MyCircle[] row : board) {
            for (MyCircle circle : row) {
                sb.append(circle.name()).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Checks equality of board states.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardModel that = (BoardModel) o;
        return Arrays.deepEquals(board, that.board);
    }

    /**
     * Generates hash code for the board state.
     */
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
