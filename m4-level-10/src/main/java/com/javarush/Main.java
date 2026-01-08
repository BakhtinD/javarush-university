package com.javarush;

import com.javarush.entity.Employee;
import com.javarush.entity.EmployeeTask;
import com.javarush.entity.Product;
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

        // 14. JOIN в HQL - просроченные задачи
        System.out.println("\n14. JOIN в HQL - просроченные задачи");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Подготовка
            Employee emp = new Employee("John", "Dev", 50000);
            session.beginTransaction();
            session.persist(emp);
            session.persist(new EmployeeTask("Task", emp, new Date(), "Overdue"));
            session.getTransaction().commit();

            // Запрос из слайда
            List<Employee> emps = session.createQuery(
                    "select distinct employee from EmployeeTask where deadline < CURRENT_DATE",
                    Employee.class).getResultList();

            System.out.println("Сотрудники с просроченными задачами: " + emps.size());
        }

        // 15. JOIN в HQL - обновление задач
        System.out.println("\n15. JOIN в HQL - обновление");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // 1. Подготовка
            Employee director = new Employee("Director", "CEO", 0);
            session.beginTransaction();
            session.persist(director);
            session.persist(new EmployeeTask("Task", null, new Date(), "New"));
            session.getTransaction().commit();

            // 2. UPDATE (запрос из слайда)
            session.beginTransaction();
            int count = session.createQuery(
                            "update EmployeeTask set employee = :d where employee is null")
                    .setParameter("d", director)
                    .executeUpdate();
            session.getTransaction().commit();

            System.out.println("Задач назначено на директора: " + count);
        }

        // 16. Параметры в HQL
        System.out.println("\n16. Параметры в HQL");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Подготовка
            Employee emp = new Employee("Ivan", "Dev", 0);
            session.beginTransaction();
            session.persist(emp);
            session.persist(new EmployeeTask("Task", emp, new Date(), "New"));
            session.getTransaction().commit();

            // Параметр из слайда
            List<EmployeeTask> tasks = session.createQuery(
                            "from EmployeeTask where employee.name = :username", EmployeeTask.class)
                    .setParameter("username", "Ivan")
                    .getResultList();

            System.out.println("Найдено: " + tasks.size());
        }

        // 17. Параметр-список
        System.out.println("\n17. setParameterList");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // 1 строка подготовки
            session.beginTransaction();
            session.persist(new Employee("Dev", "Developer", 0));
            session.getTransaction().commit();

            // 1 строка запроса
            int count = session.createQuery("from Employee where position in (:list)", Employee.class)
                    .setParameterList("list", new String[]{"Developer", "Tester"})
                    .getResultList().size();

            System.out.println("Найдено: " + count);
        }

        // 18. Защита от SQL-инъекций
        System.out.println("\n18. Защита от SQL-инъекций");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Подготовка
            session.beginTransaction();
            session.persist(new User("Ivan", "ivan@test.com", 1));
            session.getTransaction().commit();

            // Пример опасного ввода
            String dangerousInput = "Ivan'; DROP TABLE users; --";

            System.out.println("\nОпасный ввод: \"" + dangerousInput + "\"");
            System.out.println("\nС параметром - безопасно:");

            // Безопасно с параметром
            List<User> users = session.createQuery("from User where name = :name", User.class)
                    .setParameter("name", dangerousInput)
                    .getResultList();

            System.out.println("Найдено пользователей: " + users.size());
            System.out.println("Таблица users не удалена - параметры защитили!");
        }

        // 19. Пагинация
        System.out.println("\n19. Пагинация");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Быстрая подготовка
            session.beginTransaction();
            session.persist(new User("A", "a@test.com", 1));
            session.persist(new User("B", "b@test.com", 2));
            session.getTransaction().commit();

            // Пагинация
            List<User> users = session.createQuery("from User", User.class)
                    .setFirstResult(1)
                    .setMaxResults(1)
                    .getResultList();

            System.out.println("Вторая запись: " + users.get(0).getName());
        }

        // 20. Сортировка
        System.out.println("\n20. Сортировка");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Очистить и создать заново
            session.beginTransaction();
            session.createQuery("delete from User").executeUpdate();
            session.persist(new User("Zebra", "z@test.com", 3));
            session.persist(new User("Apple", "a@test.com", 1));
            session.persist(new User("Banana", "b@test.com", 2));
            session.getTransaction().commit();

            // Сортировка
            System.out.println("\nОтсортировано по имени:");
            session.createQuery("from User order by name", User.class)
                    .getResultList().forEach(u -> System.out.println("  " + u.getName()));
        }

        // 21. Агрегатные функции
        System.out.println("\n21. Агрегатные функции");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Просто добавляем еще сотрудников без удаления старых
            session.beginTransaction();
            session.persist(new Employee("NewEmp1", "Dev", 500));
            session.persist(new Employee("NewEmp2", "Manager", 700));
            session.getTransaction().commit();

            System.out.println("Всего сотрудников: " +
                    session.createQuery("select count(*) from Employee", Long.class).getSingleResult());

            System.out.println("Средняя зарплата: $" +
                    session.createQuery("select avg(salary) from Employee", Double.class).getSingleResult());
        }

        // 22. NamedQueries
        System.out.println("\n22. NamedQueries");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // Просто добавляем продукт
            session.beginTransaction();
            session.persist(new Product("Demo", "Demo", 1.0, 1));
            session.getTransaction().commit();

            // NamedQuery
            int count = session.createNamedQuery("Product.findAll", Product.class)
                    .getResultList().size();

            System.out.println("NamedQuery вернул: " + count + " продукт");
        }

        // 23. NativeQuery
        System.out.println("\n23. NativeQuery");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            System.out.println("\nNativeQuery - чистый SQL:");
            System.out.println("session.createNativeQuery(\"SELECT * FROM users\", User.class)");

            // Простой пример
            List<User> users = session.createNativeQuery("SELECT * FROM users", User.class)
                    .getResultList();

            System.out.println("Результат: " + users.size() + " строк из БД");
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
