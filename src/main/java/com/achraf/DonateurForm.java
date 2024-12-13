package com.achraf;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class DonateurForm extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Créer les éléments de l'interface graphique
        Label nomLabel = new Label("Nom:");
        TextField nomField = new TextField();

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();

        Label montantLabel = new Label("Montant du Don:");
        TextField montantField = new TextField();

        Button ajouterButton = new Button("Ajouter Donateur");

        // Créer un gestionnaire de disposition (layout)
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.add(nomLabel, 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(emailLabel, 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(montantLabel, 0, 2);
        grid.add(montantField, 1, 2);
        grid.add(ajouterButton, 1, 3);

        // Action au clic sur le bouton
        ajouterButton.setOnAction(e -> {
            String nom = nomField.getText();
            String email = emailField.getText();
            double montant = 0;

            try {
                montant = Double.parseDouble(montantField.getText());
            } catch (NumberFormatException ex) {
                // Si le montant n'est pas un nombre valide, afficher un message d'erreur
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Le montant doit être un nombre valide.");
                alert.showAndWait();
                return;
            }

            // Ajouter le donateur à la base de données
            Donateur donateur = new Donateur(nom, email, montant);
            DonateurDAO donateurDAO = new DonateurDAO();
            donateurDAO.ajouterDonateur(donateur);

            // Vider les champs après l'ajout
            nomField.clear();
            emailField.clear();
            montantField.clear();

            // Afficher un message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ajout Réussi");
            alert.setHeaderText(null);
            alert.setContentText("Donateur ajouté avec succès!");
            alert.showAndWait();
        });

        // Créer la scène et l'afficher
        Scene scene = new Scene(grid, 400, 250);
        primaryStage.setTitle("Ajouter un Donateur");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
