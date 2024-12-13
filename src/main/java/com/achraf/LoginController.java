package com.achraf;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Authentifier l'administrateur
        if (DBConnection.authenticateAdmin(username, password)) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText("Authentification réussie");
            alert.setContentText("Bienvenue, administrateur !");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Authentification échouée");
            alert.setContentText("Nom d'utilisateur ou mot de passe incorrect.");
            alert.showAndWait();
        }
    }
}
