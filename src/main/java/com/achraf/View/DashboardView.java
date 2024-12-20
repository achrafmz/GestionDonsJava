package com.achraf.View;

import com.achraf.models.Admin;
import com.achraf.models.Don;
import com.achraf.models.Donateur;
import com.achraf.services.AdminService;
import com.achraf.services.DonService;
import com.achraf.services.DonateurService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDate;

public class DashboardView {
    private Scene scene;
    private BorderPane mainPane;
    private VBox sidebar;
    private HBox cardBox;
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

        adminTable = new TableView<>();
        donTable = new TableView<>();
        donateurTable = new TableView<>();

        createAdminTable();
        createDonTable();
        createDonateurTable();

        Button adminButton = new Button("Gérer les Admins");
        Button donButton = new Button("Gérer les Dons");
        Button donateurButton = new Button("Gérer les Donateurs");

        // Boutons d'ajout
        Button addAdminButton = new Button("Ajouter un Admin");
        addAdminButton.setVisible(false);
        Button addDonButton = new Button("Ajouter un Don");
        addDonButton.setVisible(false);
        Button addDonateurButton = new Button("Ajouter un Donateur");
        addDonateurButton.setVisible(false);

        // Actions des boutons d'ajout
        addAdminButton.setOnAction(e -> showAddAdminDialog());
        addDonButton.setOnAction(e -> showAddDonDialog());
        addDonateurButton.setOnAction(e -> showAddDonateurDialog());

        // Boutons de navigation
        Button dashboardButton = new Button("Tableau de Bord");
        Button logoutButton = new Button("Déconnecter");

        dashboardButton.setOnAction(e -> mainPane.setCenter(cardBox));
        logoutButton.setOnAction(e -> handleLogout(stage));

        sidebar = new VBox(10, dashboardButton, adminButton, addAdminButton, donButton, addDonButton, donateurButton, addDonateurButton, logoutButton);
        sidebar.setAlignment(Pos.CENTER);
        sidebar.setPrefWidth(200);
        sidebar.setPadding(new Insets(10));
        sidebar.setStyle("-fx-background-color: #2f4f4f; -fx-text-fill: white;");

        // Cards
        cardBox = new HBox(20);
        cardBox.setAlignment(Pos.CENTER);
        cardBox.setPadding(new Insets(20));
        createDashboardCards();

        mainPane = new BorderPane();
        mainPane.setLeft(sidebar);
        mainPane.setCenter(cardBox); // Par défaut, afficher les cards

        scene = new Scene(mainPane, 1000, 600);

        adminButton.setOnAction(e -> {
            refreshAdminTable();
            mainPane.setCenter(adminTable);
            addAdminButton.setVisible(true);
            addDonButton.setVisible(false);
            addDonateurButton.setVisible(false);
        });
        donButton.setOnAction(e -> {
            refreshDonTable();
            mainPane.setCenter(donTable);
            addAdminButton.setVisible(false);
            addDonButton.setVisible(true);
            addDonateurButton.setVisible(false);
        });
        donateurButton.setOnAction(e -> {
            refreshDonateurTable();
            mainPane.setCenter(donateurTable);
            addAdminButton.setVisible(false);
            addDonButton.setVisible(false);
            addDonateurButton.setVisible(true);
        });

