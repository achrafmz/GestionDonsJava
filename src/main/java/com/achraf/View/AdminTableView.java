package com.achraf.views;

import com.achraf.controllers.AdminController;
import com.achraf.models.Admin;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AdminTableView extends BorderPane {
    private TableView<Admin> tableView;
    private AdminController adminController;

    public AdminTableView() {
        adminController = new AdminController();
        initializeUI();
    }

    private void initializeUI() {
        tableView = new TableView<>();
        TableColumn<Admin, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

        TableColumn<Admin, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());

        TableColumn<Admin, String> passwordColumn = new TableColumn<>("Password");
        passwordColumn.setCellValueFactory(cellData -> cellData.getValue().passwordProperty());

        TableColumn<Admin, Void> updateColumn = new TableColumn<>("Update");
        updateColumn.setCellFactory(param -> new TableCell<>() {
            private final Button updateButton = new Button("Update");

            {
                updateButton.setOnAction(event -> {
                    Admin admin = getTableView().getItems().get(getIndex());
                    showUpdateDialog(admin);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(updateButton);
                }
            }
        });

        TableColumn<Admin, Void> deleteColumn = new TableColumn<>("Delete");
        deleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    Admin admin = getTableView().getItems().get(getIndex());
                    adminController.deleteAdmin(admin.getId());
                    refreshTable();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        tableView.getColumns().addAll(idColumn, usernameColumn, passwordColumn, updateColumn, deleteColumn);

        ObservableList<Admin> admins = adminController.getAdmins();
        tableView.setItems(admins);

        setCenter(tableView);

        Button addButton = new Button("Add Admin");
        addButton.setOnAction(event -> showAddDialog());

        VBox vbox = new VBox(addButton);
        setBottom(vbox);
    }

    private void showAddDialog() {
        // Implémentation du dialogue pour ajouter un administrateur
    }

    private void showUpdateDialog(Admin admin) {
        // Implémentation du dialogue pour mettre à jour un administrateur
    }

    private void refreshTable() {
        tableView.setItems(adminController.getAdmins());
    }
}
