package com.achraf;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DonView {
    private Scene scene;

    public DonView(Stage stage) {
        Button createButton = new Button("Créer un Don");
        Button readButton = new Button("Liste des Dons");
        Button updateButton = new Button("Mettre à jour un Don");
        Button deleteButton = new Button("Supprimer un Don");

        VBox layout = new VBox(10, createButton, readButton, updateButton, deleteButton);
        layout.setAlignment(Pos.CENTER);

        scene = new Scene(layout, 800, 600);

        // Actions des boutons
        createButton.setOnAction(e -> System.out.println("Créer un Don"));
        readButton.setOnAction(e -> System.out.println("Lister les Dons"));
        updateButton.setOnAction(e -> System.out.println("Mettre à jour un Don"));
        deleteButton.setOnAction(e -> System.out.println("Supprimer un Don"));
    }

    public Scene getScene() {
        return scene;
    }
}
