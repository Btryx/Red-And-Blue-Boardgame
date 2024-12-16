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
import javafx.stage.Stage;
import model.BoardModel;
import org.tinylog.Logger;
import service.GameService;
import state.Direction;
import state.GameState;
import state.MyCircle;

import java.io.File;
import java.io.IOException;
import static model.BoardModel.BOARD_SIZE;

public class GameController {
    // UI Components
    @FXML private GridPane grid;
    @FXML private Button upButton;
    @FXML private Button downButton;
    @FXML private Button leftButton;
    @FXML private Button rightButton;
    @FXML private Button leaderboard;
    @FXML private TextField text;

    // Game State Management
    private GameState gameState;
    private final BoardModel model;
    private final GameService gameService;
    private final WinnerRepository winnerRepo;

    // Player Information
    private final StringProperty blueName = new SimpleStringProperty();
    private final StringProperty redName = new SimpleStringProperty();

    public GameController() {
        this.model = new BoardModel();
        this.gameService = new GameService(model);
        this.gameState = new GameState();
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
                Color color = model.getBoard()[i][j] == MyCircle.BLUE ? Color.BLUE : Color.RED;
                Circle circle = createCircle(color, i, j);
                grid.add(circle, j, i);
            }
        }
    }

    private Circle createCircle(Color color, int row, int col) {
        Circle circle = new Circle(30);
        circle.setFill(color);

        circle.setOnMouseClicked(event -> {
            if (color == Color.RED) {
                handleCircleSelection(event, Color.PINK, Color.RED, !gameState.isBlueTurn());
            } else {
                handleCircleSelection(event, Color.LIGHTBLUE, Color.BLUE, gameState.isBlueTurn());
            }
        });

        return circle;
    }

    private void handleCircleSelection(MouseEvent event, Color highlightColor, Color originalColor, boolean isPlayerTurn) {
        if (!gameService.isGameOver() && isPlayerTurn) {
            deselectPreviousCircle();
            selectCircle(event, highlightColor);
        }
    }

    private void deselectPreviousCircle() {
        if (gameState.isAnythingSelected()) {
            gameState.getSelectedCircle().setFill(gameState.isBlueTurn() ? Color.BLUE : Color.RED);
        }
    }

    private void selectCircle(MouseEvent event, Color highlightColor) {
        Circle selectedCircle = (Circle) event.getSource();
        int row = GridPane.getRowIndex(selectedCircle);
        int col = GridPane.getColumnIndex(selectedCircle);

        gameState.setSelectedCircle(selectedCircle, row, col, highlightColor);
        Logger.debug("Click on square (" + row + ", " + col + ")");
    }

    private void updateBoardStateAfterMove(Direction direction) throws IOException {
        MyCircle[][] board = model.getBoard();
        int row = gameState.getRow();
        int col = gameState.getCol();
        Circle selectedCircle = gameState.getSelectedCircle();

        int newRow = row + direction.getRowChange();
        int newCol = col + direction.getColChange();

        if (gameState.isBlueTurn() && board[newRow][newCol] == MyCircle.RED) {
            performBlueMove(selectedCircle, newRow, newCol);
        } else if (!gameState.isBlueTurn() && board[newRow][newCol] == MyCircle.NONE) {
            performRedMove(selectedCircle, newRow, newCol);
        } else {
            Logger.warn("Invalid move!");
            return;
        }

        gameService.makeMove(row, col, direction);
        updateGameStatus();
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

    private void updateGameStatus() throws IOException {
        text.setText(getCurrentPlayerTurnText());
        Logger.debug("\n" + model);
        handleGameOver();
    }

    private String getCurrentPlayerTurnText() {
        return gameState.isBlueTurn() ? blueName.get() + "'s turn!" : redName.get() + "'s turn!";
    }

    private void handleGameOver() throws IOException {
        if (!gameService.isGameOver()) return;

        Logger.debug("Game Over!");
        boolean redWon = gameService.redWon();
        String winnerName = redWon ? redName.get() : blueName.get();
        String winnerColor = redWon ? "Red" : "Blue";
        int winnerMoves = redWon ? gameState.getRedMoves() : gameState.getBlueMoves();

        text.setText(winnerName + " won!");
        saveWinner(winnerName, winnerColor, winnerMoves);
        disableGameControls();
        leaderboard.setVisible(true);
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

    private void disableGameControls() {
        upButton.setDisable(true);
        downButton.setDisable(true);
        rightButton.setDisable(true);
        leftButton.setDisable(true);
    }

    @FXML
    private void up() throws IOException{
        Logger.debug("Up button pressed");
        updateBoardStateAfterMove(Direction.UP);
    }

    @FXML
    private void down() throws IOException{
        Logger.debug("Down button pressed");
        updateBoardStateAfterMove(Direction.DOWN);
    }

    @FXML
    private void left() throws IOException{
        Logger.debug("Left button pressed");
        updateBoardStateAfterMove(Direction.LEFT);
    }

    @FXML
    private void right() throws IOException {
        Logger.debug("Right button pressed");
        updateBoardStateAfterMove(Direction.RIGHT);
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

    // Setter methods for player names
    public void setBlueName(String blueName) {
        this.blueName.set(blueName);
    }

    public void setRedName(String redName) {
        this.redName.set(redName);
    }
}
