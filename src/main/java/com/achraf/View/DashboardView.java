package com.achraf.View;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.IOException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import java.nio.file.Files;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import com.achraf.models.*;
import com.achraf.services.*;

import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
        historiqueTable = new TableView<>();

        createAdminTable();
        createDonTable();
        createDonateurTable();
        createBeneficiaireTable();
        createHistoriqueTable();

        Button adminButton = new Button("Gérer les Admins");
        Button donButton = new Button("Gérer les Dons");
        Button donateurButton = new Button("Gérer les Donateurs");
        Button beneficiaireButton = new Button("Gérer les Bénéficiaires");
        Button historiqueButton = new Button("Voir l'Historique des Dons");

        Button addAdminButton = new Button("Ajouter un Admin");
        addAdminButton.setVisible(false);
        Button addDonButton = new Button("Ajouter un Don");
        addDonButton.setVisible(false);
        Button addDonateurButton = new Button("Ajouter un Donateur");
        addDonateurButton.setVisible(false);
        Button addBeneficiaireButton = new Button("Ajouter un Bénéficiaire");
        addBeneficiaireButton.setVisible(false);

        addAdminButton.setOnAction(e -> showAddAdminDialog());
        addDonButton.setOnAction(e -> showAddDonDialog());
        addDonateurButton.setOnAction(e -> showAddDonateurDialog());
        addBeneficiaireButton.setOnAction(e -> showAddBeneficiaireDialog());

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
        sidebar.getStyleClass().add("sidebar");

        cardBox = new HBox(20);
        cardBox.setAlignment(Pos.CENTER);
        cardBox.setPadding(new Insets(20));
        createDashboardCards();

        mainPane = new BorderPane();
        mainPane.setLeft(sidebar);
        mainPane.setCenter(cardBox);

        scene = new Scene(mainPane, 1000, 600);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

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
        donateursCount.getStyleClass().add("card-label");
        VBox donateurCard = new VBox(donateursCount);
        donateurCard.setAlignment(Pos.CENTER);
        donateurCard.getStyleClass().add("card");

        double totalDons = donService.getTotalDons();
        Label donsSum = new Label("Montant Total des Dons: " + totalDons);
        donsSum.getStyleClass().add("card-label");
        VBox donsCard = new VBox(donsSum);
        donsCard.setAlignment(Pos.CENTER);
        donsCard.getStyleClass().add("card");

        cardBox.getChildren().addAll(donateurCard, donsCard);
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
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton = new Button("Supprimer");
            private final Button updateButton = new Button("Modifier");

            {
                deleteButton.setOnAction(event -> {
                    Donateur donateur = getTableView().getItems().get(getIndex());
                    deleteDonateur(donateur);
                });

                updateButton.setOnAction(event -> {
                    Donateur donateur = getTableView().getItems().get(getIndex());
                    showUpdateDonateurDialog(donateur);
                });

                deleteButton.getStyleClass().add("delete-button");
                updateButton.getStyleClass().add("update-button");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(10, updateButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });

        donateurTable.getColumns().addAll(idColumn, nomColumn, emailColumn, montantDonneColumn, actionColumn);
        refreshDonateurTable();
    }
    private void deleteDonateur(Donateur donateur) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setHeaderText("Attention");
        confirmationAlert.setContentText("Vous allez supprimer le donateur " + donateur.getNom() + " et tous ses dons. Voulez-vous continuer ?");

        ButtonType ouiButton = new ButtonType("Oui");
        ButtonType nonButton = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmationAlert.getButtonTypes().setAll(ouiButton, nonButton);

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ouiButton) {
            try {
                // Supprimer tous les dons de ce donateur
                List<Don> dons = donService.getDonsByDonateurId(donateur.getId());
                for (Don don : dons) {
                    donService.deleteDon(don.getId());
                }
                // Supprimer le donateur
                donateurService.deleteDonateur(donateur.getId());
                refreshDonateurTable();
                refreshDonTable();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Donateur et ses dons supprimés avec succès!");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression du donateur et de ses dons.");
            }
        }
    }

    private void createHistoriqueTable() {
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

                viewReportButton.getStyleClass().add("recu-button");
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

    private void deleteAdmin(Admin admin) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setHeaderText("Attention");
        confirmationAlert.setContentText("Vous allez supprimer l'administrateur " + admin.getUsername() + ". Voulez-vous continuer ?");

        ButtonType ouiButton = new ButtonType("Oui");
        ButtonType nonButton = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmationAlert.getButtonTypes().setAll(ouiButton, nonButton);

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ouiButton) {
            adminService.deleteAdmin(admin.getId());
            refreshAdminTable();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Administrateur supprimé avec succès !");
        }
    }


    private void createAdminTable() {
        TableColumn<Admin, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

        TableColumn<Admin, String> usernameColumn = new TableColumn<>("Nom d'utilisateur");
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());

        TableColumn<Admin, String> passwordColumn = new TableColumn<>("Mot de passe");
        passwordColumn.setCellValueFactory(cellData -> cellData.getValue().passwordProperty());

        TableColumn<Admin, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton = new Button("Supprimer");
            private final Button updateButton = new Button("Modifier");

            {
                deleteButton.setOnAction(event -> {
                    Admin admin = getTableView().getItems().get(getIndex());
                    deleteAdmin(admin);
                });

                updateButton.setOnAction(event -> {
                    Admin admin = getTableView().getItems().get(getIndex());
                    showUpdateAdminDialog(admin);
                });

                deleteButton.getStyleClass().add("delete-button");
                updateButton.getStyleClass().add("update-button");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(10, updateButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });

        adminTable.getColumns().addAll(idColumn, usernameColumn, passwordColumn, actionColumn);
        refreshAdminTable();
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
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button attribuerButton = new Button("Attribuer un Don");
            private final Button deleteButton = new Button("Supprimer");
            private final Button updateButton = new Button("Modifier");

            {
                attribuerButton.setOnAction(event -> {
                    Beneficiaire beneficiaire = getTableView().getItems().get(getIndex());
                    handleAttribuerDon(beneficiaire);
                });
                deleteButton.setOnAction(event -> {
                    Beneficiaire beneficiaire = getTableView().getItems().get(getIndex());
                    deleteBeneficiaire(beneficiaire);
                });
                updateButton.setOnAction(event -> {
                    Beneficiaire beneficiaire = getTableView().getItems().get(getIndex());
                    showUpdateBeneficiaireDialog(beneficiaire);
                });

                attribuerButton.getStyleClass().add("attribuer-button");
                deleteButton.getStyleClass().add("delete-button");
                updateButton.getStyleClass().add("update-button");
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
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dddddd; -fx-border-width: 1px;");
        Scene dialogScene = new Scene(vbox, 300, 200);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Ajouter un Admin");
        dialogStage.setScene(dialogScene);
        dialogStage.show();
    }
    private void deleteDon(Don don) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setHeaderText("Attention");
        confirmationAlert.setContentText("Vous allez supprimer le don de " + don.getMontant() + ". Voulez-vous continuer ?");

        ButtonType ouiButton = new ButtonType("Oui");
        ButtonType nonButton = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmationAlert.getButtonTypes().setAll(ouiButton, nonButton);

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ouiButton) {
            donService.deleteDon(don.getId());
            refreshDonTable();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Don supprimé avec succès !");
        }
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
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dddddd; -fx-border-width: 1px;");
        Scene dialogScene = new Scene(vbox, 300, 200);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Modifier Admin");
        dialogStage.setScene(dialogScene);
        dialogStage.show();
    }
    private void createDonTable() {
        TableColumn<Don, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

        TableColumn<Don, String> donateurNomColumn = new TableColumn<>("Nom du Donateur");
        donateurNomColumn.setCellValueFactory(cellData -> {
            Donateur donateur = donateurService.getDonateurById(cellData.getValue().getDonateurId());
            return donateur != null ? new SimpleStringProperty(donateur.getNom()) : new SimpleStringProperty("Erreur");
        });

        TableColumn<Don, Double> montantColumn = new TableColumn<>("Montant");
        montantColumn.setCellValueFactory(cellData -> cellData.getValue().montantProperty().asObject());

        TableColumn<Don, LocalDate> dateDonColumn = new TableColumn<>("Date de Don");
        dateDonColumn.setCellValueFactory(cellData -> cellData.getValue().dateDonProperty());

        TableColumn<Don, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton = new Button("Supprimer");
            private final Button recuButton = new Button("Télécharger Reçu");

            {
                deleteButton.setOnAction(event -> {
                    Don don = getTableView().getItems().get(getIndex());
                    deleteDon(don);
                });

                recuButton.setOnAction(event -> {
                    Don don = getTableView().getItems().get(getIndex());
                    generateRecuPDF(don);
                });

                deleteButton.getStyleClass().add("delete-button");
                recuButton.getStyleClass().add("recu-button");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(10, deleteButton, recuButton);
                    setGraphic(buttons);
                }
            }
        });

        donTable.getColumns().addAll(idColumn, donateurNomColumn, montantColumn, dateDonColumn, actionColumn);
        refreshDonTable();
    }

    private void showAddDonDialog() {
        ComboBox<Donateur> donateurComboBox = new ComboBox<>();
        donateurComboBox.setItems(FXCollections.observableArrayList(donateurService.getDonateurs()));
        donateurComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Donateur donateur) {
                return donateur != null ? donateur.getNom() + " (" + donateur.getEmail() + ")" : "";
            }

            @Override
            public Donateur fromString(String string) {
                return null;
            }
        });

        TextField montantField = new TextField();
        Button addButton = new Button("Ajouter");

        addButton.setOnAction(e -> {
            try {
                Donateur selectedDonateur = donateurComboBox.getValue();
                if (selectedDonateur == null) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner un donateur.");
                    return;
                }

                double montant = Double.parseDouble(montantField.getText().trim());

                donService.addDon(selectedDonateur.getId(), montant);
                refreshDonTable();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer une valeur valide pour le montant.");
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur de la base de données", ex.getMessage());
            }
        });

        VBox vbox = new VBox(10, new Label("Donateur:"), donateurComboBox, new Label("Montant:"), montantField, addButton);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dddddd; -fx-border-width: 1px;");
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
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dddddd; -fx-border-width: 1px;");
        Scene dialogScene = new Scene(vbox, 300, 250);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Modifier Don");
        dialogStage.setScene(dialogScene);
        dialogStage.show();
    }
    private String generateRecuPDF(Don don) {
        try {
            Donateur donateur = donateurService.getDonateurById(don.getDonateurId());

            String fileName = "recu_don_" + don.getId() + ".pdf";
            String userHome = System.getProperty("user.home");
            Path downloadPath = Paths.get(userHome, "Downloads", fileName);

            // Création du document PDF
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.newLineAtOffset(25, 750);
            contentStream.showText("Reçu de Don");
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(25, 725);
            contentStream.showText("ID du Don: " + don.getId());
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(25, 700);
            contentStream.showText("Nom du Donateur: " + donateur.getNom());
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(25, 675);
            contentStream.showText("Montant: " + don.getMontant());
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(25, 650);
            contentStream.showText("Date de Don: " + don.getDateDon());
            contentStream.endText();

            contentStream.close();
            document.save(downloadPath.toString());
            document.close();

            System.out.println("Reçu généré avec succès : " + downloadPath);
            return downloadPath.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showAddDonateurDialog() {
        TextField nomField = new TextField();
        TextField emailField = new TextField();
        Button addButton = new Button("Ajouter");

        addButton.setOnAction(e -> {
            String nom = nomField.getText().trim();
            String email = emailField.getText().trim();

            if (nom.isEmpty() || email.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs obligatoires.");
                return;
            }

            try {
                donateurService.addDonateur(nom, email);
                refreshDonateurTable();
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur de la base de données", ex.getMessage());
            }
        });

        VBox vbox = new VBox(10, new Label("Nom:"), nomField, new Label("Email:"), emailField, addButton);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dddddd; -fx-border-width: 1px;");
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
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dddddd; -fx-border        .border-width: 1px;");
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
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dddddd; -fx-border-width: 1px;");
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
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dddddd; -fx-border-width: 1px;");
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
                    donBeneficiaireService.attribuerDon(1, beneficiaire.getId(), montant, LocalDate.now());
                    donBeneficiaireService.enregistrerHistoriqueDon(1, beneficiaire.getId(), montant, LocalDate.now());
                    donService.updateTotalDons(totalDons - montant);
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Don attribué avec succès et enregistré dans l'historique des dons!");
                } else {
                    showAlert(Alert.AlertType.WARNING, "Fonds insuffisants", "Fonds insuffisants pour attribuer ce don.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'attribution du don.");
            }
        }
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
        run.setText("Adresse : Rue ZERKTOUNI, Marrakech, Maroc");
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
        // Vérifier et créer le répertoire Téléchargements s'il n'existe pas
        if (!Files.exists(downloadPath)) {
            try {
                Files.createDirectories(downloadPath);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la création du répertoire Téléchargements.");
                return;
            }
        }


        String filePath = downloadPath.resolve("rapport_don_" + historiqueDon.getId() + ".docx").toString();
        System.out.println("Chemin de sauvegarde du rapport: " + filePath);

        // Sauvegarder le rapport dans un fichier
        // Sauvegarder le rapport dans un fichier
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            document.write(out);
            System.out.println("Rapport généré et sauvegardé avec succès.");
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Rapport généré avec succès : " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la génération du rapport.");
        }

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

}
