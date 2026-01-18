package com.javarush;

import com.javarush.entity.Document;
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

        demonstrateSlide4();

        HibernateUtil.shutdown();
    }

    private static void demonstrateSlide3() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Создаём пользователя с разными типами данных
            User user = new User(
                    "ivan_java",
                    "ivan@example.com",
                    42,
                    true,                           // boolean
                    95.5,                           // Double
                    new BigDecimal("55000.75"),     // BigDecimal
                    LocalDate.of(1990, 5, 15),      // LocalDate
                    new Date(),                     // Date
                    "avatar_data".getBytes()        // byte[]
            );

            session.save(user);
            transaction.commit();

            System.out.println("✅ Пользователь сохранён с разными типами данных:");
            System.out.println(user);
        }
    }

    private static void demonstrateSlide4() {
        System.out.println("\n=== Слайд 4: @Type (Boolean маппинг) ===");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Document doc = new Document(
                    "Hibernate Guide",
                    true,        // isSigned -> 'Y'
                    false,       // isArchived -> 0
                    true,        // isPublic -> 1 (BIT)
                    LocalDate.of(2024, 1, 18)
            );

            session.save(doc);
            transaction.commit();

            System.out.println("✅ Документ сохранён с разными Boolean-маппингами:");
            System.out.println(doc);
        }
    }

}