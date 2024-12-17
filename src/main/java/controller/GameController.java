package controller;

import data.Winner;
import data.WinnerRepository;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.tinylog.Logger;
import service.GameService;
import state.Direction;
import state.GameState;
import state.MyCircle;

import java.io.File;
import java.io.IOException;
import static state.GameState.BOARD_SIZE;

public class GameController {
    // UI Components
    @FXML private GridPane grid;
    @FXML private Button leaderboard;
    @FXML private TextField turnText;

    // Game State Management
    private final GameState gameState;
    private final GameService gameService;
    private final WinnerRepository winnerRepo;

    // Player Information
    private final StringProperty blueName = new SimpleStringProperty();
    private final StringProperty redName = new SimpleStringProperty();


    public void setBlueName(String blueName) {
        this.blueName.set(blueName);
    }

    public void setRedName(String redName) {
        this.redName.set(redName);
    }

    public GameController() {
        this.gameState = new GameState();
        this.gameService = new GameService(gameState);
        this.winnerRepo = new WinnerRepository();
    }

    @FXML
    public void initialize() {
        leaderboard.setVisible(false);
        populateBoard();
    }

    private void populateBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Color color = gameState.getPieceAt(i,j) == MyCircle.BLUE ? Color.BLUE : Color.RED;
                Circle circle = createCircle(color);
                grid.add(circle, j, i);
            }
        }
    }

    private Circle createCircle(Color color) {
        Circle circle = new Circle(30);
        circle.setFill(color);
        return circle;
    }

    private void handleNewSelection(Circle clickedCircle, int clickedRow, int clickedCol) {
        deselectPreviousCircle();
        gameState.setSelectedCircle(clickedCircle, clickedRow, clickedCol, gameState.isBlueTurn() ? Color.LIGHTBLUE : Color.PINK);
        Logger.debug("Selected piece at (" + clickedRow + ", " + clickedCol + ")");
    }

    private void deselectPreviousCircle() {
        if (gameState.isAnythingSelected()) {
            gameState.getSelectedCircle().setFill(gameState.isBlueTurn() ? Color.BLUE : Color.RED);
        }
    }

    private boolean isValidNeighbor(int clickedRow, int clickedCol) {
        int currentRow = gameState.getRow();
        int currentCol = gameState.getCol();

        return (Math.abs(currentRow - clickedRow) == 1 && currentCol == clickedCol) ||
                (Math.abs(currentCol - clickedCol) == 1 && currentRow == clickedRow);
    }

    @FXML
    private void handleCellClick(MouseEvent event) {
        double gridWidth = grid.getWidth();
        double gridHeight = grid.getHeight();
        double cellWidth = gridWidth / BOARD_SIZE;
        double cellHeight = gridHeight / BOARD_SIZE;

        // Determine clicked row and column based on mouse position
        double mouseX = event.getX();
        double mouseY = event.getY();
        int newRow = (int) (mouseY / cellHeight);
        int newCol = (int) (mouseX / cellWidth);

        Circle selectedCircle = gameState.getSelectedCircle();

        if (newRow < 0 || newRow >= BOARD_SIZE || newCol < 0 || newCol >= BOARD_SIZE) {
            Logger.warn("Click out of bounds!");
            return;
        }

        Logger.debug("Mouse clicked at (" + mouseX + ", " + mouseY + ")");
        Logger.debug("Calculated cell: (" + newRow + ", " + newCol + ")");

        // Case 1: Attempting to select a piece
        if (gameState.isBlueTurn() && gameState.getPieceAt(newRow, newCol) == MyCircle.BLUE) {
            Circle clickedCircle = findCircleAt(newRow, newCol);
            if (clickedCircle != null) {
                handleNewSelection(clickedCircle, newRow, newCol);
            }
            return;
        } else if (!gameState.isBlueTurn() && gameState.getPieceAt(newRow, newCol) == MyCircle.RED) {
            Circle clickedCircle = findCircleAt(newRow, newCol);
            if (clickedCircle != null) {
                handleNewSelection(clickedCircle, newRow, newCol);
            }
            return;
        }

        // Case 2: Attempting to move the selected piece
        if (gameState.isAnythingSelected()) {
            if (gameState.isBlueTurn() && gameState.getPieceAt(newRow, newCol) == MyCircle.RED && isValidNeighbor(newRow, newCol)) {
                performBlueMove(selectedCircle, newRow, newCol);
                gameService.makeMove(gameState.getRow(), gameState.getCol(), getDirection(gameState.getRow(), gameState.getCol(), newRow, newCol));
                deselectPreviousCircle();
                updateGameStatus();
            } else if (!gameState.isBlueTurn() && gameState.getPieceAt(newRow, newCol) == MyCircle.NONE && isValidNeighbor(newRow, newCol)) {
                performRedMove(selectedCircle, newRow, newCol);
                gameService.makeMove(gameState.getRow(), gameState.getCol(), getDirection(gameState.getRow(), gameState.getCol(), newRow, newCol));
                deselectPreviousCircle();
                updateGameStatus();
            } else {
                Logger.warn("Invalid move!");
            }
        }
    }

    private Circle findCircleAt(int row, int col) {
        for (Node node : grid.getChildren()) {
            Integer nodeRow = GridPane.getRowIndex(node);
            Integer nodeCol = GridPane.getColumnIndex(node);

            if (nodeRow == null) nodeRow = -1;
            if (nodeCol == null) nodeCol = -1;

            if (nodeRow == row && nodeCol == col && node instanceof Circle) {
                return (Circle) node;
            }
        }
        return null; // Return null if no circle is found at the specified position
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

    private void performBlueMove(Circle selectedCircle, int newRow, int newCol) {
        updateGridForMove(selectedCircle, newRow, newCol, Color.BLUE);
        gameState.switchTurn(false);
        gameState.incrementBlueMoves();
    }

    private void performRedMove(Circle selectedCircle, int newRow, int newCol) {
        updateGridForMove(selectedCircle, newRow, newCol, Color.RED);
        gameState.switchTurn(true);
        gameState.incrementRedMoves();
    }

    private void updateGridForMove(Circle selectedCircle, int newRow, int newCol, Color color) {
        grid.getChildren().remove(selectedCircle);
        removeNodeByRowColumnIndex(newRow, newCol);
        grid.add(selectedCircle, newCol, newRow);
        selectedCircle.setFill(color);
        gameState.resetSelection();
    }

    public void removeNodeByRowColumnIndex(final int row,final int column) {
        ObservableList<Node> children = grid.getChildren();
        for(Node node : children) {
            if(node instanceof Circle && GridPane.getRowIndex(node) == row
                    && GridPane.getColumnIndex(node) == column) {
                Circle circle = (Circle) node;
                grid.getChildren().remove(circle);
                break;
            }
        }
    }

    private void updateGameStatus() {
        turnText.setText(getCurrentPlayerTurnText());
        handleGameOver();
    }

    private String getCurrentPlayerTurnText() {
        return gameState.isBlueTurn() ? blueName.get() + "'s turn!" : redName.get() + "'s turn!";
    }

    private void handleGameOver() {
        if (!gameService.isGameOver()) return;

        try {
            Logger.debug("Game Over!");
            boolean redWon = gameService.redWon();
            String winnerName = redWon ? redName.get() : blueName.get();
            String winnerColor = redWon ? "Red" : "Blue";
            int winnerMoves = redWon ? gameState.getCountRedMoves() : gameState.getCountBlueMoves();

            turnText.setText(winnerName + " won!");
            saveWinner(winnerName, winnerColor, winnerMoves);
            leaderboard.setVisible(true);
        } catch (IOException e) {
            Logger.error("File not found");
        }

    }

    private void saveWinner(String winnerName, String winnerColor, int winnerMoves) throws IOException {
        File winnerFile = new File("winners.json");
        if (!isFileEmpty(winnerFile)) {
            winnerRepo.loadFromFile(winnerFile);
        }

        Winner winner = Winner.builder()
                .winnerName(winnerName)
                .winnerColor(winnerColor)
                .winnerMoves(winnerMoves)
                .build();

        winnerRepo.add(winner);
        winnerRepo.saveToFile(winnerFile);
    }

    private boolean isFileEmpty(File file) {
        return file.length() == 0;
    }

    @FXML
    private void exit() {
        Logger.debug("Exiting...");
        Platform.exit();
    }

    @FXML
    private void switchToTable(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/table.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
        stage.show();
        Logger.debug("Switching to leaderboard...");
    }
}
