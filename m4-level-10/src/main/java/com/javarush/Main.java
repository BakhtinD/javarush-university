package com.javarush;

import com.javarush.entity.Employee;
import com.javarush.entity.EmployeeTask;
import com.javarush.entity.User;
import com.javarush.util.HibernateUtil;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;


import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

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

        // 6. Демонстрация HQL (простой запрос "FROM")
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // 1. Создаем объект Query, используя HQL-строку.
            Query<User> query = session.createQuery("from User", User.class);

            // 2. Выполняем запрос и получаем результат в виде списка (List).
            List<User> allUsers = query.getResultList();

            // 3. Работаем с результатом.
            System.out.println("Найдено пользователей: " + allUsers.size());
            for (User user : allUsers) {
                System.out.println("  -> " + user);
            }
        }

        // 7. Демонстрация HQL С SELECT
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Запрос возвращает список String, а не User
            Query<String> query = session.createQuery("select name from User", String.class);
            List<String> names = query.getResultList();

            System.out.println("Все имена пользователей (" + names.size() + "):");
            for (String name : names) {
                System.out.println("  -> " + name);
            }
        }

        // 8. ДЕМОНСТРАЦИЯ getSingleResult()
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // Пример 1: Успешный поиск по уникальному значению
            System.out.println("\nПример 1: Поиск пользователя по уникальному email");
            Query<User> query1 = session.createQuery(
                    "from User where email = :email", User.class);
            query1.setParameter("email", "alice@example.com");

            User user1 = query1.getSingleResult();
            System.out.println("  Найден пользователь: " + user1.getName());
            System.out.println("  Email: " + user1.getEmail());
            System.out.println("  Уровень: " + user1.getLevel());
        }


        // 9. Демонстрация stream() и getResultStream()
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("from User", User.class);

            // a) Использование stream()
            query.stream()
                    .limit(3) // Берем только первых 3 пользователя
                    .forEach(user -> System.out.println("   -> " + user.getName() + " (уровень: " + user.getLevel() + ")"));

            // b) Использование getResultStream() (JPA стандарт)
            query.getResultStream()
                    .filter(user -> user.getLevel() > 10)
                    .forEach(user -> System.out.println("   -> " + user.getName() + " (уровень > 10)"));

            // c) Преобразование Stream в List:
            List<String> names = query.getResultStream()
                    .map(User::getName)
                    .sorted()
                    .collect(Collectors.toList());
            System.out.println("   Отсортированные имена: " + String.join(", ", names));
        }

        // 10. Демонстрация executeUpdate()
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            Transaction transaction = session.beginTransaction();

            // a) Обновление всех пользователей (увеличиваем уровень на 1)
            Query updateQuery = session.createQuery("update User set level = level + 1");
            int updatedCount = updateQuery.executeUpdate();
            System.out.println("   Обновлено записей: " + updatedCount);

            // b) Удаление пользователей с уровнем < 10
            Query deleteQuery = session.createQuery("delete from User where level < 10");
            int deletedCount = deleteQuery.executeUpdate();
            System.out.println("   Удалено записей: " + deletedCount);

            // c) Проверка результатов
            Query<User> selectQuery = session.createQuery("from User", User.class);
            List<User> remainingUsers = selectQuery.getResultList();
            System.out.println("   Осталось пользователей: " + remainingUsers.size());
            remainingUsers.forEach(user ->
                    System.out.println("   -> " + user.getName() + " (уровень: " + user.getLevel() + ")"));

            transaction.commit();

        }

        // 11. Демонстрация scroll()
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("from User", User.class);

            System.out.println("Пример scroll():");
            try (ScrollableResults scroll = query.scroll(ScrollMode.FORWARD_ONLY)) {
                while (scroll.next()) {
                    User user = (User) scroll.get(0); // Приведение типа
                    System.out.println("  -> " + user.getName());
                }
            }

            System.out.println("\nАналог с getResultList():");
            List<User> users = query.getResultList();
            users.forEach(user -> System.out.println("  -> " + user.getName()));
        }

        // 12. JOIN в HQL
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Быстрая подготовка данных
            Employee emp = new Employee("Ivan", "Dev", 100000);
            EmployeeTask task = new EmployeeTask("Test", emp, new Date(), "New");

            session.beginTransaction();
            session.persist(emp);
            session.persist(task);
            session.getTransaction().commit();

            // Простой JOIN
            System.out.println("\nПример JOIN:");
            Query<EmployeeTask> query = session.createQuery(
                    "from EmployeeTask t join fetch t.employee", EmployeeTask.class);

            query.getResultList().forEach(t ->
                    System.out.println(t.getName() + " -> " + t.getEmployee().getName())
            );
        }

        // 13. JOIN в HQL
        System.out.println("\n13. JOIN в HQL");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // Подготовка
            Employee emp = new Employee("Ivan", "Manager", 100000);
            session.beginTransaction();
            session.persist(emp);
            session.persist(new EmployeeTask("Task", emp, new Date(), "New"));
            session.getTransaction().commit();

            // Демонстрация из слайда
            System.out.println("\nИз слайда:");
            System.out.println("HQL:  from EmployeeTask where employee.name = \"Ivan\"");

            int count = session.createQuery(
                            "from EmployeeTask where employee.name = 'Ivan'", EmployeeTask.class)
                    .getResultList().size();

            System.out.println("Результат: " + count + " задача");
            System.out.println("\nHibernate сам добавил JOIN в SQL!");
        }

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
