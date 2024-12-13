package com.achraf;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DonorCrudView {

    private final Stage stage;

    public DonorCrudView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        // Titre de la gestion des donateurs
        stage.setTitle("Gestion des donateurs");

        // Liste des donateurs (simulée ici)
        Label donorListLabel = new Label("Liste des donateurs:");
        Button addDonorButton = new Button("Ajouter un donateur");
        Button deleteDonorButton = new Button("Supprimer un donateur");
        Button listDonorsButton = new Button("Afficher la liste des donateurs");

        // Actions des boutons
        addDonorButton.setOnAction(e -> addDonor());
        deleteDonorButton.setOnAction(e -> deleteDonor());
        listDonorsButton.setOnAction(e -> listDonors());

        // Layout
        VBox vBox = new VBox(10, donorListLabel, addDonorButton, deleteDonorButton, listDonorsButton);
        Scene scene = new Scene(vBox, 400, 300);
        stage.setScene(scene); // Met à jour la scène avec la gestion des donateurs
        stage.show();
    }

    // Ajouter un donateur
    private void addDonor() {
        System.out.println("Ajout d'un donateur...");
        // Ajouter un donateur dans la base de données ici
    }

    // Supprimer un donateur
    private void deleteDonor() {
        System.out.println("Suppression d'un donateur...");
        // Supprimer un donateur de la base de données ici
    }

    // Afficher la liste des donateurs
    private void listDonors() {
        System.out.println("Liste des donateurs...");
        // Afficher la liste des donateurs depuis la base de données ici
    }
}
