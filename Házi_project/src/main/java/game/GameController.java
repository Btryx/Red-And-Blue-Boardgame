package game;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javafx.event.ActionEvent;

public class GameController {

    @FXML
    private GridPane grid;


    @FXML
    private void initialize() {
        for (int i = 0; i < grid.getRowCount(); i++) {
            for (int j = 0; j < grid.getColumnCount(); j++) {
                if(i == 0 && j == 4 || i == 2 && j == 2|| i == 4 && j == 0){
                    var circle = createBlueCircle();
                    grid.add(circle, j, i);
                }
                else{
                    var circle = createRedCircle();
                    grid.add(circle, j, i);
                }
            }
        }
    }

    private Circle createRedCircle() {
        var circle = new Circle(30);
        circle.setFill(Color.RED);
        return circle;
    }

    private Circle createBlueCircle() {
        var circle = new Circle(30);
        circle.setFill(Color.BLUE);
        return circle;
    }


    @FXML
    private void up(ActionEvent e){
        System.out.println("up");
    }
    @FXML
    private void down(ActionEvent e){
        System.out.println("down");
    }
    @FXML
    private void left(ActionEvent e){
        System.out.println("left");
    }
    @FXML
    private void right(ActionEvent e){
        System.out.println("right");
    }

}
