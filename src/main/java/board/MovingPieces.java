package board;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.MyCircle;
import org.tinylog.Logger;
import model.Direction;

import static board.GameController.*;

public class MovingPieces{

    public static boolean blueTurn = true;
    public static boolean isSomethingSelected = false;
    public GridPane gridPane;

    int countBlueMoves = 0;
    int countRedMoves = 0;

    MovingPieces(){}

    public void setGridPane(GridPane gridPane) {
        this.gridPane = gridPane;
    }

    boolean shouldBlueMove(){
        return blueTurn && isSomethingSelected;
    }
    boolean shouldRedMove(){
        return !blueTurn && isSomethingSelected;
    }

    public void removeNodeByRowColumnIndex(final int row,final int column) {

        ObservableList<Node> children = gridPane.getChildren();
        for(Node node : children) {
            if(node instanceof Circle && GridPane.getRowIndex(node) == row
                    && GridPane.getColumnIndex(node) == column) {
                Circle circle = (Circle) node;
                gridPane.getChildren().remove(circle);
                break;
            }
        }
    }

    private void movingBlue(int coord1, int coord2){
        gridPane.getChildren().remove(selectedCircle);
        removeNodeByRowColumnIndex(coord1, coord2);
        gridPane.add(selectedCircle, coord2, coord1);
        selectedCircle.setFill(Color.BLUE);
        blueTurn = false;
        countBlueMoves++;
        isSomethingSelected = false;
    }

    private void movingRed(int coord1, int coord2){
        gridPane.getChildren().remove(selectedCircle);
        gridPane.add(selectedCircle, coord2, coord1);
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
