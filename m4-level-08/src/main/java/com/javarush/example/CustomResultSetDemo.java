package com.javarush.example;

import java.sql.*;

public class CustomResultSetDemo {
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/sakila";
        String user = "root";
        String password = "sakila";

        Connection conn = DriverManager.getConnection(url, user, password);

        // 1. SCROLLABLE ResultSet
        System.out.println("Scrollable ResultSet:");
        try (Statement stmt = conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = stmt.executeQuery("SELECT customer_id, first_name FROM customer LIMIT 3")) {

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt(1) + " - " + rs.getString(2));
            }

            rs.previous(); // можно двигаться назад
            System.out.println("Последний снова: ID=" + rs.getInt(1));
        }

        // 2. UPDATABLE ResultSet
        System.out.println("\nUpdatable ResultSet:");
        try (Statement stmt = conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
             ResultSet rs = stmt.executeQuery("SELECT customer_id, first_name FROM customer WHERE customer_id = 1")) {

            if (rs.next()) {
                System.out.println("Было: ID=" + rs.getInt(1) + ", Имя=" + rs.getString(2));
                rs.updateString("first_name", "ОбновленноеИмя");
                rs.updateRow();
                System.out.println("Стало: ID=" + rs.getInt(1) + ", Имя=" + rs.getString(2));
            }
        }

        conn.close();
    }
}