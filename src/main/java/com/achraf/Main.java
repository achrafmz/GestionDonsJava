package com.achraf;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Création de la fenêtre de connexion
        primaryStage.setTitle("Authentification Administrateur");

        Label usernameLabel = new Label("Nom d'utilisateur:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Mot de passe:");
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Se connecter");

        // Layout de la page de connexion
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(usernameLabel, 0, 0);
        gridPane.add(usernameField, 1, 0);
        gridPane.add(passwordLabel, 0, 1);
        gridPane.add(passwordField, 1, 1);
        gridPane.add(loginButton, 1, 2);

        Scene scene = new Scene(gridPane, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Action sur le bouton de connexion
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (DBConnection.authenticateAdmin(username, password)) {
                // Authentification réussie : redirige vers le tableau de bord
                DashboardView dashboardView = new DashboardView(primaryStage);
                dashboardView.show();
            } else {
                // Affiche une erreur si l'authentification échoue
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Nom d'utilisateur ou mot de passe incorrect.");
                alert.showAndWait();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
