package com.javarush.example;

import java.sql.*;

public class CallableStatementSimple {
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/sakila";
        String user = "root";
        String password = "sakila";

        Connection conn = DriverManager.getConnection(url, user, password);

        System.out.println("=== 1. Вызов функции get_customer_balance ===");
        String callFunc = "{? = call get_customer_balance(?, ?)}";

        try (CallableStatement cstmt = conn.prepareCall(callFunc)) {
            cstmt.registerOutParameter(1, Types.DECIMAL); // возвращаемое значение
            cstmt.setInt(2, 1); // customer_id = 1
            cstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis())); // текущая дата

            cstmt.execute();
            double balance = cstmt.getDouble(1);
            System.out.println("Баланс клиента 1: $" + balance);
        }

        System.out.println("\n=== 2. Вызов процедуры film_in_stock ===");
        String callProc = "{call film_in_stock(?, ?, ?)}";

        try (CallableStatement cstmt = conn.prepareCall(callProc)) {
            cstmt.setInt(1, 1); // film_id = 1
            cstmt.setInt(2, 1); // store_id = 1
            cstmt.registerOutParameter(3, Types.INTEGER); // film_count OUT

            boolean hasResults = cstmt.execute();

            // Получаем OUT параметр
            int filmCount = cstmt.getInt(3);
            System.out.println("Фильм ID=1 в наличии: " + filmCount + " копий");

            // Обрабатываем ResultSet, если есть
            if (hasResults) {
                System.out.println("ID инвентаря в наличии:");
                try (ResultSet rs = cstmt.getResultSet()) {
                    while (rs.next()) {
                        System.out.println("  - " + rs.getInt(1));
                    }
                }
            }
        }

        conn.close();
    }
}