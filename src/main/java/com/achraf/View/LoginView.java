package com.achraf.View;

import com.achraf.services.AdminService;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView {
    private Scene scene;
    private AdminService adminService;

    public LoginView(Stage stage) {
        adminService = new AdminService();

        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (adminService.authenticate(username, password)) {
                DashboardView dashboardView = new DashboardView(stage);
                stage.setScene(dashboardView.getScene());
            } else {
                System.out.println("Invalid credentials");
            }
        });

        VBox vbox = new VBox(10, new Label("Username:"), usernameField, new Label("Password:"), passwordField, loginButton);
        vbox.setAlignment(Pos.CENTER);
        scene = new Scene(vbox, 300, 200);
    }

    public Scene getScene() {
        return scene;
    }
}
