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
import java.util.Objects;
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

    public static int row;
    public static int col;
    public static Circle selectedCircle;
    public static boolean blueTurn = true;
    public static boolean isSomethingSelected = false;

    int countBlueMoves = 0;
    int countRedMoves = 0;

    private static final StringProperty name = new SimpleStringProperty();
    private static final StringProperty name2 = new SimpleStringProperty();
    private static String winnerName;
    private static String winnerColor;
    private static int countWinnerMoves;


    public static String getName1() {
        return name.get();
    }

    public static String getName2() {
        return name2.get();
    }

    public static String getWinnerName() {
        return winnerName;
    }

    public static void setWinnerName(String winnerName) {
        GameController.winnerName = winnerName;
    }

    public static String getWinnerColor() {
        return winnerColor;
    }

    public static void setWinnerColor(String winnerColor) {
        GameController.winnerColor = winnerColor;
    }

    public static int getCountWinnerMoves() {
        return countWinnerMoves;
    }

    public static void setCountWinnerMoves(int countWinnerMoves) {
        GameController.countWinnerMoves = countWinnerMoves;
    }

    public void setName1(String name) {
        GameController.name.set(name);
    }

    public void setName2(String name) {
        name2.set(name);
    }

    private final GameModel model = new GameModel();
    private final GameService gameService = new GameService(model);


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

    @FXML
    public void initialize(){
        text.setEditable(false);
        Logger.debug("\n"+model);
        leaderboard.setVisible(false);
        populateBoard();
    }

    private void performMove(Direction direction) throws IOException{
        moveBoardPieces(direction, model.getBoard());
        gameService.makeMove(row, col, direction);
        text.setText(changeTurnText());
        Logger.debug("\n"+model);
        handleGameOver();
    }


    private void populateBoard(){
        Circle c;
        for(int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(Objects.equals(model.getBoard()[i][j], MyCircle.BLUE)){
                    c = createCircle(Color.BLUE);
                }else {
                    c = createCircle(Color.RED);
                }
                grid.add(c, j, i);
            }
        }
    }

    private Circle createCircle(Color color) {
        var circle = new Circle(30);
        circle.setFill(color);
        if(color == Color.RED) circle.setOnMouseClicked(this::selectRedCircle);
        else circle.setOnMouseClicked(this::selectBlueCircle);
        return circle;
    }

    public void selectBlueCircle(MouseEvent e){
        if(!gameService.isGameOver() && blueTurn && !isSomethingSelected) {
            selection(e, Color.LIGHTBLUE);
        }else if(!gameService.isGameOver() && blueTurn && isSomethingSelected) {
            selectedCircle.setFill(Color.BLUE);
            selection(e, Color.LIGHTBLUE);
        }
    }

    public void selectRedCircle(MouseEvent e){
        if(!gameService.isGameOver() && !blueTurn && !isSomethingSelected) {
            selection(e, Color.PINK);
        }else if(!gameService.isGameOver() && !blueTurn && isSomethingSelected) {
            selectedCircle.setFill(Color.RED);
            selection(e, Color.PINK);
        }
    }

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
                .winnerName(getWinnerName())
                .winnerColor(getWinnerColor())
                .winnerMoves(getCountWinnerMoves())
                .build();
        repo.add(winner);
        repo.saveToFile(new File("winners.json"));
    }

    public String changeTurnText(){
        return blueTurn ? GameController.getName1() + "'s turn!" :
                GameController.getName2() + "'s turn!";
    }

    public void handleGameOver() throws IOException{
        if(gameService.isGameOver()){
            Logger.debug("Game Over!");
            if(gameService.redWon()) {
                Logger.debug("Red Won!!");
                text.setText(GameController.getName2() + " won!");
                setWinnerName(GameController.getName2());
                setWinnerColor("Red");
                setCountWinnerMoves(countRedMoves);
            }
            else {
                Logger.debug("Blue Won!!");
                text.setText(GameController.getName1() + " won!");
                setWinnerName(GameController.getName1());
                setWinnerColor("Blue");
                setCountWinnerMoves(countBlueMoves);

            }
            makeWinner();
            upButton.setDisable(true);
            downButton.setDisable(true);
            rightButton.setDisable(true);
            leftButton.setDisable(true);
            leaderboard.setVisible(true);
        }
    }

    boolean shouldBlueMove(){
        return blueTurn && isSomethingSelected;
    }
    boolean shouldRedMove(){
        return !blueTurn && isSomethingSelected;
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

    private void movingBlue(int coord1, int coord2){
        grid.getChildren().remove(selectedCircle);
        removeNodeByRowColumnIndex(coord1, coord2);
        grid.add(selectedCircle, coord2, coord1);
        selectedCircle.setFill(Color.BLUE);
        blueTurn = false;
        countBlueMoves++;
        isSomethingSelected = false;
    }

    private void movingRed(int coord1, int coord2){
        grid.getChildren().remove(selectedCircle);
        grid.add(selectedCircle, coord2, coord1);
        selectedCircle.setFill(Color.RED);
        blueTurn = true;
        countRedMoves++;
        isSomethingSelected = false;
    }

    public void moveBoardPieces(Direction direction, MyCircle[][] model){
        try {
            int getRow = row + direction.getRowChange();
            int getCol = col + direction.getColChange();
            if (shouldBlueMove() && model[getRow][getCol] == MyCircle.RED) {
                movingBlue(getRow, getCol);
            }else if (shouldRedMove() && model[getRow][getCol] == MyCircle.NONE) {
                movingRed(getRow, getCol);
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
}
