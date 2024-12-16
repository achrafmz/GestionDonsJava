package com.achraf;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminView {
    private Scene scene;

    public AdminView(Stage stage) {
        Button createButton = new Button("Créer un Admin");
        Button readButton = new Button("Liste des Admins");
        Button updateButton = new Button("Mettre à jour un Admin");
        Button deleteButton = new Button("Supprimer un Admin");

        VBox layout = new VBox(10, createButton, readButton, updateButton, deleteButton);
        layout.setAlignment(Pos.CENTER);

        scene = new Scene(layout, 800, 600);

        // Bouton retour au Dashboard
        createButton.setOnAction(e -> System.out.println("Créer un Admin"));
        readButton.setOnAction(e -> System.out.println("Lister les Admins"));
        updateButton.setOnAction(e -> System.out.println("Mettre à jour un Admin"));
        deleteButton.setOnAction(e -> System.out.println("Supprimer un Admin"));
    }

    public Scene getScene() {
        return scene;
    }
}
