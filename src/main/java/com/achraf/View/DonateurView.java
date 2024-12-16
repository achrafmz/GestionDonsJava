package com.achraf;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DonateurView {
    private Scene scene;

    public DonateurView(Stage stage) {
        Button createButton = new Button("Créer un Donateur");
        Button readButton = new Button("Liste des Donateurs");
        Button updateButton = new Button("Mettre à jour un Donateur");
        Button deleteButton = new Button("Supprimer un Donateur");

        VBox layout = new VBox(10, createButton, readButton, updateButton, deleteButton);
        layout.setAlignment(Pos.CENTER);

        scene = new Scene(layout, 800, 600);

        // Actions des boutons
        createButton.setOnAction(e -> System.out.println("Créer un Donateur"));
        readButton.setOnAction(e -> System.out.println("Lister les Donateurs"));
        updateButton.setOnAction(e -> System.out.println("Mettre à jour un Donateur"));
        deleteButton.setOnAction(e -> System.out.println("Supprimer un Donateur"));
    }

    public Scene getScene() {
        return scene;
    }
}
