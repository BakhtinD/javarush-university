package com.javarush;

import com.javarush.entity.User;
import com.javarush.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

/**
 * JavaRush-University
 */
public class Main {

    public static void main(String[] args) {
        demonstrateSlide3();
    }

    private static void demonstrateSlide3() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Создание пользователя с коллекцией сообщений
            User user = new User(
                    "ivan",
                    "ivan@mail.ru",
                    "42",
                    true,
                    95.2,
                    new BigDecimal("50000"),
                    LocalDate.of(1990, 5, 16),
                    new Date(),
                    "avatar".getBytes()
            );

            user.getMessages().add("Hello,");
            user.getMessages().add("Hibernate!");

            session.save(user);
            transaction.commit();

            System.out.println("User: " + user.getId() + " " + user.getMessages());

        }

    }


}
