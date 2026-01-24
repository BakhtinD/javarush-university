package com.javarush;

import com.javarush.entity.Product;
import com.javarush.entity.User;
import com.javarush.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        demonstrateMappedSuperclass();
    }

    private static void demonstrateMappedSuperclass() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Создаем пользователя
            User user = new User();
            user.setUsername("john_doe");
            user.setEmail("john@example.com");

            // Создаем продукт
            Product product = new Product();
            product.setName("Laptop");
            product.setPrice(999.99);
            product.setInStock(10);

            // Сохраняем (поля createdAt/updatedAt заполнятся автоматически)
            session.save(user);
            session.save(product);

            transaction.commit();

            // Чтение данных
            System.out.println("=== Проверка наследования ===");

            User savedUser = session.get(User.class, user.getId());
            Product savedProduct = session.get(Product.class, product.getId());

            System.out.println("User ID: " + savedUser.getId() +
                    ", Created: " + savedUser.getCreatedAt());
            System.out.println("Product ID: " + savedProduct.getId() +
                    ", Updated: " + savedProduct.getUpdatedAt());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }
}