package com.achraf.services;

import com.achraf.DBConnection;
import com.achraf.models.Beneficiaire;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BeneficiaireService {
    public List<Beneficiaire> getBeneficiaires() throws SQLException {
        List<Beneficiaire> beneficiaires = new ArrayList<>();
        String query = "SELECT * FROM beneficiaires";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Beneficiaire beneficiaire = new Beneficiaire();
                beneficiaire.setId(rs.getInt("id"));
                beneficiaire.setNom(rs.getString("nom"));
                beneficiaire.setPrenom(rs.getString("prenom"));
                beneficiaire.setEmail(rs.getString("email"));
                beneficiaire.setTelephone(rs.getString("telephone"));
                beneficiaire.setAdresse(rs.getString("adresse"));
                beneficiaires.add(beneficiaire);
            }
        }
        return beneficiaires;
    }

    public void addBeneficiaire(String nom, String prenom, String email, String telephone, String adresse) throws SQLException {
        String query = "INSERT INTO beneficiaires (nom, prenom, email, telephone, adresse) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, email);
            stmt.setString(4, telephone);
            stmt.setString(5, adresse);
            stmt.executeUpdate();
        }
    }

    public void updateBeneficiaire(int id, String nom, String prenom, String email, String telephone, String adresse) throws SQLException {
        String query = "UPDATE beneficiaires SET nom = ?, prenom = ?, email = ?, telephone = ?, adresse = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, email);
            stmt.setString(4, telephone);
            stmt.setString(5, adresse);
            stmt.setInt(6, id);
            stmt.executeUpdate();
        }
    }

    public void deleteBeneficiaire(int id) throws SQLException {
        String query = "DELETE FROM beneficiaires WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
