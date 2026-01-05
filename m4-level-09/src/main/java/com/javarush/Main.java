package com.javarush;

import com.javarush.entity.User;
import com.javarush.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Демонстрация Hibernate ===");

        // 1. Сохранение объекта (INSERT)
        System.out.println("\n1. Сохранение нового пользователя...");
        User newUser = new User("JohnDoe", "john@example.com", 10);
        saveUser(newUser);
        System.out.println("Сохранён пользователь: " + newUser);

        // 2. Получение объекта по ID (SELECT)
        System.out.println("\n2. Получение пользователя по ID = " + newUser.getId());
        User retrievedUser = getUserById(newUser.getId());
        System.out.println("Получен пользователь: " + retrievedUser);

        // 3. Обновление объекта (UPDATE) - просто меняем поле и сохраняем снова
        System.out.println("\n3. Обновление уровня пользователя...");
        retrievedUser.setLevel(20);
        updateUser(retrievedUser);
        System.out.println("Уровень обновлён.");

        // 4. Удаление объекта (DELETE)
        System.out.println("\n4. Удаление пользователя...");
        deleteUser(retrievedUser);
        System.out.println("Пользователь удалён.");

        // 5. Проверка, что удалён
        User deletedUser = getUserById(newUser.getId());
        System.out.println("Попытка получить удалённого пользователя: " + deletedUser);

        HibernateUtil.shutdown();
        System.out.println("\n=== Демонстрация завершена ===");
    }

    private static void saveUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(user); // JPA-стандарт (Слайд 16)
            transaction.commit();
        }
    }

    private static User getUserById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Метод get() (Слайд 15)
            return session.get(User.class, id);
        }
    }

    private static void updateUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(user); // Можно использовать merge() для JPA
            transaction.commit();
        }
    }

    private static void deleteUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(user); // JPA-стандарт (Слайд 17)
            transaction.commit();
        }
    }
}
