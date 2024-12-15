package Data;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class TableApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/table.fxml")));
        Scene scene = new Scene(root);
        stage.setTitle("Winners");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}