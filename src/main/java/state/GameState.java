package state;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;
import lombok.Setter;
import org.tinylog.Logger;

/**
 * Represents the current state of the game board
 */
@Getter
@Setter
public class GameState {

    /**
     * Constant representing the standard board size.
     */
    public static final int BOARD_SIZE = 5;

    /**
     * Internal representation of the game board.
     */
    private final MyCircle[][] board;

    private Circle selectedCircle;
    private int row;
    private int col;
    private boolean blueTurn = true;
    private int countBlueMoves = 0;
    private int countRedMoves = 0;

    public GameState() {
        this.board = new MyCircle[BOARD_SIZE][BOARD_SIZE];
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
     * @param row       Row of the piece to move
     * @param col       Column of the piece to move
     * @param direction Direction of movement
     * @throws IllegalArgumentException if move is invalid
     */
    public void move(int row, int col, Direction direction, boolean blueMove) {
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
            Logger.warn("Move out of board bounds");
            return;
        }

        // Perform move based on piece type
        if (blueMove) {
            performBlueMove(row, col, newRow, newCol);
        } else {
            performRedMove(row, col, newRow, newCol);
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
     * Increases the count for movements the blue player made
     */
    public void incrementBlueMoves() {
        countBlueMoves++;
    }

    /**
     * Increases the count for movements the red player made
     */
    public void incrementRedMoves() {
        countRedMoves++;
    }

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
}
