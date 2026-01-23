package com.javarush;

import com.javarush.entity.User;
import com.javarush.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

public class Main {

    public static void main(String[] args) {

        demonstrateSlide3();

        HibernateUtil.shutdown();
    }

    private static void demonstrateSlide3() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Создаём пользователя с коллекцией сообщений
            User user = new User(
                    "ivan_java",
                    "ivan@example.com",
                    42,
                    true,
                    95.5,
                    new BigDecimal("55000.75"),
                    LocalDate.of(1990, 5, 15),
                    new Date(),
                    "avatar_data".getBytes()
            );

            // Добавляем сообщения в коллекцию
            user.getMessages().add("Hello!");
            user.getMessages().add("Hibernate.");

            // Сохраняем пользователя (коллекция сохранится автоматически)
            session.save(user);
            transaction.commit();

            System.out.println("✅ Пользователь сохранён с коллекцией сообщений!");
            System.out.println("ID пользователя: " + user.getId());
            System.out.println("Сообщения: " + user.getMessages());
        }

    }
}
