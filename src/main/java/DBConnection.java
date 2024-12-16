package com.achraf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/Gestion_Dons";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }
}
