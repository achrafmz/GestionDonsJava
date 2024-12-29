package com.achraf.services;

import com.achraf.DBConnection;
import com.achraf.models.Don;
import com.achraf.models.Donateur;
import com.achraf.utils.EmailUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DonService {
    private DonateurService donateurService;

    public DonService() {
        this.donateurService = new DonateurService();
    }

    public List<Don> getDonsByDonateurId(int donateurId) throws SQLException {
        List<Don> dons = new ArrayList<>();
        String query = "SELECT * FROM dons WHERE donateur_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, donateurId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Don don = new Don(
                            rs.getInt("id"),
                            rs.getInt("donateur_id"),
                            rs.getDouble("montant"),
                            rs.getDate("date_don").toLocalDate()
                    );
                    dons.add(don);
                }
            }
        }
        return dons;
    }

    public void addDon(int donateurId, double montant, LocalDate dateDon) throws SQLException {
        String query = "INSERT INTO dons (donateur_id, montant, date_don) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, donateurId);
            stmt.setDouble(2, montant);
            stmt.setDate(3, Date.valueOf(dateDon));
            stmt.executeUpdate();

            // Envoyer un email de remerciement
            Donateur donateur = donateurService.getDonateurById(donateurId);
            String subject = "Merci pour votre don!";
            String body = "Cher " + donateur.getNom() + ",\n\nMerci beaucoup pour votre généreux don de " + montant + " DHS.\n\nVotre soutien est grandement apprécié!\n\nCordialement,\nL'Association Najd";
            EmailUtil.sendEmail(donateur.getEmail(), subject, body);
        }
    }

    public boolean donateurExists(int donateurId) {
        String query = "SELECT 1 FROM donateurs WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, donateurId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public double getTotalDons() throws SQLException {
        String query = "SELECT montant FROM total_dons WHERE id = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("montant");
            } else {
                throw new SQLException("Erreur : Total des dons non trouvé.");
            }
        }
    }

    public void updateTotalDons(double totalDons) throws SQLException {
        String query = "UPDATE total_dons SET montant = ? WHERE id = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, totalDons);
            stmt.executeUpdate();
        }
    }

    public List<Don> getDons() {
        List<Don> dons = new ArrayList<>();
        String query = "SELECT * FROM dons";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                int donateurId = rs.getInt("donateur_id");
                double montant = rs.getDouble("montant");
                java.sql.Date sqlDateDon = rs.getDate("date_don");
                LocalDate dateDon = sqlDateDon != null ? sqlDateDon.toLocalDate() : null;
                dons.add(new Don(id, donateurId, montant, dateDon));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dons;
    }

    public void deleteDon(int id) {
        String query = "DELETE FROM dons WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateDon(int id, int donateurId, double montant, LocalDate dateDon) throws SQLException {
        String query = "UPDATE dons SET donateur_id = ?, montant = ?, date_don = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, donateurId);
            stmt.setDouble(2, montant);
            stmt.setDate(3, java.sql.Date.valueOf(dateDon));
            stmt.setInt(4, id);
            stmt.executeUpdate();
        }
    }
}
