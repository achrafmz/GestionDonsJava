package com.achraf;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginView {
    private Stage stage;

    public LoginView(Stage stage) {
        this.stage = stage;

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Nom d'utilisateur");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");

        Button loginButton = new Button("Se connecter");
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (authenticateAdmin(username, password)) {
                DashboardView dashboardView = new DashboardView(stage);
                stage.setScene(dashboardView.getScene());
            } else {
                showAlert("Erreur", "Nom d'utilisateur ou mot de passe incorrect.");
            }
        });

        grid.add(usernameField, 0, 0);
        grid.add(passwordField, 0, 1);
        grid.add(loginButton, 0, 2);

        Scene scene = new Scene(grid, 400, 200);
        stage.setScene(scene);
    }

    private boolean authenticateAdmin(String username, String password) {
        String url = "jdbc:mysql://localhost:3306/gestion_dons";
        String dbUser = "root";
        String dbPassword = ""; // Remplace par ton mot de passe si nécessaire

        try (Connection connection = DriverManager.getConnection(url, dbUser, dbPassword)) {
            String query = "SELECT * FROM admins WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
