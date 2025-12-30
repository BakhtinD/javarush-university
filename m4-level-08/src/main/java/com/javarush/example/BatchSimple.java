package com.javarush.example;

import java.sql.*;

public class BatchSimple {
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/sakila";
        String user = "root";
        String password = "sakila";

        Connection conn = DriverManager.getConnection(url, user, password);
        conn.setAutoCommit(false);

        System.out.println("=== Batch с PreparedStatement ===");

        String insertSQL = "INSERT INTO actor (first_name, last_name) VALUES (?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            // Добавляем 3 актера в batch
            pstmt.setString(1, "Batch");
            pstmt.setString(2, "User1");
            pstmt.addBatch();

            pstmt.setString(1, "Batch");
            pstmt.setString(2, "User2");
            pstmt.addBatch();

            pstmt.setString(1, "Batch");
            pstmt.setString(2, "User3");
            pstmt.addBatch();

            System.out.println("Добавлено 3 запроса в batch");

            // Выполняем batch
            int[] results = pstmt.executeBatch();
            System.out.println("Результаты:");
            for (int i = 0; i < results.length; i++) {
                System.out.println("  Запрос " + (i+1) + ": " + results[i] + " строк");
            }

            conn.commit();
            System.out.println("✓ Batch успешно выполнен");

        } catch (SQLException e) {
            conn.rollback();
            System.out.println("✗ Ошибка! Batch откатан");
            e.printStackTrace();
        }

        // -- МОЖНО БЕЗ СРАВНЕНИЯ ПРОИЗВОДИТЕЛЬНОСТИ

        System.out.println("\n=== Сравнение производительности ===");

        // Тест: 50 запросов по отдельности
        long start = System.currentTimeMillis();
        try (PreparedStatement single = conn.prepareStatement(insertSQL)) {
            for (int i = 0; i < 50; i++) {
                single.setString(1, "Single" + i);
                single.setString(2, "Test" + i);
                single.executeUpdate();
            }
            conn.commit();
        }
        long singleTime = System.currentTimeMillis() - start;
        System.out.println("50 отдельных запросов: " + singleTime + " мс");

        // Тест: 50 запросов batch
        start = System.currentTimeMillis();
        try (PreparedStatement batch = conn.prepareStatement(insertSQL)) {
            for (int i = 0; i < 50; i++) {
                batch.setString(1, "Batch" + i);
                batch.setString(2, "Test" + i);
                batch.addBatch();
            }
            batch.executeBatch();
            conn.commit();
        }
        long batchTime = System.currentTimeMillis() - start;
        System.out.println("50 запросов batch: " + batchTime + " мс");

        double faster = (double) singleTime / batchTime;
        System.out.println(String.format("Batch быстрее в %.1f раз!", faster));

        conn.setAutoCommit(true);
        conn.close();
    }
}