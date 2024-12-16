package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Main application class for the Board Game JavaFX application.
 * This class is responsible for launching the initial application scene.
 */
public class GameApp extends Application {

    /**
     * Starts the JavaFX application by loading the initial FXML scene.
     *
     * @param primaryStage the primary stage for this application, which the scene will be set on
     * @throws IOException if the FXML resource cannot be loaded
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(Objects.
                requireNonNull(getClass().getResource("/first.fxml")));
        primaryStage.setTitle("Board Game");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
