package com.javarush;

import com.javarush.entity.Product;
import com.javarush.entity.User;
import com.javarush.entity.discriminator.Contract;
import com.javarush.entity.discriminator.Document;
import com.javarush.entity.discriminator.Invoice;
import com.javarush.entity.discriminator.Report;
import com.javarush.entity.singletable.Admin;
import com.javarush.entity.singletable.Employee;
import com.javarush.entity.singletable.Person;
import com.javarush.entity.singletable.RegularUser;
import com.javarush.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        demonstrateMappedSuperclass();

        demonstrateSingleTableInheritance();

        demonstrateDiscriminator();

        HibernateUtil.shutdown();

    }

    private static void demonstrateDiscriminator() {
        System.out.println("=== Демонстрация дискриминаторов (разные типы) ===");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Очищаем таблицу documents
            session.createQuery("DELETE FROM Document").executeUpdate();

            System.out.println("\n1. Создаем документы разных типов со строковым дискриминатором:");

            // Счет
            Invoice invoice = new Invoice();
            invoice.setTitle("Bill for services");
            invoice.setAuthor("Accounting");
            invoice.setCreatedDate(java.time.LocalDate.now());
            invoice.setFileSize(2048L);
            invoice.setInvoiceNumber("INV-2024-001");
            invoice.setTotalAmount(new BigDecimal("15000.00"));
            invoice.setClientName("LLC \"Chamomile\"");
            invoice.setDueDate(java.time.LocalDate.now().plusDays(30));

            // Договор
            Contract contract = new Contract();
            contract.setTitle("Lease agreement");
            contract.setAuthor("Legal Department");
            contract.setCreatedDate(java.time.LocalDate.now());
            contract.setFileSize(5120L);
            contract.setPartyA("LLC \"Lessor\"");
            contract.setPartyB("IP Ivanov I.I.");
            contract.setValidFrom(java.time.LocalDate.now());
            contract.setValidTo(java.time.LocalDate.now().plusYears(1));
            contract.setSignatureDate(java.time.LocalDate.now());

            // Отчет
            Report report = new Report();
            report.setTitle("Financial report for the 1st quarter of 2024");
            report.setAuthor("Finance Department");
            report.setCreatedDate(java.time.LocalDate.now());
            report.setFileSize(10240L);
            report.setPeriod("2024-Q1");
            report.setPagesCount(25);
            report.setHasCharts(true);
            report.setApprovedBy("Director");

            session.save(invoice);
            session.save(contract);
            session.save(report);

            transaction.commit();
            System.out.println("✅ Документы сохранены в таблицу 'documents'");

            // Показываем структуру таблицы
            System.out.println("\n2. Структура таблицы 'documents':");
            System.out.println("   Дискриминаторная колонка: doc_type (VARCHAR)");
            System.out.println("   Значения: INVOICE, CONTRACT, REPORT");

            // Запрос всех документов
            System.out.println("\n3. Запрос всех документов:");
            List<Document> allDocuments = session.createQuery(
                            "FROM Document ORDER BY createdDate", Document.class)
                    .list();

            for (Document doc : allDocuments) {
                String discriminatorValue = "";
                if (doc instanceof Invoice) discriminatorValue = "INVOICE";
                else if (doc instanceof Contract) discriminatorValue = "CONTRACT";
                else if (doc instanceof Report) discriminatorValue = "REPORT";

                System.out.printf("   [%s] %s (%s, %d байт)%n",
                        discriminatorValue,
                        doc.getTitle(),
                        doc.getAuthor(),
                        doc.getFileSize());
            }

            // Запрос только определенного типа
            System.out.println("\n4. Запрос только счетов (INVOICE):");
            List<Invoice> invoices = session.createQuery(
                            "FROM Invoice", Invoice.class)
                    .list();

            for (Invoice inv : invoices) {
                System.out.printf("   Счет №%s: %,.2f руб. для %s%n",
                        inv.getInvoiceNumber(),
                        inv.getTotalAmount(),
                        inv.getClientName());
            }

            // Демонстрация разных типов дискриминаторов
            System.out.println("\n5. Примеры разных типов дискриминаторов:");
            System.out.println("   а) STRING (по умолчанию):");
            System.out.println("      @DiscriminatorColumn(name=\"type\", discriminatorType = DiscriminatorType.STRING)");
            System.out.println("      @DiscriminatorValue(\"INVOICE\")");

            System.out.println("\n   б) INTEGER:");
            System.out.println("      @DiscriminatorColumn(name=\"type_code\", discriminatorType = DiscriminatorType.INTEGER)");
            System.out.println("      @DiscriminatorValue(\"1\") // для Invoice");
            System.out.println("      @DiscriminatorValue(\"2\") // для Contract");

            System.out.println("\n   в) CHAR:");
            System.out.println("      @DiscriminatorColumn(name=\"type_char\", discriminatorType = DiscriminatorType.CHAR, length = 1)");
            System.out.println("      @DiscriminatorValue(\"I\") // для Invoice");
            System.out.println("      @DiscriminatorValue(\"C\") // для Contract");

            // Демонстрация с числовыми значениями
            System.out.println("\n6. Преимущества числового дискриминатора:");
            System.out.println("   - Экономия места (INT vs VARCHAR)");
            System.out.println("   - Более быстрые сравнения");
            System.out.println("   - Меньше вероятность ошибок при вводе");

            System.out.println("\n7. Преимущества строкового дискриминатора:");
            System.out.println("   - Человекочитаемые значения");
            System.out.println("   - Легче отлаживать в SQL");
            System.out.println("   - Не требует конвертации");

            // Показываем SQL-запрос
            System.out.println("\n8. Как выглядит SQL-запрос:");
            System.out.println("   SELECT id, title, author, doc_type, ... FROM documents");
            System.out.println("   WHERE doc_type IN ('INVOICE', 'CONTRACT', 'REPORT')");

            // Динамическое определение типа
            System.out.println("\n9. Определение типа во время выполнения:");
            Document firstDoc = session.createQuery(
                            "FROM Document ORDER BY id", Document.class)
                    .setMaxResults(1)
                    .uniqueResult();

            if (firstDoc != null) {
                String type = firstDoc.getClass().getSimpleName();
                System.out.printf("   Первый документ: %s (тип: %s)%n",
                        firstDoc.getTitle(), type);

                // Пример использования instanceof с дискриминатором
                if (firstDoc instanceof Invoice) {
                    System.out.println("   Это счет, можно получить invoiceNumber");
                } else if (firstDoc instanceof Contract) {
                    System.out.println("   Это договор, можно получить partyA");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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