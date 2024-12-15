import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {
    @FXML
    private StackPane contentPane;

    @FXML
    private void handleAdminManagement(ActionEvent event) {
        loadPage("admin_management.fxml");
    }

    @FXML
    private void handleDonationManagement(ActionEvent event) {
        loadPage("donation_management.fxml");
    }

    @FXML
    private void handleDonorManagement(ActionEvent event) {
        loadPage("donor_management.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPage(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            contentPane.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
