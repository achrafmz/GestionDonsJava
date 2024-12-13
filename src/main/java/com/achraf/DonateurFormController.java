package com.achraf;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DonateurFormController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;

    @FXML
    private void handleAjouterDonateur() {
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String email = emailField.getText();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Champs incomplets");
            alert.setContentText("Veuillez remplir tous les champs.");
            alert.showAndWait();
        } else {
            // Insérer le donateur dans la base de données
            if (ajouterDonateur(nom, prenom, email)) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText("Donateur ajouté");
                alert.setContentText("Le donateur a été ajouté avec succès.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Erreur d'ajout");
                alert.setContentText("Il y a eu un problème lors de l'ajout du donateur.");
                alert.showAndWait();
            }
        }
    }

    private boolean ajouterDonateur(String nom, String prenom, String email) {
        String query = "INSERT INTO donateurs (nom, prenom, email) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, email);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
