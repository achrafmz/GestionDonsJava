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

    public void addDonateur(String nom, String email) throws SQLException {
        String query = "INSERT INTO donateurs (nom, email) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nom);
            stmt.setString(2, email);
            stmt.executeUpdate();
        }
    }

    public void deleteDonateur(int id) throws SQLException {
        String query = "DELETE FROM donateurs WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
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

    public Donateur getDonateurById(int id) {
        String query = "SELECT * FROM donateurs WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Donateur(rs.getInt("id"), rs.getString("nom"), rs.getString("email"), rs.getDouble("montant_donne"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    public void updateDonateur(int id, String nom, String email, double montantDonne) throws SQLException {
        String query = "UPDATE donateurs SET nom = ?, email = ?, montant_donne = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nom);
            stmt.setString(2, email);
            stmt.setDouble(3, montantDonne);
            stmt.setInt(4, id);
            stmt.executeUpdate();
        }
    }

}
