package controller;

import data.WinnerRepo;
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
import model.GameModel;
import enums.MyCircle;
import enums.Direction;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import data.Winner;
import service.GameService;

import static model.GameModel.BOARD_SIZE;

public class GameController {

    public GridPane grid;
    public Button upButton;
    public Button downButton;
    public Button leftButton;
    public Button rightButton;
    public Button leaderboard;
    public TextField text;
    public Button exitButton;

    // Static Fields for Game State
    private static int row;
    private static int col;
    private static Circle selectedCircle;
    private static boolean blueTurn = true;
    private static boolean isSomethingSelected = false;
    private static final StringProperty blueName = new SimpleStringProperty();
    private static final StringProperty redName = new SimpleStringProperty();
    private static String winnerName;
    private static String winnerColor;
    private static int countWinnerMoves;

    public static void setBlueName(String blueName) {
        GameController.blueName.set(blueName);
    }

    public static void setRedName(String redName) {
        GameController.redName.set(redName);
    }

    // Instance Fields
    private int countBlueMoves = 0;
    private int countRedMoves = 0;
    private final GameModel model = new GameModel();
    private final GameService gameService = new GameService(model);

    /**
     * Initialize the board and UI state.
     */
    @FXML
    public void initialize(){
        leaderboard.setVisible(false);
        populateBoard();
    }

    /**
     * Populate the board with circles based on the model.
     */
    private void populateBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Color color = model.getBoard()[i][j] == MyCircle.BLUE ? Color.BLUE : Color.RED;
                Circle circle = createCircle(color);
                grid.add(circle, j, i);
            }
        }
    }

    /**
     * Creates a circle with the specified color and sets up event handling.
     * @param color The color of the circle.
     * @return The created circle.
     */
    private Circle createCircle(Color color) {
        var circle = new Circle(30);
        circle.setFill(color);
        if(color == Color.RED) circle.setOnMouseClicked(this::selectRedCircle);
        else circle.setOnMouseClicked(this::selectBlueCircle);
        return circle;
    }

    /**
     * Handle selection of a blue circle.
     */
    public void selectBlueCircle(MouseEvent e) {
        handleSelection(e, Color.LIGHTBLUE, Color.BLUE, blueTurn);
    }

    /**
     * Handle selection of a red circle.
     */
    public void selectRedCircle(MouseEvent e) {
        handleSelection(e, Color.PINK, Color.RED, !blueTurn);
    }

    private void handleSelection(MouseEvent e, Color highlight, Color original, boolean isTurn) {
        if (!gameService.isGameOver() && isTurn) {
            if (isSomethingSelected) {
                selectedCircle.setFill(original);
            }
            selection(e, highlight);
        }
    }

    /**
     * Set the selected circle and update the UI.
     */
    public void selection(MouseEvent e, Color color){
        isSomethingSelected = true;
        selectedCircle = (Circle) e.getSource();
        row = GridPane.getRowIndex(selectedCircle);
        col = GridPane.getColumnIndex(selectedCircle);
        Logger.debug("Click on square (" + row + ", " + col + ")");
        selectedCircle.setFill(color);
    }

    public boolean isFileEmpty(File file){
        return file.length() == 0;
    }

    public void makeWinner() throws IOException {
        var repo = new WinnerRepo();
        if(!isFileEmpty(new File("winners.json"))){
            repo.loadFromFile(new File("winners.json"));
        }
        var winner = Winner.builder()
                .winnerName(winnerName)
                .winnerColor(winnerColor)
                .winnerMoves(countWinnerMoves)
                .build();
        repo.add(winner);
        repo.saveToFile(new File("winners.json"));
    }

    public String changeTurnText(){
        return blueTurn ? blueName.get() + "'s turn!" :
                redName.get() + "'s turn!";
    }

    public void handleGameOver() throws IOException {
        if (!gameService.isGameOver()) return;

        Logger.debug("Game Over!");
        boolean redWon = gameService.redWon();
        winnerName = redWon ? redName.get() : blueName.get();
        winnerColor = redWon ? "Red" : "Blue";
        countWinnerMoves = redWon ? countRedMoves : countBlueMoves;

        text.setText(winnerName + " won!");
        makeWinner();
        disableControls();
        leaderboard.setVisible(true);
    }

    private void disableControls() {
        upButton.setDisable(true);
        downButton.setDisable(true);
        rightButton.setDisable(true);
        leftButton.setDisable(true);
    }

    /**
     * Checks if it's blue's turn to move.
     */
    boolean shouldBlueMove(){
        return blueTurn && isSomethingSelected;
    }

    /**
     * Checks if it's red's turn to move.
     */
    boolean shouldRedMove(){
        return !blueTurn && isSomethingSelected;
    }


    /**
     * Removes a node from the grid by its row and column index.
     */
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

    private void updateBoardState(int newRow, int newCol, Color color) {
        grid.getChildren().remove(selectedCircle);
        removeNodeByRowColumnIndex(newRow, newCol);
        grid.add(selectedCircle, newCol, newRow);
        selectedCircle.setFill(color);
        isSomethingSelected = false;
    }

    /**
     * Updates the UI to reflect a move.
     */
    public void updateUIAfterMove(Direction direction, MyCircle[][] model){
        try {
            int newRow = row + direction.getRowChange();
            int newCol = col + direction.getColChange();
            if (shouldBlueMove() && model[newRow][newCol] == MyCircle.RED) {
                updateBoardState(newRow, newCol, Color.BLUE);
                blueTurn = false;
                countBlueMoves++;
            }else if (shouldRedMove() && model[newRow][newCol] == MyCircle.NONE) {
                updateBoardState(newRow, newCol, Color.RED);
                blueTurn = true;
                countRedMoves++;
            }
            else{
                Logger.warn("Not valid move!");
            }
            Logger.info("Red move count: "+ countRedMoves);
            Logger.info("Blue move count: "+ countBlueMoves);
        } catch (IndexOutOfBoundsException ex) {
            Logger.error("Not valid move. Try again.");
        }
    }

    /**
     * Performs the move and handles post-move actions.
     */
    private void performMove(Direction direction) throws IOException{
        updateUIAfterMove(direction, model.getBoard());
        gameService.makeMove(row, col, direction);
        text.setText(changeTurnText());
        Logger.debug("\n"+model);
        handleGameOver();
    }

    @FXML
    private void up() throws IOException{
        Logger.debug("Up button pressed");
        performMove(Direction.UP);
    }

    @FXML
    private void down() throws IOException{
        Logger.debug("Down button pressed");
        performMove(Direction.DOWN);
    }

    @FXML
    private void left() throws IOException{
        Logger.debug("Left button pressed");
        performMove(Direction.LEFT);
    }

    @FXML
    private void right() throws IOException {
        Logger.debug("Right button pressed");
        performMove(Direction.RIGHT);
    }

    @FXML
    private void exit(){
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
