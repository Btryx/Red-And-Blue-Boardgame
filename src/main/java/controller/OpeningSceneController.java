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

    private static final String DEFAULT_BLUE_NAME = "Player Blue";
    private static final String DEFAULT_RED_NAME = "Player Red";
    private static final String BLUE_PROMPT = "Enter the name of the blue player";
    private static final String RED_PROMPT = "Enter the name of the red player";
    private static final String GAME_SCENE_PATH = "/ui.fxml";

    @FXML
    public TextField secondPlayer;
    @FXML
    public TextField firstPlayer;

    @FXML
    private void initialize() {
        firstPlayer.setPromptText(BLUE_PROMPT);
        secondPlayer.setPromptText(RED_PROMPT);
        firstPlayer.setText(System.getProperty("user.name.1", DEFAULT_BLUE_NAME));
        secondPlayer.setText(System.getProperty("user.name.2", DEFAULT_RED_NAME));
    }

    @FXML
    private void switchScene(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui.fxml"));
        Parent root = fxmlLoader.load();

        GameController.setBlueName(firstPlayer.getText());
        GameController.setRedName(secondPlayer.getText());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}
