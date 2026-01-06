package com.javarush.util;

import com.javarush.util.HibernateUtil;

public class TestHikariConfiguration {
    public static void main(String[] args) {
        System.out.println("=== Тестирование конфигурации HikariCP ===\n");

        try {
            // Получаем SessionFactory (это инициализирует HikariCP)
            var sessionFactory = HibernateUtil.getSessionFactory();

            // Тестируем соединение
            HibernateUtil.testConnectionPool();

            // Простой тест с несколькими соединениями
            System.out.println("\nТест нескольких соединений из пула:");
            for (int i = 1; i <= 3; i++) {
                try (var session = sessionFactory.openSession()) {
                    var result = session.createNativeQuery(
                                    "SELECT CONCAT('Connection ', ?, ' from pool')")
                            .setParameter(1, i)
                            .getSingleResult();
                    System.out.println("   " + result);
                }
            }

            System.out.println("\nВсе тесты пройдены успешно!");

        } catch (Exception e) {
            System.err.println("\nТесты не пройдены:");
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }
}
