package com.javarush.example;

import io.github.cdimascio.dotenv.Dotenv;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class TransactionExample {

    public static void main(String[] args) throws SQLException {

        // Загружаем переменные окружения из .env, который лежит на одном уровне с pom.xml в \m4-level-08
        String projectRoot = Paths.get("m4-level-08").toAbsolutePath().toString();
        Dotenv dotenv = Dotenv.configure()
                .directory(projectRoot)
                .load();

        // Данные для подключения к БД
        String url = dotenv.get("DB_URL");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        Connection connection = null;
        try {
            // 1. Устанавливаем соединение с БД
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Соединение установлено!");

            // 2. Отключаем авто-коммит для ручного управления транзакциями
            connection.setAutoCommit(false);
            System.out.println("Авто-коммит отключен");

            // 3. Выполняем несколько операций в рамках одной транзакции
            try (Statement stmt = connection.createStatement()) {

                // Операция 1: Обновляем email клиента с ID=1
                System.out.println("Операция 1: Обновление e-mail клиента...");
                String updateEmail = "UPDATE customer SET email = 'new.email@example.com' WHERE customer_id = 1";
                int rowUpdated = stmt.executeUpdate(updateEmail);
                System.out.println("Обновлено клиентов: " + rowUpdated);

                // Операция 2: Создаем новый платеж для этого клиента
                System.out.println("Операция 2: Создаем новый платеж для этого клиента...");
                String insertPayment = "INSERT INTO payment (customer_id, staff_id, rental_id, amount, payment_date) "
                        + "VALUES (1, 1, NULL, 10.99, NOW())";
                int rowsInserted = stmt.executeUpdate(insertPayment);
                System.out.println("Добавлено платежей: " + rowsInserted + " шт.");

                // 4. Имитация проверки бизнес-логики
                boolean businessCheckPassed = true; // false - для демонстрации отката БД

                if (businessCheckPassed) {
                    // 5. Если все успешно - коммитим транзакцию
                    connection.commit();
                    System.out.println("Транзакция успешно завершена! Все изменения сохранены.");
                } else {
                    // 6. Если что-то пошло не так - откатываем транзакцию
                    connection.rollback();
                    System.out.println("Транзакция отменена, изменения в БД не сохранены!");
                }

            } catch (SQLException e) {
                // 7. При любой ошибке SQL - откатываем транзакцию
                connection.rollback();
                System.out.println("Ошибка SQL! Транзакция отменена!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 8. Всегда закрываем соединение
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
                System.out.println("Соединение было закрыто!");
            }
        }
    }
}
