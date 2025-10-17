/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mainPackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;

/**
 *
 * @author User
 */
public class Database {
    
    private static final String DB_URL = "jdbc:sqlite:database/data.db";
    
    // connect
    public Connection connect() {
        Connection conn = null;
        try {       
            // Start connection
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connected to SQLite database.");
        } catch (SQLException e) {
            System.out.println("SQLite connection failed: " + e.getMessage());
        } 
        return conn;  
    }
    
    public String login(String username, String password) {
    String sql = "SELECT role FROM userData WHERE user = ? AND pass = ?";

    try (Connection conn = connect();
         PreparedStatement state = conn.prepareStatement(sql)) {

        state.setString(1, username);
        state.setString(2, password);
        ResultSet rs = state.executeQuery();

        if (rs.next()) {
            
            return rs.getString("role");
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return null;
}

    
    
}