        stage.setScene(scene);
        stage.show();
    }

    public Scene getScene() {
        return scene;
    }

    private void createDashboardCards() {
        Label donateursCount = new Label("Nombre de Donateurs: " + donateurService.getDonateurs().size());
        donateursCount.setStyle("-fx-font-size: 24; -fx-text-fill: #ffffff;");
        VBox donateurCard = new VBox(donateursCount);
        donateurCard.setAlignment(Pos.CENTER);
        donateurCard.setPadding(new Insets(20));
        donateurCard.setStyle("-fx-background-color: #4682b4; -fx-background-radius: 10; -fx-padding: 20;");

        double totalDons = donService.getDons().stream().mapToDouble(Don::getMontant).sum();
        Label donsSum = new Label("Montant Total des Dons: " + totalDons);
        donsSum.setStyle("-fx-font-size: 24; -fx-text-fill: #ffffff;");
        VBox donsCard = new VBox(donsSum);
        donsCard.setAlignment(Pos.CENTER);
        donsCard.setPadding(new Insets(20));
        donsCard.setStyle("-fx-background-color: #32cd32; -fx-background-radius: 10; -fx-padding: 20;");

        cardBox.getChildren().addAll(donateurCard, donsCard);
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

        TableColumn<Donateur, String> nomColumn = new TableColumn<>("Nom");
        nomColumn.setCellValueFactory(cellData -> cellData.getValue().nomProperty());

        TableColumn<Donateur, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());

        TableColumn<Donateur, Double> montantDonneColumn = new TableColumn<>("Montant Donné");
        montantDonneColumn.setCellValueFactory(cellData -> cellData.getValue().montantDonneProperty().asObject());

        TableColumn<Donateur, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(col -> createActionCell(donateurTable, "donateur"));

        donateurTable.getColumns().addAll(idColumn, nomColumn, emailColumn, montantDonneColumn, actionColumn);
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
            String password = passwordField.getText();
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
        ComboBox<Donateur> donateurComboBox = new ComboBox<>();
        donateurComboBox.setItems(FXCollections.observableArrayList(donateurService.getDonateurs()));
        donateurComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Donateur donateur) {
                return donateur != null ? donateur.getNom() + " (" + donateur.getId() + ")" : "";
            }

            @Override
            public Donateur fromString(String string) {
                return null;
            }
        });

        TextField montantField = new TextField();
        DatePicker dateDonPicker = new DatePicker();
        Button addButton = new Button("Ajouter");

        addButton.setOnAction(e -> {
            try {
                Donateur selectedDonateur = donateurComboBox.getValue();
                if (selectedDonateur == null) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner un donateur.");
                    return;
                }

                double montant = Double.parseDouble(montantField.getText().trim());
                LocalDate dateDon = dateDonPicker.getValue();

                if (dateDon == null) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "La date de don est obligatoire.");
                    return;
                }

                donService.addDon(selectedDonateur.getId(), montant, dateDon);
                refreshDonTable();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer une valeur valide pour le montant.");
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur de la base de données", ex.getMessage());
            }
        });

        VBox vbox = new VBox(10, new Label("Donateur:"), donateurComboBox, new Label("Montant:"), montantField, new Label("Date de Don:"), dateDonPicker, addButton);
        Scene dialogScene = new Scene(vbox, 300, 250);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Ajouter un Don");
        dialogStage.setScene(dialogScene);
        dialogStage.show();
    }

    private void showUpdateDonDialog(Don don) {
        ComboBox<Donateur> donateurComboBox = new ComboBox<>();
        donateurComboBox.setItems(FXCollections.observableArrayList(donateurService.getDonateurs()));
        donateurComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Donateur donateur) {
                return donateur != null ? donateur.getNom() + " (" + donateur.getId() + ")" : "";
            }

            @Override
            public Donateur fromString(String string) {
                return null;
            }
        });
        donateurComboBox.getSelectionModel().select(donateurService.getDonateurById(don.getDonateurId()));

        TextField montantField = new TextField(String.valueOf(don.getMontant()));
        DatePicker dateDonPicker = new DatePicker(don.getDateDon());
        Button updateButton = new Button("Modifier");

        updateButton.setOnAction(e -> {
            try {
                Donateur selectedDonateur = donateurComboBox.getValue();
                if (selectedDonateur == null) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner un donateur.");
                    return;
                }

                double montant = Double.parseDouble(montantField.getText().trim());
                LocalDate dateDon = dateDonPicker.getValue();

                if (dateDon == null) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "La date de don est obligatoire.");
                    return;
                }

                donService.updateDon(don.getId(), selectedDonateur.getId(), montant, dateDon);
                refreshDonTable();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer une valeur valide pour le montant.");
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur de la base de données", ex.getMessage());
            }
        });

        VBox vbox = new VBox(10, new Label("Donateur:"), donateurComboBox, new Label("Montant:"), montantField, new Label("Date de Don:"), dateDonPicker, updateButton);
        Scene dialogScene = new Scene(vbox, 300, 250);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Modifier Don");
        dialogStage.setScene(dialogScene);
        dialogStage.show();
    }

    private void showAddDonateurDialog() {
        TextField nomField = new TextField();
        TextField emailField = new TextField();
        TextField montantDonneField = new TextField();
        Button addButton = new Button("Ajouter");

        addButton.setOnAction(e -> {
            try {
                String nom = nomField.getText().trim();
                String email = emailField.getText().trim();
                double montantDonne = Double.parseDouble(montantDonneField.getText().trim());

                if (nom.isEmpty() || email.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs obligatoires.");
                    return;
                }

                donateurService.addDonateur(nom, email, montantDonne);
                refreshDonateurTable();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer une valeur valide pour le montant.");
            }
        });

        VBox vbox = new VBox(10, new Label("Nom:"), nomField, new Label("Email:"), emailField, new Label("Montant Donné:"), montantDonneField, addButton);
        Scene dialogScene = new Scene(vbox, 300, 250);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Ajouter un Donateur");
        dialogStage.setScene(dialogScene);
        dialogStage.show();
    }

    private void showUpdateDonateurDialog(Donateur donateur) {
        TextField nomField = new TextField(donateur.getNom());
        TextField emailField = new TextField(donateur.getEmail());
        TextField montantDonneField = new TextField(String.valueOf(donateur.getMontantDonne()));
        Button updateButton = new Button("Modifier");

        updateButton.setOnAction(e -> {
            try {
                String nom = nomField.getText().trim();
                String email = emailField.getText().trim();
                double montantDonne = Double.parseDouble(montantDonneField.getText().trim());

                if (nom.isEmpty() || email.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs obligatoires.");
                    return;
                }

                donateurService.updateDonateur(donateur.getId(), nom, email, montantDonne);
                refreshDonateurTable();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer une valeur valide pour le montant.");
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur de la base de données", ex.getMessage());
            }
        });

        VBox vbox = new VBox(10, new Label("Nom:"), nomField, new Label("Email:"), emailField, new Label("Montant Donné:"), montantDonneField, updateButton);
        Scene dialogScene = new Scene(vbox, 300, 250);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Modifier Donateur");
        dialogStage.setScene(dialogScene);
        dialogStage.show();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleLogout(Stage stage) {
        LoginView loginView = new LoginView(stage);
        stage.setScene(loginView.getScene());
    }
}
