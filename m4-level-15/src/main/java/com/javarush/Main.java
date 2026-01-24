package com.javarush;

import com.javarush.entity.Product;
import com.javarush.entity.User;
import com.javarush.entity.singletable.Admin;
import com.javarush.entity.singletable.Employee;
import com.javarush.entity.singletable.Person;
import com.javarush.entity.singletable.RegularUser;
import com.javarush.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        demonstrateMappedSuperclass();

        demonstrateSingleTableInheritance();

        HibernateUtil.shutdown();

    }

    private static void demonstrateSingleTableInheritance() {
        System.out.println("=== Демонстрация Single Table Inheritance ===");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Очищаем таблицу
            session.createQuery("DELETE FROM Person").executeUpdate();

            // 1. Создаем разные типы пользователей
            RegularUser regular = new RegularUser();
            regular.setUsername("ivan_ivanov");
            regular.setEmail("ivan@mail.ru");
            regular.setProfilePicture("avatar.jpg");
            regular.setLastLogin(java.time.LocalDateTime.now());

            Employee employee = new Employee();
            employee.setUsername("petr_petrov");
            employee.setEmail("petr@company.com");
            employee.setEmployeeId("EMP-001");
            employee.setDepartment("IT");
            employee.setSalary(75000.0);

            Admin admin = new Admin();
            admin.setUsername("admin_system");
            admin.setEmail("admin@company.com");
            admin.setSecurityLevel(5);
            admin.setCanDeleteUsers(true);
            admin.setSuperAdmin(true);

            // 2. Сохраняем все в ОДНУ таблицу
            session.save(regular);
            session.save(employee);
            session.save(admin);

            transaction.commit();
            System.out.println("3 типа пользователей сохранены в одну таблицу!");

            // 3. Демонстрация полиморфного запроса
            System.out.println("\n=== Полиморфный запрос: SELECT * FROM users ===");
            List<Person> allUsers = session.createQuery("FROM Person", Person.class).list();

            System.out.println("Всего пользователей: " + allUsers.size());
            for (Person user : allUsers) {
                System.out.println("ID: " + user.getId() +
                        ", Имя: " + user.getUsername() +
                        ", Тип: " + user.getClass().getSimpleName());
            }

            // 4. Запрос к конкретному подклассу
            System.out.println("\n=== Запрос только сотрудников ===");
            List<Employee> employees = session.createQuery("FROM Employee", Employee.class).list();
            System.out.println("Сотрудников: " + employees.size());
            for (Employee emp : employees) {
                System.out.println("Сотрудник: " + emp.getUsername() +
                        ", Отдел: " + emp.getDepartment() +
                        ", Зарплата: " + emp.getSalary());
            }

            // 5. Показываем структуру таблицы
            System.out.println("\n=== Структура таблицы users ===");
            System.out.println("Все поля в одной таблице:");
            System.out.println("- id, username, email (общие для всех)");
            System.out.println("- user_type (discriminator column)");
            System.out.println("- profile_picture, last_login (только для RegularUser)");
            System.out.println("- employee_id, department, salary (только для Employee)");
            System.out.println("- security_level, can_delete_users, super_admin (только для Admin)");

            // 6. Показываем данные из таблицы (имитация SQL-запроса)
            System.out.println("\n=== Пример данных в таблице ===");
            System.out.println("| id | username        | email               | user_type | profile_picture | employee_id | ...");
            System.out.println("| 1  | ivan_ivanov     | ivan@mail.ru        | REGULAR   | avatar.jpg      | NULL        | ...");
            System.out.println("| 2  | petr_petrov     | petr@company.com    | EMPLOYEE  | NULL            | EMP-001     | ...");
            System.out.println("| 3  | admin_system    | admin@company.com   | ADMIN     | NULL            | NULL        | ...");

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        }
    }
}