package com.achraf.services;

import com.achraf.DBConnection;
import com.achraf.models.Donateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DonateurService {
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
    public void addDonateur(String name, String email, double montantDonne) {
        String query = "INSERT INTO donateurs (nom, email, montant_donne) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setDouble(3, montantDonne);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Donateur> getDonateurs() {
        List<Donateur> donateurs = new ArrayList<>();
        String query = "SELECT * FROM donateurs";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                donateurs.add(new Donateur(rs.getInt("id"), rs.getString("nom"), rs.getString("email"), rs.getDouble("montant_donne")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return donateurs;
    }

    public void deleteDonateur(int id) {
        String query = "DELETE FROM donateurs WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateDonateur(int id, String name, String email, double montantDonne) {
        String query = "UPDATE donateurs SET name = ?, email = ?, montant_donne = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setDouble(3, montantDonne);
            stmt.setInt(4, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
