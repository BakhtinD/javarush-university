package com.javarush.example;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.*;

public class SimpleConnectionPoolDemo {
    public static void main(String[] args) throws SQLException {
        System.out.println("=== ПРОСТОЙ ПРИМЕР CONNECTION POOL ===\n");

        // 1. Быстрая настройка HikariCP
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://localhost:3306/sakila");
        ds.setUsername("root");
        ds.setPassword("sakila");
        ds.setMaximumPoolSize(5);

        System.out.println("Пул создан (макс. 5 соединений)");

        // 2. Получаем и используем соединения
        System.out.println("\nПолучаем 3 соединения:");

        Connection conn1 = ds.getConnection();
        System.out.println("1. Соединение 1 получено");

        Connection conn2 = ds.getConnection();
        System.out.println("2. Соединение 2 получено");

        Connection conn3 = ds.getConnection();
        System.out.println("3. Соединение 3 получено");

        // 3. Используем одно из соединений
        System.out.println("\nИспользуем соединение 1:");
        Statement stmt = conn1.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT 'Привет из пула!' as message");

        if (rs.next()) {
            System.out.println("Результат: " + rs.getString("message"));
        }

        // 4. Возвращаем соединения в пул (close() возвращает в пул!)
        System.out.println("\nВозвращаем соединения в пул:");
        conn1.close();
        conn2.close();
        conn3.close();
        System.out.println("Соединения возвращены (не закрыты!)");

        // 5. Закрываем пул
        ds.close();
        System.out.println("\n✓ Пул соединений закрыт");
    }
}
