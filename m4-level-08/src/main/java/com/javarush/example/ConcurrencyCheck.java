package com.javarush.example;

import java.sql.*;

public class ConcurrencyCheck {
    public static void main(String[] args) throws SQLException {
        Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/sakila", "root", "sakila"
        );

        // Check if database supports UPDATABLE ResultSet
        boolean canUpdate = conn.getMetaData().supportsResultSetConcurrency(
                ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_UPDATABLE
        );

        System.out.println("CONCUR_UPDATABLE supported? " + canUpdate);

        // Example usage
        if (canUpdate) {
            Statement stmt = conn.createStatement(
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE
            );
            System.out.println("UPDATABLE statement created");
        }

        conn.close();
    }
}
