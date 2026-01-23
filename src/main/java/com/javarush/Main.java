package com.javarush;

import com.javarush.entity.Author;
import com.javarush.entity.Book;
import com.javarush.entity.BookDetail;
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

        demonstrateSlide5();

    }

    private static void demonstrateSlide5() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Author author = new Author("George Orwell");
            session.save(author);

            Book book1 = new Book("1984");
            Book book2 = new Book("Animal Farm");

            // Устанавливаем связь с обеих сторон
            book1.setAuthor(author);
            book2.setAuthor(author);
            author.getBooks().add(book1);
            author.getBooks().add(book2);

            session.save(book1);
            session.save(book2);

            BookDetail detail = new BookDetail("123-123-1234", 328);
            detail.setBook(book1);
            session.save(detail);
            session.save(author);

            transaction.commit();

            System.out.println("Автор " + author.getName());
            System.out.println("Книги автора " + author.getBooks().size());
            System.out.println("Книга 1 " + book1.getAuthor().getName());
            System.out.println("ISBN " + detail.getIsbn());

        }

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
