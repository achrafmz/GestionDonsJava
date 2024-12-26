package com.achraf.controllers;

import com.achraf.DBConnection;
import com.achraf.models.Beneficiaire;
import com.achraf.services.BeneficiaireService;
import com.achraf.services.DonBeneficiaireService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class GestionBeneficiaireController {

    @FXML
    private VBox mainContent;

    @FXML
    private TableView<Beneficiaire> beneficiaireTable;
    @FXML
    private TableColumn<Beneficiaire, Integer> idColumn;
    @FXML
    private TableColumn<Beneficiaire, String> nomColumn;
    @FXML
    private TableColumn<Beneficiaire, String> prenomColumn;
    @FXML
    private TableColumn<Beneficiaire, String> emailColumn;
    @FXML
    private TableColumn<Beneficiaire, String> telephoneColumn;
    @FXML
    private TableColumn<Beneficiaire, String> adresseColumn;
    @FXML
    private TableColumn<Beneficiaire, Void> actionColumn;

    private ObservableList<Beneficiaire> beneficiaireList = FXCollections.observableArrayList();
    private BeneficiaireService beneficiaireService = new BeneficiaireService();
    private DonBeneficiaireService donBeneficiaireService = new DonBeneficiaireService();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        nomColumn.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        prenomColumn.setCellValueFactory(cellData -> cellData.getValue().prenomProperty());
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        telephoneColumn.setCellValueFactory(cellData -> cellData.getValue().telephoneProperty());
        adresseColumn.setCellValueFactory(cellData -> cellData.getValue().adresseProperty());

        // Ajouter des boutons dans la colonne des actions
        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Beneficiaire, Void> call(final TableColumn<Beneficiaire, Void> param) {
                return new TableCell<>() {
                    private final Button attribuerButton = new Button("Attribuer un Don");

                    {
                        attribuerButton.setOnAction(event -> handleAttribuerDon(getTableView().getItems().get(getIndex())));
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(attribuerButton);
                        }
                    }
                };
            }
        });

        beneficiaireTable.setItems(beneficiaireList);
        loadBeneficiaires();
    }

    private void loadBeneficiaires() {
        beneficiaireList.clear();
        String query = "SELECT * FROM beneficiaires";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String email = rs.getString("email");
                String telephone = rs.getString("telephone");
                String adresse = rs.getString("adresse");
                beneficiaireList.add(new Beneficiaire(id, nom, prenom, email, telephone, adresse));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                int donId = 1; // ID du don existant ou à créer
                donBeneficiaireService.attribuerDon(donId, beneficiaire.getId(), montant, LocalDate.now());

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Don attribué avec succès!", ButtonType.OK);
                alert.showAndWait();
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'attribution du don.", ButtonType.OK);
                alert.showAndWait();
            }
        }
    }
}
