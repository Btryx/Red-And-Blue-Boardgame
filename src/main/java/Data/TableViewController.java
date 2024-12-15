package Data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TableViewController {

    @FXML
    private TableView<Winner> tableView;

    @FXML
    private TableColumn<Winner, String> name;

    @FXML
    private TableColumn<Winner, String> color;

    @FXML
    private TableColumn<Winner, Integer> moves;

    @FXML
    private void initialize() throws IOException {

        name.setCellValueFactory(new PropertyValueFactory<>("winnerName"));
        color.setCellValueFactory(new PropertyValueFactory<>("winnerColor"));
        moves.setCellValueFactory(new PropertyValueFactory<>("winnerMoves"));

        var repo = new WinnerRepo();
        repo.loadFromFile(new File("winners.json"));
        List<Winner> winners = repo.topTen();

        ObservableList<Winner> observableList = FXCollections.observableArrayList();
        observableList.addAll(winners);
        tableView.setItems(observableList);
    }
}
