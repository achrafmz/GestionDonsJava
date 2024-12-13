package com.achraf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DonateurDAO {

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/gestion_dons", "root", "");
    }

    public void ajouterDonateur(Donateur donateur) {
        String query = "INSERT INTO donateurs (nom, email, montant_donne) VALUES (?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, donateur.getNom());
            statement.setString(2, donateur.getEmail());
            statement.setDouble(3, donateur.getMontantDonne());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Donateur ajouté avec succès.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
