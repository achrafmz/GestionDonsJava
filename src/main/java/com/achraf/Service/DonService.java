package com.achraf.services;

import com.achraf.DBConnection;
import com.achraf.models.Don;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DonService {

    public void addDon(int donateurId, double montant, LocalDate dateDon) throws SQLException {
        if (!donateurExists(donateurId)) {
            throw new SQLException("Le donateur avec l'ID " + donateurId + " n'existe pas.");
        }

        String query = "INSERT INTO dons (donateur_id, montant, date_don) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, donateurId);
            stmt.setDouble(2, montant);
            stmt.setDate(3, java.sql.Date.valueOf(dateDon));
            stmt.executeUpdate();
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
