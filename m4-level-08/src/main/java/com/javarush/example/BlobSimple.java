package com.javarush.example;

import java.sql.*;
import java.io.*;

public class BlobSimple {
    public static void main(String[] args) throws SQLException, IOException {
        String url = "jdbc:mysql://localhost:3306/sakila";
        String user = "root";
        String password = "sakila";

        Connection conn = DriverManager.getConnection(url, user, password);

        System.out.println("=== 1. Чтение BLOB (способ 1: getBytes) ===");

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT picture FROM staff WHERE staff_id = 1")) {

            if (rs.next()) {
                Blob blob = rs.getBlob("picture");
                if (blob != null) {
                    byte[] bytes = blob.getBytes(1, (int) blob.length());
                    System.out.println("Прочитано байт: " + bytes.length);
                    blob.free();
                } else {
                    System.out.println("BLOB равен null");
                }
            }
        }

        System.out.println("\n=== 2. Запись BLOB (способ 1: setBytes) ===");

        String updateSQL = "UPDATE staff SET picture = ? WHERE staff_id = 2";
        byte[] testData = "Тестовые данные BLOB".getBytes();

        try (PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setBytes(1, testData);
            int updated = pstmt.executeUpdate();
            System.out.println("Обновлено строк: " + updated);
        }

        System.out.println("\n=== 3. Чтение BLOB (способ 2: getBinaryStream) ===");

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT picture FROM staff WHERE staff_id = 2")) {

            if (rs.next()) {
                Blob blob = rs.getBlob("picture");
                if (blob != null) {
                    try (InputStream is = blob.getBinaryStream()) {
                        int total = 0;
                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = is.read(buffer)) != -1) {
                            total += read;
                        }
                        System.out.println("Прочитано через поток: " + total + " байт");
                    }
                    blob.free();
                }
            }
        }

        conn.close();
        System.out.println("\n✓ BLOB поддерживает работу с бинарными данными!");
    }
}