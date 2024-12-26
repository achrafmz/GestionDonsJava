package com.achraf.services;

import com.achraf.DBConnection;
import com.achraf.models.Beneficiaire;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BeneficiaireService {

    private ObservableList<Beneficiaire> beneficiaireList = FXCollections.observableArrayList();

    public ObservableList<Beneficiaire> getBeneficiaires() {
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

                Beneficiaire beneficiaire = new Beneficiaire(id, nom, prenom, email, telephone, adresse);
                beneficiaireList.add(beneficiaire);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return beneficiaireList;
    }

    public void addBeneficiaire(String nom, String prenom, String email, String telephone, String adresse) {
        String query = "INSERT INTO beneficiaires (nom, prenom, email, telephone, adresse) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, email);
            stmt.setString(4, telephone);
            stmt.setString(5, adresse);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteBeneficiaire(int id) {
        String query = "DELETE FROM beneficiaires WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBeneficiaire(Beneficiaire beneficiaire) {
        String query = "UPDATE beneficiaires SET nom = ?, prenom = ?, email = ?, telephone = ?, adresse = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, beneficiaire.getNom());
            stmt.setString(2, beneficiaire.getPrenom());
            stmt.setString(3, beneficiaire.getEmail());
            stmt.setString(4, beneficiaire.getTelephone());
            stmt.setString(5, beneficiaire.getAdresse());
            stmt.setInt(6, beneficiaire.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
