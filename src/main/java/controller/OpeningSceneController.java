package controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

public class OpeningSceneController {

    @FXML
    public TextField secondPlayer;
    @FXML
    public TextField firstPlayer;

    @FXML
    private void initialize() {
        firstPlayer.setPromptText("Enter the name of the blue player");
        secondPlayer.setPromptText("Enter the name of the red player");
        firstPlayer.setText(System.getProperty("user.name.1"));
        secondPlayer.setText(System.getProperty("user.name.2"));
        firstPlayer.setText("Player Blue");
        secondPlayer.setText("Player Red");
    }

    @FXML
    private void switchScene(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui.fxml"));
        Parent root = fxmlLoader.load();
        GameController controller = fxmlLoader.getController();
        controller.setName1(firstPlayer.getText());
        controller.setName2(secondPlayer.getText());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}
