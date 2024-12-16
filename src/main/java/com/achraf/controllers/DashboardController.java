package com.achraf.controllers;

import com.achraf.models.Admin;
import com.achraf.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardController {

    @FXML
    private VBox mainContent;

    private ObservableList<Admin> adminList = FXCollections.observableArrayList();

    @FXML
    public void handleGererAdmins() {
        TableView<Admin> table = new TableView<>();

        // Colonnes
        TableColumn<Admin, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

        TableColumn<Admin, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());

        TableColumn<Admin, String> passwordColumn = new TableColumn<>("Password");
        passwordColumn.setCellValueFactory(cellData -> cellData.getValue().passwordProperty());

        // Colonne des actions (supprimer)
        TableColumn<Admin, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Supprimer");
            private final Button updateButton = new Button("Modifier");

            {
                deleteButton.setOnAction(event -> {
                    Admin admin = getTableView().getItems().get(getIndex());
                    deleteAdmin(admin);
                });

                updateButton.setOnAction(event -> {
                    Admin admin = getTableView().getItems().get(getIndex());
                    showUpdateAdminDialog(admin);
                });

                // Style des boutons
                deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                updateButton.setStyle("-fx-background-color: orange; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    VBox box = new VBox(5, deleteButton, updateButton);
                    setGraphic(box);
                }
            }
        });

        table.getColumns().addAll(idColumn, usernameColumn, passwordColumn, actionColumn);
        table.setItems(adminList);

        // Charger les admins depuis la base de données
        loadAdmins();

        // Ajouter le tableau au contenu principal
        mainContent.getChildren().clear();
        mainContent.getChildren().add(table);
    }

    private void loadAdmins() {
        adminList.clear();
        String query = "SELECT * FROM admins";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                adminList.add(new Admin(id, username, password));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteAdmin(Admin admin) {
        String query = "DELETE FROM admins WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, admin.getId());
            stmt.executeUpdate();
            adminList.remove(admin);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showUpdateAdminDialog(Admin admin) {
        TextInputDialog dialog = new TextInputDialog(admin.getUsername());
        dialog.setTitle("Modifier Admin");
        dialog.setHeaderText("Modifier l'admin : " + admin.getId());
        dialog.setContentText("Nouveau username :");

        dialog.showAndWait().ifPresent(newUsername -> {
            String query = "UPDATE admins SET username = ? WHERE id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, newUsername);
                stmt.setInt(2, admin.getId());
                stmt.executeUpdate();
                admin.setUsername(newUsername);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
