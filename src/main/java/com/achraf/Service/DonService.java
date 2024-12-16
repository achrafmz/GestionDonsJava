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
    public boolean authenticate(String username, String password) {
        String query = "SELECT * FROM admins WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public void addDon(int donateurId, double montant, LocalDate dateDon) {
        String query = "INSERT INTO dons (donateur_id, montant, date_don) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, donateurId);
            stmt.setDouble(2, montant);
            stmt.setDate(3, java.sql.Date.valueOf(dateDon));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Don> getDons() {
        List<Don> dons = new ArrayList<>();
        String query = "SELECT * FROM dons";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                dons.add(new Don(rs.getInt("id"), rs.getInt("donateur_id"), rs.getDouble("montant"), rs.getDate("date_don").toLocalDate()));
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

    public void updateDon(int id, int donateurId, double montant, LocalDate dateDon) {
        String query = "UPDATE dons SET donateur_id = ?, montant = ?, date_don = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, donateurId);
            stmt.setDouble(2, montant);
            stmt.setDate(3, java.sql.Date.valueOf(dateDon));
            stmt.setInt(4, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
