package controller;

import data.Winner;
import data.WinnerRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import service.WinnerService;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class LeaderBoardController {

    @FXML
    private TableView<Winner> tableView;

    @FXML
    private TableColumn<Winner, String> name;

    @FXML
    private TableColumn<Winner, String> color;

    @FXML
    private TableColumn<Winner, Integer> moves;

    @FXML
    private void initialize() {
        name.setCellValueFactory(new PropertyValueFactory<>("winnerName"));
        color.setCellValueFactory(new PropertyValueFactory<>("winnerColor"));
        moves.setCellValueFactory(new PropertyValueFactory<>("winnerMoves"));

        var winnerService = new WinnerService(new WinnerRepository());
        winnerService.loadWinnersFromFile(new File("winners.json"));
        List<Winner> winners = winnerService.getTopTenWinners();

        ObservableList<Winner> observableList = FXCollections.observableArrayList();
        observableList.addAll(winners);
        tableView.setItems(observableList);
    }

    @FXML
    public void restartGame(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.
                    requireNonNull(getClass().getResource("/first.fxml")));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
