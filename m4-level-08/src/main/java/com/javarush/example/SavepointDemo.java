package com.javarush.example;

import java.sql.*;

public class SavepointDemo {

    public static void main(String[] args) throws SQLException {

        // Данные для подключения к БД
        String url = "jdbc:mysql://localhost:3306/sakila";
        String user = "root";       //
        String password = "sakila"; //

        Connection conn = DriverManager.getConnection(url, user, password);
        conn.setAutoCommit(false);

        try (Statement stmt = conn.createStatement()) {

            // Шаг 1: первая операция
            stmt.executeUpdate("UPDATE address SET phone = '111-1111' WHERE address_id = 1");
            System.out.println("1. Телефон обновлен");

            // Создадим точку сохранения
            Savepoint savepoint1 = conn.setSavepoint();
            System.out.println(" Точка сохранения создана");

            // Шаг 2:  2-ая операция
            stmt.executeUpdate("UPDATE customer SET email = 'test@test.com' WHERE customer_id = 1");
            System.out.println(" email обновлен");

            // Имитация ошибки
            System.out.println("3. Рискованная тяжелая операция");
            try {
                stmt.executeUpdate("UPDATE nonexistent_table SET x = 1"); // Ошибка!
            } catch (SQLException e) {
                System.out.println(" Ошибка " + e.getMessage());

                // Откат к точке сохранения - savepoint1
                conn.rollback(savepoint1);
                System.out.println(" Откат к точке восстановления savepoint1 выполнен");
            }

            // Проверяем что первая операция сохранилась
            ResultSet rs = stmt.executeQuery("SELECT phone FROM address WHERE address_id = 1");
            if (rs.next()) {
                System.out.println("\n телефон после отката: " + rs.getString(1));
            }

            // завершаем транзакцию
            conn.commit();
            System.out.println("Транзакция завершена успешно!");

        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }

    }

}
