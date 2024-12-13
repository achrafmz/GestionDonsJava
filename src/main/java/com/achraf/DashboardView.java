package com.achraf;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

public class DashboardView {
    private Scene scene;
    private ObservableList<String> donateursList;

    public DashboardView(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // Navigation bar
        HBox navBar = new HBox(20);
        navBar.setPadding(new Insets(10));
        navBar.setStyle("-fx-background-color: #4CAF50; -fx-padding: 10px;");
        Label title = new Label("Tableau de Bord");
        title.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        navBar.getChildren().add(title);

        // Donateurs section
        VBox donateursSection = new VBox(10);
        donateursSection.setPadding(new Insets(10));

        Label donateursLabel = new Label("Liste des Donateurs");
        donateursLabel.setStyle("-fx-font-size: 16px;");

        ListView<String> donateursListView = new ListView<>();
        donateursList = FXCollections.observableArrayList();
        donateursListView.setItems(donateursList);
        loadDonateurs();

        HBox crudButtons = new HBox(10);
        Button addButton = new Button("Ajouter");
        Button updateButton = new Button("Modifier");
        Button deleteButton = new Button("Supprimer");

        crudButtons.getChildren().addAll(addButton, updateButton, deleteButton);

        // Add functionality
        addButton.setOnAction(e -> openAddDonateurDialog());
        updateButton.setOnAction(e -> openUpdateDonateurDialog(donateursListView.getSelectionModel().getSelectedItem()));
        deleteButton.setOnAction(e -> deleteDonateur(donateursListView.getSelectionModel().getSelectedItem()));

        donateursSection.getChildren().addAll(donateursLabel, donateursListView, crudButtons);

        root.setTop(navBar);
        root.setCenter(donateursSection);

        scene = new Scene(root, 800, 600);
        scene.getStylesheets().add("style.css");

        stage.setScene(scene);
    }

    public Scene getScene() {
        return scene;
    }

    private void loadDonateurs() {
        donateursList.clear();
        String url = "jdbc:mysql://localhost:3306/gestion_dons";
        String dbUser = "root";
        String dbPassword = "";

        try (Connection connection = DriverManager.getConnection(url, dbUser, dbPassword)) {
            String query = "SELECT * FROM donateurs";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String nom = resultSet.getString("nom");
                String email = resultSet.getString("email");
                donateursList.add(nom + " (" + email + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openAddDonateurDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Ajouter un Donateur");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nomField = new TextField();
        nomField.setPromptText("Nom");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        Button saveButton = new Button("Enregistrer");
        saveButton.setOnAction(e -> {
            addDonateur(nomField.getText(), emailField.getText());
            dialog.close();
        });

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(saveButton, 1, 2);

        Scene scene = new Scene(grid, 300, 200);
        dialog.setScene(scene);
        dialog.show();
    }

    private void addDonateur(String nom, String email) {
        String url = "jdbc:mysql://localhost:3306/gestion_dons";
        String dbUser = "root";
        String dbPassword = "";

        try (Connection connection = DriverManager.getConnection(url, dbUser, dbPassword)) {
            String query = "INSERT INTO donateurs (nom, email) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, nom);
            preparedStatement.setString(2, email);
            preparedStatement.executeUpdate();
            loadDonateurs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openUpdateDonateurDialog(String selectedDonateur) {
        if (selectedDonateur == null) {
            showAlert("Erreur", "Veuillez sélectionner un donateur à modifier.");
            return;
        }

        String nom = selectedDonateur.split(" \\(")[0];
        String email = selectedDonateur.split("\\(")[1].replace(")", "");

        Stage dialog = new Stage();
        dialog.setTitle("Modifier un Donateur");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nomField = new TextField(nom);
        TextField emailField = new TextField(email);

        Button saveButton = new Button("Enregistrer");
        saveButton.setOnAction(e -> {
            updateDonateur(nom, nomField.getText(), emailField.getText());
            dialog.close();
        });

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(saveButton, 1, 2);

        Scene scene = new Scene(grid, 300, 200);
        dialog.setScene(scene);
        dialog.show();
    }

    private void updateDonateur(String oldNom, String newNom, String newEmail) {
        String url = "jdbc:mysql://localhost:3306/gestion_dons";
        String dbUser = "root";
        String dbPassword = "";

        try (Connection connection = DriverManager.getConnection(url, dbUser, dbPassword)) {
            String query = "UPDATE donateurs SET nom = ?, email = ? WHERE nom = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, newNom);
            preparedStatement.setString(2, newEmail);
            preparedStatement.setString(3, oldNom);
            preparedStatement.executeUpdate();
            loadDonateurs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteDonateur(String selectedDonateur) {
        if (selectedDonateur == null) {
            showAlert("Erreur", "Veuillez sélectionner un donateur à supprimer.");
            return;
        }

        String nom = selectedDonateur.split(" \\(")[0];

        String url = "jdbc:mysql://localhost:3306/gestion_dons";
        String dbUser = "root";
        String dbPassword = "";

        try (Connection connection = DriverManager.getConnection(url, dbUser, dbPassword)) {
            String query = "DELETE FROM donateurs WHERE nom = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, nom);
            preparedStatement.executeUpdate();
            loadDonateurs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void show() {
    }
}
