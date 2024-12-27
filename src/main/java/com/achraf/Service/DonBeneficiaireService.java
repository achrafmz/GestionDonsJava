package com.achraf.services;

import com.achraf.DBConnection;
import com.achraf.models.HistoriqueDon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DonBeneficiaireService {

    public void attribuerDon(int donId, int beneficiaireId, double montant, LocalDate dateAttribution) throws SQLException {
        String query = "INSERT INTO historique_dons (don_id, beneficiaire_id, date_attribution, montant) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, donId);
            stmt.setInt(2, beneficiaireId);
            stmt.setDate(3, java.sql.Date.valueOf(dateAttribution));
            stmt.setDouble(4, montant);
            stmt.executeUpdate();
            System.out.println("Don ajouté avec succès dans l'historique.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Relance l'exception après log
        }
    }

    public void enregistrerHistoriqueDon(int donId, int beneficiaireId, double montant, LocalDate dateAttribution) throws SQLException {
        
    }



    public List<HistoriqueDon> getHistoriqueDons() {
        List<HistoriqueDon> historiqueDons = new ArrayList<>();
        String query = "SELECT * FROM historique_dons";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                int donId = rs.getInt("don_id");
                int beneficiaireId = rs.getInt("beneficiaire_id");
                LocalDate dateAttribution = rs.getDate("date_attribution").toLocalDate();
                double montant = rs.getDouble("montant");
                historiqueDons.add(new HistoriqueDon(id, donId, beneficiaireId, dateAttribution, montant));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return historiqueDons;
    }
    private boolean donExistsInHistorique(int donId, int beneficiaireId, LocalDate dateAttribution) throws SQLException {
        String query = "SELECT 1 FROM historique_dons WHERE don_id = ? AND beneficiaire_id = ? AND date_attribution = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, donId);
            stmt.setInt(2, beneficiaireId);
            stmt.setDate(3, java.sql.Date.valueOf(dateAttribution));
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

}
