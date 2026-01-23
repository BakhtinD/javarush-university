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

    private static void demonstrateSlide4() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Создаём сотрудника
            Employee employee = new Employee("John Doe");

            // 1. List<String> - упорядоченные навыки
            employee.getSkills().add("Java");
            employee.getSkills().add("SQL");
            employee.getSkills().add("Hibernate");

            // 2. Set<String> - уникальные языки
            employee.getLanguages().add("English");
            employee.getLanguages().add("Spanish");
            employee.getLanguages().add("English"); // Дубликат - не добавится

            session.save(employee);
            transaction.commit();

            System.out.println("✅ Employee saved with two auxiliary tables!");
            System.out.println("Employee ID: " + employee.getId());
            System.out.println("Skills (List, ordered): " + employee.getSkills());
            System.out.println("Languages (Set, unique): " + employee.getLanguages());
        }
    }

    private static void demonstrateSlide5() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // 1. Создаём автора
            Author author = new Author("George Orwell");
            session.save(author);

            // 2. Создаём книги
            Book book1 = new Book("1984");
            Book book2 = new Book("Animal Farm");

            // 3. Связываем книги с автором (Many-to-One)
            book1.setAuthor(author);
            book2.setAuthor(author);

            session.save(book1);
            session.save(book2);

            // 4. Создаём детали для книги (One-to-One)
            BookDetail detail = new BookDetail("978-0451524935", 328);
            detail.setBook(book1);
            session.save(detail);

            transaction.commit();

            System.out.println("\n✅ Slide 5: Entity Relationships");
            System.out.println("Author: " + author.getName());
            System.out.println("Books by author: " + author.getBooks().size());
            System.out.println("Book '1984' author: " + book1.getAuthor().getName());
            System.out.println("Book ISBN: " + detail.getIsbn());
        }
    }

}
