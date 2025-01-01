package com.achraf.controllers;

import com.achraf.models.Don;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

public class SearchDonController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Don> tableView;

    @FXML
    private TableColumn<Don, Integer> idColumn;

    @FXML
    private TableColumn<Don, Integer> donateurIdColumn;

    @FXML
    private TableColumn<Don, Double> montantColumn;

    @FXML
    private TableColumn<Don, String> dateDonColumn;

    private ObservableList<Don> donList = FXCollections.observableArrayList();

    // Property for filtering by the search text
    private StringProperty filterText = new SimpleStringProperty();

    // Constructor for initialization
    public SearchDonController() {
        // Example data, in a real scenario, you would fetch this from your database
        donList.add(new Don(1, 101, 50.0, java.time.LocalDate.now()));
        donList.add(new Don(2, 102, 75.0, java.time.LocalDate.now()));
        donList.add(new Don(3, 103, 100.0, java.time.LocalDate.now()));
    }

    @FXML
    private void initialize() {
        // Initializing TableView columns
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        donateurIdColumn.setCellValueFactory(cellData -> cellData.getValue().donateurIdProperty().asObject());
        montantColumn.setCellValueFactory(cellData -> cellData.getValue().montantProperty().asObject());
        dateDonColumn.setCellValueFactory(cellData -> cellData.getValue().dateDonProperty().asString());

        // Bind the filter text property to the search field
        searchField.textProperty().bindBidirectional(filterText);

        // Filter the list of donations based on the search text
        filterText.addListener((observable, oldValue, newValue) -> filterList(newValue));

        // Set the initial data in the table
        tableView.setItems(donList);
    }

    // Filter the donations list based on the entered text in the search field
    private void filterList(String filter) {
        if (filter == null || filter.isEmpty()) {
            tableView.setItems(donList);
        } else {
            ObservableList<Don> filteredList = FXCollections.observableArrayList();
            for (Don don : donList) {
                if (don.getMontant() == Double.parseDouble(filter) || don.getDateDon().toString().contains(filter)) {
                    filteredList.add(don);
                }
            }
            tableView.setItems(filteredList);
        }
    }

    // Optional: to perform actions when the user presses a key in the search field
    @FXML
    private void onSearchKeyPressed(KeyEvent event) {
        // You can implement additional actions here, for example when the user presses "Enter"
    }
}
