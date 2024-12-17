package controller;

import data.Winner;
import data.WinnerRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import service.WinnerService;

import java.io.File;
import java.util.List;

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
}
