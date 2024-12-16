package com.achraf;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.achraf.View.LoginView;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        LoginView loginView = new LoginView(primaryStage);
        primaryStage.setScene(loginView.getScene());
        primaryStage.setTitle("Dashboard Application");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
