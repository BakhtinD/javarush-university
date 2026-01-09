package com.javarush;

import com.javarush.entity.User;
import com.javarush.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        System.out.println("=== НАЧАЛО РАБОТЫ С ПРОГРАММИРУЕМОЙ КОНФИГУРАЦИЕЙ ===");

        // 1. Сохранение объекта (INSERT)
        System.out.println("\n1. Сохранение новых пользователей...");
        User newUser = new User("JohnDoe", "john@example.com", 10);
        saveUser(newUser);
        saveUser(new User("Alice", "alice@example.com", 5));
        saveUser(new User("Bob", "bob@example.com", 15));
        System.out.println("Сохранён пользователь: " + newUser);

        // 2. Получение объекта по ID (SELECT)
        System.out.println("\n2. Получение пользователя по ID = " + newUser.getId());
        User retrievedUser = getUserById(newUser.getId());
        System.out.println("Получен пользователь: " + retrievedUser);

        // 3. Обновление объекта (UPDATE)
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

        // 6. Демонстрация HQL (простой запрос "FROM")
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("from User", User.class);
            List<User> allUsers = query.getResultList();
            System.out.println("Найдено пользователей: " + allUsers.size());
            for (User user : allUsers) {
                System.out.println("  -> " + user);
            }
        }

        // ... остальной код без изменений (как у вас было)
        // Просто скопируйте остальную часть метода main из вашего кода

        HibernateUtil.shutdown();
    }

    private static void saveUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
        }
    }

    private static User getUserById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, id);
        }
    }

    private static void updateUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(user);
            transaction.commit();
        }
    }

    private static void deleteUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(user);
            transaction.commit();
        }
    }
}