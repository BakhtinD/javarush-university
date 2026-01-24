package com.javarush;

import com.javarush.entity.*;
import com.javarush.util.HibernateUtil;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        // demonstrateSlideN();

        demonstrateSlide4();

        demonstrateSlide5();

    }

    private static void demonstrateSlide5() {
        System.out.println("=== Демонстрация Slide 5: Значения по умолчанию ===");

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        System.out.println("\n1. Создаем тестовые данные:");

        // Создаем издателя
        Publisher publisher = new Publisher();
        publisher.setName("Tech Books Publishing");
        session.save(publisher);
        System.out.println("   - Создан Publisher: " + publisher.getName());

        // Создаем автора
        Author author = new Author();
        author.setName("Anna Ivanova");
        author.setPublisher(publisher);
        session.save(author);
        System.out.println("   - Создан Author: " + author.getName());

        // Создаем книги
        Book book1 = new Book();
        book1.setTitle("Hibernate");
        book1.setAuthor(author);
        session.save(book1);

        Book book2 = new Book();
        book2.setTitle("Hibernate 2");
        book2.setAuthor(author);
        session.save(book2);

        System.out.println("   - Создано 2 книги автора");

        // Создаем отзывы
        Review review1 = new Review();
        review1.setText("Best!");
        review1.setRating(5);
        book1.addReview(review1);
        session.save(review1);

        Review review2 = new Review();
        review2.setText("Ok");
        review2.setRating(4);
        book1.addReview(review2);
        session.save(review2);

        System.out.println("   - Создано 2 отзыва на первую книгу");

        tx.commit();
        session.close();

        System.out.println("\n2. Демонстрация значений по умолчанию:");

        session = HibernateUtil.getSessionFactory().openSession();

        System.out.println("\n   Загружаем книгу по ID...");
        Book loadedBook = session.get(Book.class, book1.getId());

        System.out.println("   Книга загружена: " + loadedBook.getTitle());

        // ManyToOne: EAGER по умолчанию - автор загружен сразу
        System.out.println("   Автор (ManyToOne - EAGER по умолчанию): " +
                loadedBook.getAuthor().getName());

        // OneToMany: LAZY по умолчанию - отзывы еще не загружены
        System.out.println("   Отзывы (OneToMany - LAZY по умолчанию):");
        System.out.println("   Инициализированы? " +
                Hibernate.isInitialized(loadedBook.getReviews()));

        // Теперь загружаем отзывы
        System.out.println("\n   Обращаемся к отзывам...");
        List<Review> reviews = loadedBook.getReviews(); // Запрос выполнится здесь
        System.out.println("   Количество отзывов: " + reviews.size());
        reviews.forEach(r ->
                System.out.println("   - " + r.getText() + " (рейтинг: " + r.getRating() + ")")
        );

        // Проверяем издателя через автора (цепочка связей)
        System.out.println("\n3. Проверка цепочки связей:");
        Author bookAuthor = loadedBook.getAuthor();
        System.out.println("   Автор книги: " + bookAuthor.getName());

        // OneToMany: LAZY по умолчанию - книги автора еще не загружены
        System.out.println("   Книги автора (OneToMany - LAZY):");
        System.out.println("   Инициализированы? " +
                Hibernate.isInitialized(bookAuthor.getBooks()));

        // ManyToOne: EAGER по умолчанию - издатель автора загружен сразу
        System.out.println("   Издатель автора (ManyToOne - EAGER): " +
                bookAuthor.getPublisher().getName());

        session.close();

        System.out.println("\n=== Итог: ===");
        System.out.println("• @ManyToOne → EAGER (загружается сразу)");
        System.out.println("• @OneToMany → LAZY (загружается при обращении)");
        System.out.println("• Это значения ПО УМОЛЧАНИЮ, их можно переопределять!");

        System.out.println("\n=== Конец демонстрации ===");
    }

    private static void demonstrateSlide4() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        // Создаем тестовые данные
        Department dept = new Department();
        dept.setName("IT");
        session.save(dept);

        User user = new User();
        user.setName("John Doe");
        user.setDepartment(dept);
        session.save(user);

        Comment comment1 = new Comment();
        comment1.setText("First comment");
        comment1.setUser(user);
        session.save(comment1);

        Comment comment2 = new Comment();
        comment2.setText("Second comment");
        comment2.setUser(user);
        session.save(comment2);

        session.getTransaction().commit();
        session.close();

        // Новая сессия для демонстрации LAZY
        session = HibernateUtil.getSessionFactory().openSession();
        User loadedUser = session.get(User.class, user.getId());

        System.out.println("User loaded: " + loadedUser.getName());
        System.out.println("Department (EAGER) loaded immediately: " +
                loadedUser.getDepartment().getName());

        // Комментарии (LAZY) загрузятся только при обращении
        System.out.println("Accessing comments (LAZY)...");
        List<Comment> comments = loadedUser.getComments(); // Здесь выполнится запрос
        comments.forEach(c -> System.out.println("Comment: " + c.getText()));

        session.close();
    }

    private static void demonstrateSlideN() {
        //
    }

}
