package com.javarush;

import com.javarush.entity.*;
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

        demonstrateSlide5();

        demonstrateSlide6();

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

    private static void demonstrateSlide5() {
        System.out.println("\n=== Слайд 5: Маппинг enum ===");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Создаём продукт с двумя вариантами маппинга enum
            ProductWithEnum product = new ProductWithEnum(
                    "Laptop",
                    ProductCategory.ELECTRONICS, // categoryOrdinal -> 0
                    ProductCategory.ELECTRONICS   // categoryString -> "ELECTRONICS"
            );

            session.save(product);
            transaction.commit();

            System.out.println("✅ Продукт сохранён с enum:");
            System.out.println(product);

            // Загрузим обратно, чтобы убедиться, что маппинг работает
            ProductWithEnum loadedProduct = session.get(ProductWithEnum.class, product.getId());
            System.out.println("📦 Загружено из БД:");
            System.out.println("  categoryOrdinal: " + loadedProduct.getCategoryOrdinal());
            System.out.println("  categoryString: " + loadedProduct.getCategoryString());
        }

    }

    private static void demonstrateSlide6() {
        System.out.println("\n=== Слайд 6: Маппинг Boolean ===");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Создаём вопрос викторины с разными Boolean-маппингами
            QuizQuestion question = new QuizQuestion(
                    "Is Java an object-oriented language?",
                    true,      // isActive -> BIT/TINYINT (1)
                    true,      // isApproved -> numeric_boolean (1)
                    true,      // isVerified -> yes_no ('Y')
                    true,      // isCorrect -> BIT через @Type
                    'T'        // isPublic -> CHAR(1) 'T'
            );

            session.save(question);
            transaction.commit();

            System.out.println("✅ Вопрос викторины сохранён с разными Boolean-маппингами:");
            System.out.println(question);

            // Проверим SQL-логи
            System.out.println("\n📊 В SQL это выглядит так:");
            System.out.println("- is_active: 1 (BIT/TINYINT)");
            System.out.println("- is_approved: 1 (numeric_boolean)");
            System.out.println("- is_verified: 'Y' (yes_no)");
            System.out.println("- is_correct: 1 (BIT через NumericBooleanType)");
            System.out.println("- is_public: 'T' (CHAR(1))");
        }
    }
}