package com.achraf.View;

import com.achraf.models.*;
import com.achraf.services.AdminService;
import com.achraf.services.BeneficiaireService;
import com.achraf.services.DonBeneficiaireService;
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


import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DashboardView {
    private Scene scene;
    private BorderPane mainPane;
    private VBox sidebar;
    private HBox cardBox;
    private TableView<Admin> adminTable;
    private TableView<Don> donTable;
    private TableView<Donateur> donateurTable;
    private TableView<Beneficiaire> beneficiaireTable;
    private TableView<HistoriqueDon> historiqueTable;
    private AdminService adminService;
    private DonService donService;
    private DonateurService donateurService;
    private BeneficiaireService beneficiaireService;
    private DonBeneficiaireService donBeneficiaireService;

    public DashboardView(Stage stage) throws SQLException {
        adminService = new AdminService();
        donService = new DonService();
        donateurService = new DonateurService();
        beneficiaireService = new BeneficiaireService();
        donBeneficiaireService = new DonBeneficiaireService();

        adminTable = new TableView<>();
        donTable = new TableView<>();
        donateurTable = new TableView<>();
        beneficiaireTable = new TableView<>();
        createHistoriqueTable();  // Ajout de la table Historique

        createAdminTable();
        createDonTable();
        createDonateurTable();
        createBeneficiaireTable();

        Button adminButton = new Button("Gérer les Admins");
        Button donButton = new Button("Gérer les Dons");
        Button donateurButton = new Button("Gérer les Donateurs");
        Button beneficiaireButton = new Button("Gérer les Bénéficiaires");
        Button historiqueButton = new Button("Voir l'Historique des Dons");  // Bouton pour voir l'historique

        // Boutons d'ajout
        Button addAdminButton = new Button("Ajouter un Admin");
        addAdminButton.setVisible(false);
        Button addDonButton = new Button("Ajouter un Don");
        addDonButton.setVisible(false);
        Button addDonateurButton = new Button("Ajouter un Donateur");
        addDonateurButton.setVisible(false);
        Button addBeneficiaireButton = new Button("Ajouter un Bénéficiaire");
        addBeneficiaireButton.setVisible(false);

        // Actions des boutons d'ajout
        addAdminButton.setOnAction(e -> showAddAdminDialog());
        addDonButton.setOnAction(e -> showAddDonDialog());
        addDonateurButton.setOnAction(e -> showAddDonateurDialog());
        addBeneficiaireButton.setOnAction(e -> showAddBeneficiaireDialog());

        // Boutons de navigation
        Button dashboardButton = new Button("Tableau de Bord");
        Button logoutButton = new Button("Déconnecter");

        dashboardButton.setOnAction(e -> mainPane.setCenter(cardBox));
        logoutButton.setOnAction(e -> handleLogout(stage));
        historiqueButton.setOnAction(e -> {
            refreshHistoriqueTable();
            mainPane.setCenter(historiqueTable);
        });

        sidebar = new VBox(10, dashboardButton, adminButton, addAdminButton, donButton, addDonButton, donateurButton, addDonateurButton, beneficiaireButton, addBeneficiaireButton, historiqueButton, logoutButton);
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
            addBeneficiaireButton.setVisible(false);
        });
        donButton.setOnAction(e -> {
            refreshDonTable();
            mainPane.setCenter(donTable);
            addAdminButton.setVisible(false);
            addDonButton.setVisible(true);
            addDonateurButton.setVisible(false);
            addBeneficiaireButton.setVisible(false);
        });
        donateurButton.setOnAction(e -> {
            refreshDonateurTable();
            mainPane.setCenter(donateurTable);
            addAdminButton.setVisible(false);
            addDonButton.setVisible(false);
            addDonateurButton.setVisible(true);
            addBeneficiaireButton.setVisible(false);
        });
        beneficiaireButton.setOnAction(e -> {
            refreshBeneficiaireTable();
            mainPane.setCenter(beneficiaireTable);
            addAdminButton.setVisible(false);
            addDonButton.setVisible(false);
            addDonateurButton.setVisible(false);
            addBeneficiaireButton.setVisible(true);
        });

        stage.setScene(scene);
        stage.show();
    }

    public Scene getScene() {
        return scene;
    }

    private void createDashboardCards() throws SQLException {
        Label donateursCount = new Label("Nombre de Donateurs: " + donateurService.getDonateurs().size());
        donateursCount.setStyle("-fx-font-size: 24; -fx-text-fill: #ffffff;");
        VBox donateurCard = new VBox(donateursCount);
        donateurCard.setAlignment(Pos.CENTER);
        donateurCard.setPadding(new Insets(20));
        donateurCard.setStyle("-fx-background-color: #4682b4; -fx-background-radius: 10; -fx-padding: 20;");

        double totalDons = donService.getTotalDons();
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
        actionColumn.setCellFactory(col -> (TableCell<Admin, Void>) createActionCell(adminTable, "admin"));

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
        actionColumn.setCellFactory(col -> (TableCell<Don, Void>) createActionCell(donTable, "don"));

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
        actionColumn.setCellFactory(col -> (TableCell<Donateur, Void>) createActionCell(donateurTable, "donateur"));

        donateurTable.getColumns().addAll(idColumn, nomColumn, emailColumn, montantDonneColumn, actionColumn);
        refreshDonateurTable();
    }

    private void createBeneficiaireTable() {
        TableColumn<Beneficiaire, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

        TableColumn<Beneficiaire, String> nomColumn = new TableColumn<>("Nom");
        nomColumn.setCellValueFactory(cellData -> cellData.getValue().nomProperty());

        TableColumn<Beneficiaire, String> prenomColumn = new TableColumn<>("Prénom");
        prenomColumn.setCellValueFactory(cellData -> cellData.getValue().prenomProperty());

        TableColumn<Beneficiaire, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());

        TableColumn<Beneficiaire, String> telephoneColumn = new TableColumn<>("Téléphone");
        telephoneColumn.setCellValueFactory(cellData -> cellData.getValue().telephoneProperty());

        TableColumn<Beneficiaire, String> adresseColumn = new TableColumn<>("Adresse");
        adresseColumn.setCellValueFactory(cellData -> cellData.getValue().adresseProperty());

        TableColumn<Beneficiaire, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(col -> new TableCell<Beneficiaire, Void>() {
            private final Button attribuerButton = new Button("Attribuer un Don");
            private final Button deleteButton = new Button("Supprimer");
            private final Button updateButton = new Button("Modifier");

            {
                attribuerButton.setOnAction(event -> handleAttribuerDon(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(event -> {
                    Beneficiaire beneficiaire = getTableView().getItems().get(getIndex());
                    deleteBeneficiaire(beneficiaire);
                });
                updateButton.setOnAction(event -> {
                    Beneficiaire beneficiaire = getTableView().getItems().get(getIndex());
                    showUpdateBeneficiaireDialog(beneficiaire);
                });

                // Style des boutons
                attribuerButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                updateButton.setStyle("-fx-background-color: orange; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    VBox box = new VBox(5, attribuerButton, deleteButton, updateButton);
                    setGraphic(box);
                }
            }
        });

        beneficiaireTable.getColumns().addAll(idColumn, nomColumn, prenomColumn, emailColumn, telephoneColumn, adresseColumn, actionColumn);
        refreshBeneficiaireTable();
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

    private void refreshBeneficiaireTable() {
        ObservableList<Beneficiaire> beneficiaires = FXCollections.observableArrayList(beneficiaireService.getBeneficiaires());
        beneficiaireTable.setItems(beneficiaires);
    }

    private void refreshHistoriqueTable() {
        ObservableList<HistoriqueDon> historiqueDons = FXCollections.observableArrayList(donBeneficiaireService.getHistoriqueDons());
        historiqueTable.setItems(historiqueDons);
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

    private void showAddBeneficiaireDialog() {
        TextField nomField = new TextField();
        TextField prenomField = new TextField();
        TextField emailField = new TextField();
        TextField telephoneField = new TextField();
        TextField adresseField = new TextField();
        Button addButton = new Button("Ajouter");

        addButton.setOnAction(e -> {
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            String email = emailField.getText().trim();
            String telephone = telephoneField.getText().trim();
            String adresse = adresseField.getText().trim();

            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || telephone.isEmpty() || adresse.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs obligatoires.");
                return;
            }

            beneficiaireService.addBeneficiaire(nom, prenom, email, telephone, adresse);
            refreshBeneficiaireTable();
        });

        VBox vbox = new VBox(10, new Label("Nom:"), nomField, new Label("Prénom:"), prenomField, new Label("Email:"), emailField, new Label("Téléphone:"), telephoneField, new Label("Adresse:"), adresseField, addButton);
        Scene dialogScene = new Scene(vbox, 300, 300);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Ajouter un Bénéficiaire");
        dialogStage.setScene(dialogScene);
        dialogStage.show();
    }

    private void showUpdateBeneficiaireDialog(Beneficiaire beneficiaire) {
        TextField nomField = new TextField(beneficiaire.getNom());
        TextField prenomField = new TextField(beneficiaire.getPrenom());
        TextField emailField = new TextField(beneficiaire.getEmail());
        TextField telephoneField = new TextField(beneficiaire.getTelephone());
        TextField adresseField = new TextField(beneficiaire.getAdresse());
        Button updateButton = new Button("Modifier");

        updateButton.setOnAction(e -> {
            String nom = nomField.getText();
            String prenom = prenomField.getText();
            String email = emailField.getText();
            String telephone = telephoneField.getText();
            String adresse = adresseField.getText();

            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || telephone.isEmpty() || adresse.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs obligatoires.");
                return;
            }

            beneficiaireService.updateBeneficiaire(new Beneficiaire(beneficiaire.getId(), nom, prenom, email, telephone, adresse));
            refreshBeneficiaireTable();
        });

        VBox vbox = new VBox(10, new Label("Nom:"), nomField, new Label("Prénom:"), prenomField, new Label("Email:"), emailField, new Label("Téléphone:"), telephoneField, new Label("Adresse:"), adresseField, updateButton);
        Scene dialogScene = new Scene(vbox, 300, 300);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Modifier Bénéficiaire");
        dialogStage.setScene(dialogScene);
        dialogStage.show();
    }

    private void deleteBeneficiaire(Beneficiaire beneficiaire) {
        beneficiaireService.deleteBeneficiaire(beneficiaire.getId());
        refreshBeneficiaireTable();
    }

    private void handleAttribuerDon(Beneficiaire beneficiaire) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Attribuer un Don");
        dialog.setHeaderText("Attribuer un Don à " + beneficiaire.getNom());
        dialog.setContentText("Montant du Don:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            double montant = Double.parseDouble(result.get());
            try {
                double totalDons = donService.getTotalDons();
                if (totalDons >= montant) {
                    System.out.println("Total des dons suffisant pour attribuer le don.");
                    // Ajouter le don uniquement dans l'historique
                    int donId = 1; // ID fictif pour le don (n'est plus utilisé pour `dons`)

                    donBeneficiaireService.attribuerDon(donId, beneficiaire.getId(), montant, LocalDate.now());
                    donBeneficiaireService.enregistrerHistoriqueDon(donId, beneficiaire.getId(), montant, LocalDate.now());

                    // Mise à jour du total des dons
                    donService.updateTotalDons(totalDons - montant);

                    System.out.println("Mise à jour du total des dons après attribution.");

                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Don attribué avec succès et enregistré dans l'historique des dons!", ButtonType.OK);
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Fonds insuffisants pour attribuer ce don.", ButtonType.OK);
                    alert.showAndWait();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'attribution du don.", ButtonType.OK);
                alert.showAndWait();
            }
        }
    }











    private int choisirDonateur() throws SQLException {
        // Recherchez un donateur existant et retournez son ID.
        List<Donateur> donateurs = donateurService.getDonateurs();
        if (donateurs.isEmpty()) {
            throw new SQLException("Aucun donateur disponible.");
        }
        return donateurs.get(0).getId(); // Retourne l'ID du premier donateur trouvé.
    }

    private void handleLogout(Stage stage) {
        LoginView loginView = new LoginView(stage);
        stage.setScene(loginView.getScene());
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private TableCell<?, Void> createActionCell(TableView<?> table, String type) {
        return new TableCell<>() {
            private final Button attribuerButton = new Button("Attribuer un Don");
            private final Button deleteButton = new Button("Supprimer");
            private final Button updateButton = new Button("Modifier");

            {
                attribuerButton.setOnAction(event -> {
                    if (type.equals("beneficiaire")) {
                        Beneficiaire beneficiaire = (Beneficiaire) getTableView().getItems().get(getIndex());
                        handleAttribuerDon(beneficiaire);
                    }
                });

                deleteButton.setOnAction(event -> {
                    if (type.equals("beneficiaire")) {
                        Beneficiaire beneficiaire = (Beneficiaire) getTableView().getItems().get(getIndex());
                        deleteBeneficiaire(beneficiaire);
                    }
                });

                updateButton.setOnAction(event -> {
                    if (type.equals("beneficiaire")) {
                        Beneficiaire beneficiaire = (Beneficiaire) getTableView().getItems().get(getIndex());
                        showUpdateBeneficiaireDialog(beneficiaire);
                    }
                });

                attribuerButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                updateButton.setStyle("-fx-background-color: orange; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    VBox box = new VBox(5, attribuerButton, deleteButton, updateButton);
                    setGraphic(box);
                }
            }
        };
    }



    private void createHistoriqueTable() {
        historiqueTable = new TableView<>();

        TableColumn<HistoriqueDon, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

        TableColumn<HistoriqueDon, Integer> donIdColumn = new TableColumn<>("Don ID");
        donIdColumn.setCellValueFactory(cellData -> cellData.getValue().donIdProperty().asObject());

        TableColumn<HistoriqueDon, String> beneficiaireNomColumn = new TableColumn<>("Bénéficiaire");
        beneficiaireNomColumn.setCellValueFactory(cellData -> cellData.getValue().beneficiaireNomProperty());

        TableColumn<HistoriqueDon, LocalDate> dateAttributionColumn = new TableColumn<>("Date d'Attribution");
        dateAttributionColumn.setCellValueFactory(cellData -> cellData.getValue().dateAttributionProperty());

        TableColumn<HistoriqueDon, Double> montantColumn = new TableColumn<>("Montant");
        montantColumn.setCellValueFactory(cellData -> cellData.getValue().montantProperty().asObject());

        TableColumn<HistoriqueDon, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button viewReportButton = new Button("Voir/Télécharger Rapport");

            {
                viewReportButton.setOnAction(event -> {
                    HistoriqueDon historiqueDon = getTableView().getItems().get(getIndex());
                    generateOperationReport(historiqueDon);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewReportButton);
                }
            }
        });

        historiqueTable.getColumns().addAll(idColumn, donIdColumn, beneficiaireNomColumn, dateAttributionColumn, montantColumn, actionColumn);
    }

    private void generateOperationReport(HistoriqueDon historiqueDon) {
        System.out.println("Début de la génération du rapport pour l'ID du don: " + historiqueDon.getDonId());

        // Création du document Word
        XWPFDocument document = new XWPFDocument();

        // Ajout de l'en-tête du rapport
        XWPFParagraph header = document.createParagraph();
        header.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = header.createRun();
        run.setText("Association NAJD");
        run.setBold(true);
        run.setFontSize(16);
        run.addBreak();

        run = header.createRun();
        run.setText("Adresse :  Rue ZERKTOUNI , Marrakech, Maroc");
        run.setFontSize(12);
        run.addBreak();
        run = header.createRun();
        run.setText("0655554443");
        run.setFontSize(12);
        run.addBreak();
        run.addBreak();

        // Ajout du titre du rapport
        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        run = title.createRun();
        run.setText("Rapport de l'Opération de Don");
        run.setBold(true);
        run.setFontSize(14);
        run.addBreak();
        run.addBreak();

        // Ajout des détails de l'opération de don
        XWPFParagraph paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setText("Détails de l'Opération:");
        run.setBold(true);
        run.setFontSize(12);
        run.addBreak();

        run = paragraph.createRun();
        run.setText("ID du Don: " + historiqueDon.getDonId());
        run.addBreak();

        run.setText("Bénéficiaire: " + historiqueDon.getBeneficiaireNom());
        run.addBreak();

        run.setText("Date d'Attribution: " + historiqueDon.getDateAttribution());
        run.addBreak();

        run.setText("Montant: " + historiqueDon.getMontant());
        run.addBreak();

        // Chemin de sauvegarde du rapport dans le répertoire Téléchargements
        String userHome = System.getProperty("user.home");
        Path downloadPath = Paths.get(userHome, "Downloads");

        // Vérifier et créer le répertoire Téléchargements s'il n'existe pas
        if (!Files.exists(downloadPath)) {
            try {
                Files.createDirectories(downloadPath);
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la création du répertoire Téléchargements.", ButtonType.OK);
                alert.showAndWait();
                return;
            }
        }

        String filePath = downloadPath.resolve("rapport_don_" + historiqueDon.getId() + ".docx").toString();
        System.out.println("Chemin de sauvegarde du rapport: " + filePath);

        // Sauvegarder le rapport dans un fichier
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            document.write(out);
            System.out.println("Rapport généré et sauvegardé avec succès.");
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Rapport généré avec succès : " + filePath, ButtonType.OK);
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la génération du rapport.", ButtonType.OK);
            alert.showAndWait();
        }
    }



}
