package com.achraf.View;

import com.achraf.models.Admin;
import com.achraf.models.Don;
import com.achraf.models.Donateur;
import com.achraf.services.AdminService;
import com.achraf.services.DonService;
import com.achraf.services.DonateurService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

public class DashboardView {
    private Scene scene;
    private TableView<Admin> adminTable;
    private TableView<Don> donTable;
    private TableView<Donateur> donateurTable;
    private AdminService adminService;
    private DonService donService;
    private DonateurService donateurService;

    public DashboardView(Stage stage) {
        adminService = new AdminService();
        donService = new DonService();
        donateurService = new DonateurService();

        // Initialisation des tables
        adminTable = new TableView<>();
        donTable = new TableView<>();
        donateurTable = new TableView<>();

        createAdminTable();
        createDonTable();
        createDonateurTable();

        Button adminButton = new Button("Gérer les Admins");
        Button donButton = new Button("Gérer les Dons");
        Button donateurButton = new Button("Gérer les Donateurs");

        VBox sidebar = new VBox(10, adminButton, donButton, donateurButton);
        sidebar.setAlignment(Pos.CENTER);
        sidebar.setPrefWidth(200);

        BorderPane mainPane = new BorderPane();
        mainPane.setLeft(sidebar);
        mainPane.setCenter(adminTable); // Par défaut, afficher la table des admins

        scene = new Scene(mainPane, 800, 600);

        adminButton.setOnAction(e -> {
            refreshAdminTable();
            mainPane.setCenter(adminTable);
        });
        donButton.setOnAction(e -> {
            refreshDonTable();
            mainPane.setCenter(donTable);
        });
        donateurButton.setOnAction(e -> {
            refreshDonateurTable();
            mainPane.setCenter(donateurTable);
        });
    }

    public Scene getScene() {
        return scene;
    }

    private void createAdminTable() {
        TableColumn<Admin, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

        TableColumn<Admin, String> usernameColumn = new TableColumn<>("Nom d'utilisateur");
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());

        TableColumn<Admin, String> passwordColumn = new TableColumn<>("Mot de passe");
        passwordColumn.setCellValueFactory(cellData -> cellData.getValue().passwordProperty());

        TableColumn<Admin, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(col -> createActionCell(adminTable, "admin"));

