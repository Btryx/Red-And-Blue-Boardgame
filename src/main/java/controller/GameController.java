package controller;

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
import javafx.stage.Stage;
import javafx.util.Pair;
import org.tinylog.Logger;
import service.GameService;
import service.WinnerService;
import state.GameState;

import java.io.IOException;
import java.util.Objects;

import static state.GameState.BOARD_SIZE;

public class GameController {
    // UI Components
    @FXML
    private GridPane grid;
    @FXML
    private Button leaderboard;
    @FXML
    private TextField turnText;

    // Game State Management
    private final GameService gameService;
    private final WinnerService winnerService;

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
        this.gameService = new GameService(new GameState());
        this.winnerService = new WinnerService(new WinnerRepository());
    }

    @FXML
    public void initialize() {
        leaderboard.setVisible(false);
        populateBoard();
    }

    private void populateBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Color color = gameService.isPieceBlue(i, j) ? Color.BLUE : Color.RED;
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

    @FXML
    private void handleCellClick(MouseEvent event) {
        Pair<Integer, Integer> indexes = calculateClickedNode(event);
        int newRow = indexes.getKey();
        int newCol = indexes.getValue();

        if (newRow < 0 || newRow >= BOARD_SIZE || newCol < 0 || newCol >= BOARD_SIZE) {
            Logger.warn("Click out of bounds!");
            return;
        }

        // Case 1: Attempting to select a piece
        if (gameService.isPieceSelectable(newRow, newCol)) {
            select(newRow, newCol);
            return;
        }

        // Case 2: Attempting to move the selected piece
        if (gameService.isAnythingSelected()) {
            Circle selectedCircle = gameService.getSelectedCircle();
            move(selectedCircle, newRow, newCol);
        }
    }

    private Pair<Integer, Integer> calculateClickedNode(MouseEvent event) {
        double gridWidth = grid.getWidth();
        double gridHeight = grid.getHeight();
        double cellWidth = gridWidth / BOARD_SIZE;
        double cellHeight = gridHeight / BOARD_SIZE;

        // Determine clicked row and column based on mouse position
        double mouseX = event.getX();
        double mouseY = event.getY();
        int newRow = (int) (mouseY / cellHeight);
        int newCol = (int) (mouseX / cellWidth);
        Logger.debug("Calculated cell: (" + newRow + ", " + newCol + ")");
        return new Pair<>(newRow, newCol);
    }

    private void select(int newRow, int newCol) {
        Circle clickedCircle = findCircleAt(newRow, newCol);
        if (clickedCircle != null) {
            gameService.handleNewSelection(clickedCircle, newRow, newCol);
        }
    }

    private void move(Circle selectedCircle, int newRow, int newCol, Color color) {
        gameService.movePiece(newRow, newCol);
        updateUI(selectedCircle, newRow, newCol, color);
        handleGameOver();
    }

    private void move(Circle selectedCircle, int newRow, int newCol) {
        if (!gameService.isWithinBounds(newRow, newCol)) {
            Logger.warn("Ending position is out of bounds");
            return;
        }

        if (blueMoveIsValid(newRow, newCol)) {
            move(selectedCircle, newRow, newCol, Color.BLUE);

        } else if (redMoveIsValid(newRow, newCol)) {
            move(selectedCircle, newRow, newCol, Color.RED);
        } else {
            Logger.warn("Invalid move!");
        }
    }

    private boolean redMoveIsValid(int newRow, int newCol) {
        return !gameService.isBlueTurn()
                && gameService.isPieceEmpty(newRow, newCol)
                && gameService.isValidNeighbor(newRow, newCol);
    }

    private boolean blueMoveIsValid(int newRow, int newCol) {
        return gameService.isBlueTurn()
                && gameService.isPieceRed(newRow, newCol)
                && gameService.isValidNeighbor(newRow, newCol);
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
        return null;
    }

    private void updateUI(Circle selectedCircle, int newRow, int newCol, Color color) {
        grid.getChildren().remove(selectedCircle);
        removeNodeByRowColumnIndex(newRow, newCol);
        grid.add(selectedCircle, newCol, newRow);
        selectedCircle.setFill(color);
        gameService.resetSelection();
        turnText.setText(getCurrentPlayerTurnText());
    }

    public void removeNodeByRowColumnIndex(final int row, final int column) {
        ObservableList<Node> children = grid.getChildren();
        for (Node node : children) {
            if (node instanceof Circle && GridPane.getRowIndex(node) == row
                    && GridPane.getColumnIndex(node) == column) {
                Circle circle = (Circle) node;
                grid.getChildren().remove(circle);
                break;
            }
        }
    }

    private String getCurrentPlayerTurnText() {
        return gameService.isBlueTurn() ? blueName.get() + "'s turn!" : redName.get() + "'s turn!";
    }

    private void handleGameOver() {
        if (!gameService.isGameOver()) return;

        Logger.debug("Game Over!");
        boolean redWon = gameService.redWon();
        String winnerName = redWon ? redName.get() : blueName.get();
        String winnerColor = redWon ? "Red" : "Blue";
        int winnerMoves = redWon ? gameService.getRedMoves() : gameService.getBlueMoves();

        turnText.setText(winnerName + " won!");
        leaderboard.setVisible(true);
        saveWinner(winnerName, winnerColor, winnerMoves);
    }

    private void saveWinner(String winnerName, String winnerColor, int winnerMoves){
        try {
            winnerService.saveWinner(winnerName, winnerColor, winnerMoves);
        } catch (IOException e) {
            Logger.warn("Couldn't save to file");
        }

    }

    @FXML
    private void switchToLeaderBoard(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/table.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
        stage.show();
        Logger.debug("Switching to leaderboard...");
    }

    @FXML
    public void restartGame(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.
                    requireNonNull(getClass().getResource("/first.fxml")));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            Logger.debug("Restarting...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void exit() {
        Logger.debug("Exiting...");
        Platform.exit();
    }
}

