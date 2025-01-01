package com.achraf.View;

import com.achraf.services.AdminService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.SQLException;

public class LoginView {
    private Scene scene;
    private AdminService adminService;

    public LoginView(Stage stage) {
        adminService = new AdminService();

        // Créer les éléments de l'interface utilisateur
        Label titleLabel = new Label("Bienvenue au association NAJD");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        titleLabel.setTextFill(Color.web("#27ae60"));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Nom d'utilisateur");
        usernameField.setStyle("-fx-font-size: 18px; -fx-padding: 10px; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #27ae60;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        passwordField.setStyle("-fx-font-size: 18px; -fx-padding: 10px; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #27ae60;");

        Button loginButton = new Button("Connexion");
        loginButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 10px 20px; -fx-background-radius: 20;");
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (adminService.authenticate(username, password)) {
                DashboardView dashboardView = null;
                try {
                    dashboardView = new DashboardView(stage);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                stage.setScene(dashboardView.getScene());
            } else {
                showAlert("Informations d'identification invalides");
            }
        });

        // Ajouter des effets d'ombre
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.GRAY);
        shadow.setRadius(5);
        usernameField.setEffect(shadow);
        passwordField.setEffect(shadow);
        loginButton.setEffect(shadow);

        // Créer une boîte verticale pour contenir les champs
        VBox vbox = new VBox(20, titleLabel, usernameField, passwordField, loginButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(40));
        vbox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dcdcdc; -fx-border-width: 2px; -fx-border-radius: 20px; -fx-background-radius: 20px;");

        // Créer une boîte pour centrer le formulaire de connexion
        VBox root = new VBox(vbox);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #e8f5e9; -fx-padding: 50px;");

        // Configurer la scène et ajouter des actions
        scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Scene getScene() {
        return scene;
    }
}