        adminTable.getColumns().addAll(idColumn, usernameColumn, passwordColumn, actionColumn);
        refreshAdminTable();
    }

    private void createDonTable() {
        TableColumn<Don, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

        TableColumn<Don, Integer> donateurIdColumn = new TableColumn<>("Donateur ID");
        donateurIdColumn.setCellValueFactory(cellData -> cellData.getValue().donateurIdProperty().asObject());

        TableColumn<Don, Double> montantColumn = new TableColumn<>("Montant");
        montantColumn.setCellValueFactory(cellData -> cellData.getValue().montantProperty().asObject());

        TableColumn<Don, LocalDate> dateDonColumn = new TableColumn<>("Date de Don");
        dateDonColumn.setCellValueFactory(cellData -> cellData.getValue().dateDonProperty());

        TableColumn<Don, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(col -> createActionCell(donTable, "don"));

        donTable.getColumns().addAll(idColumn, donateurIdColumn, montantColumn, dateDonColumn, actionColumn);
        refreshDonTable();
    }

    private void createDonateurTable() {
        TableColumn<Donateur, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

        TableColumn<Donateur, String> nameColumn = new TableColumn<>("Nom");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        TableColumn<Donateur, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());

        TableColumn<Donateur, Double> montantDonneColumn = new TableColumn<>("Montant Donné");
        montantDonneColumn.setCellValueFactory(cellData -> cellData.getValue().montantDonneProperty().asObject());

        TableColumn<Donateur, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(col -> createActionCell(donateurTable, "donateur"));

        donateurTable.getColumns().addAll(idColumn, nameColumn, emailColumn, montantDonneColumn, actionColumn);
        refreshDonateurTable();
    }

    private <T> TableCell<T, Void> createActionCell(TableView<T> table, String type) {
        return new TableCell<>() {
            private final Button deleteButton = new Button("Supprimer");
            private final Button updateButton = new Button("Modifier");

            {
                deleteButton.setOnAction(e -> {
                    T item = getTableView().getItems().get(getIndex());
                    if ("admin".equals(type) && item instanceof Admin) {
                        adminService.deleteAdmin(((Admin) item).getId());
                        refreshAdminTable();
                    } else if ("don".equals(type) && item instanceof Don) {
                        donService.deleteDon(((Don) item).getId());
                        refreshDonTable();
                    } else if ("donateur".equals(type) && item instanceof Donateur) {
                        donateurService.deleteDonateur(((Donateur) item).getId());
                        refreshDonateurTable();
                    }
                });

                updateButton.setOnAction(e -> {
                    T item = getTableView().getItems().get(getIndex());
                    if ("admin".equals(type) && item instanceof Admin) {
                        showUpdateAdminDialog((Admin) item);
                    } else if ("don".equals(type) && item instanceof Don) {
                        showUpdateDonDialog((Don) item);
                    } else if ("donateur".equals(type) && item instanceof Donateur) {
                        showUpdateDonateurDialog((Donateur) item);
                    }
                });

                HBox pane = new HBox(deleteButton, updateButton);
                pane.setSpacing(10);
                setGraphic(pane);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(getGraphic());
                }
            }
        };
    }

    private void refreshAdminTable() {
        ObservableList<Admin> admins = FXCollections.observableArrayList(adminService.getAdmins());
        adminTable.setItems(admins);
    }

    private void refreshDonTable() {
        ObservableList<Don> dons = FXCollections.observableArrayList(donService.getDons());
        donTable.setItems(dons);
    }

    private void refreshDonateurTable() {
        ObservableList<Donateur> donateurs = FXCollections.observableArrayList(donateurService.getDonateurs());
        donateurTable.setItems(donateurs);
    }

    private void showAddAdminDialog() {
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        Button addButton = new Button("Ajouter");

        addButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = usernameField.getText();
            adminService.addAdmin(username, password);
            refreshAdminTable();
        });

        VBox vbox = new VBox(10, new Label("Nom d'utilisateur:"), usernameField, new Label("Mot de passe:"), passwordField, addButton);
        Scene dialogScene = new Scene(vbox, 300, 200);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Ajouter un Admin");
        dialogStage.setScene(dialogScene);
        dialogStage.show();
    }

    private void showUpdateAdminDialog(Admin admin) {
        TextField usernameField = new TextField(admin.getUsername());
        PasswordField passwordField = new PasswordField();
        passwordField.setText(admin.getPassword());
        Button updateButton = new Button("Modifier");

        updateButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            adminService.updateAdmin(admin.getId(), username, password);
            refreshAdminTable();
        });

        VBox vbox = new VBox(10, new Label("Nom d'utilisateur:"), usernameField, new Label("Mot de passe:"), passwordField, updateButton);
        Scene dialogScene = new Scene(vbox, 300, 200);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Modifier Admin");
        dialogStage.setScene(dialogScene);
        dialogStage.show();
    }

    private void showAddDonDialog() {
        TextField donateurIdField = new TextField();
        TextField montantField = new TextField();
        DatePicker dateDonPicker = new DatePicker();
        Button addButton = new Button("Ajouter");

        addButton.setOnAction(e -> {
            int donateurId = Integer.parseInt(donateurIdField.getText());
            double montant = Double.parseDouble(montantField.getText());
            LocalDate dateDon = dateDonPicker.getValue();
            donService.addDon(donateurId, montant, dateDon);
            refreshDonTable();
        });

        VBox vbox = new VBox(10, new Label("Donateur ID:"), donateurIdField, new Label("Montant:"), montantField, new Label("Date de Don:"), dateDonPicker, addButton);
        Scene dialogScene = new Scene(vbox, 300, 250);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Ajouter un Don");
        dialogStage.setScene(dialogScene);
        dialogStage.show();
    }

    private void showUpdateDonDialog(Don don) {
        TextField donateurIdField = new TextField(String.valueOf(don.getDonateurId()));
        TextField montantField = new TextField(String.valueOf(don.getMontant()));
        DatePicker dateDonPicker = new DatePicker(don.getDateDon());
        Button updateButton = new Button("Modifier");

        updateButton.setOnAction(e -> {
            int donateurId = Integer.parseInt(donateurIdField.getText());
            double montant = Double.parseDouble(montantField.getText());
            LocalDate dateDon = dateDonPicker.getValue();
            donService.updateDon(don.getId(), donateurId, montant, dateDon);
            refreshDonTable();
        });

        VBox vbox = new VBox(10, new Label("Donateur ID:"), donateurIdField, new Label("Montant:"), montantField, new Label("Date de Don:"), dateDonPicker, updateButton);
        Scene dialogScene = new Scene(vbox, 300, 250);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Modifier Don");
        dialogStage.setScene(dialogScene);
        dialogStage.show();
    }

    private void showAddDonateurDialog() {
        TextField nameField = new TextField();
        TextField emailField = new TextField();
        TextField montantDonneField = new TextField();
        Button addButton = new Button("Ajouter");

        addButton.setOnAction(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            double montantDonne = Double.parseDouble(montantDonneField.getText());
            donateurService.addDonateur(name, email, montantDonne);
            refreshDonateurTable();
        });

        VBox vbox = new VBox(10, new Label("Nom:"), nameField, new Label("Email:"), emailField, new Label("Montant Donné:"), montantDonneField, addButton);
        Scene dialogScene = new Scene(vbox, 300, 250);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Ajouter un Donateur");
        dialogStage.setScene(dialogScene);
        dialogStage.show();
    }

    private void showUpdateDonateurDialog(Donateur donateur) {
        TextField nameField = new TextField(donateur.getName());
        TextField emailField = new TextField(donateur.getEmail());
        TextField montantDonneField = new TextField(String.valueOf(donateur.getMontantDonne()));
        Button updateButton = new Button("Modifier");

        updateButton.setOnAction(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            double montantDonne = Double.parseDouble(montantDonneField.getText());
            donateurService.updateDonateur(donateur.getId(), name, email, montantDonne);
            refreshDonateurTable();
        });

        VBox vbox = new VBox(10, new Label("Nom:"), nameField, new Label("Email:"), emailField, new Label("Montant Donné:"), montantDonneField, updateButton);
        Scene dialogScene = new Scene(vbox, 300, 250);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Modifier Donateur");
        dialogStage.setScene(dialogScene);
        dialogStage.show();
    }
}
