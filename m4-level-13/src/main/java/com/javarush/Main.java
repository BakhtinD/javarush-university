package com.javarush;

import com.javarush.entity.*;
import com.javarush.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        demonstrateSlide3();

        demonstrateSlide4();

        demonstrateSlide5();

        demonstrateSlide7();

        demonstrateSlide6();

        demonstrateSlide8();

        demonstrateSlide9();

        demonstrateSlide10();

        demonstrateSlide11();

        demonstrateSlide12();

        demonstrateSlide13();

        HibernateUtil.shutdown();
    }

    private static void demonstrateSlide13() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            System.out.println("\n✅ Односторонний OneToOne Example");

            // 1. Создаём водителей
            Driver driver1 = new Driver("Иван Иванов", "DL123456");
            Driver driver2 = new Driver("Петр Петров", "DL789012");

            session.save(driver1);
            session.save(driver2);

            // 2. Создаём транспортные средства
            Vehicle car1 = new Vehicle("Toyota Camry", "A123BC", LocalDate.of(2022, 5, 15));
            Vehicle car2 = new Vehicle("Honda Civic", "B456DE", LocalDate.of(2023, 3, 20));

            // 3. Назначаем водителей (односторонняя связь)
            car1.setDriver(driver1); // Эта машина закреплена за Иваном
            car2.setDriver(driver2); // Эта машина закреплена за Петром

            // 4. Сохраняем транспортные средства
            session.save(car1);
            session.save(car2);

            tx.commit();

            System.out.println("\n📊 Данные сохранены:");
            System.out.println("   Водитель 1: " + driver1.getName() + " (лицензия: " + driver1.getLicenseNumber() + ")");
            System.out.println("   Водитель 2: " + driver2.getName() + " (лицензия: " + driver2.getLicenseNumber() + ")");
            System.out.println("   Машина 1: " + car1.getModel() + " (" + car1.getLicensePlate() + ")");
            System.out.println("   Машина 2: " + car2.getModel() + " (" + car2.getLicensePlate() + ")");

            // 5. Демонстрация односторонней связи
            System.out.println("\n🔍 Односторонняя связь:");
            System.out.println("   Машина знает своего водителя: " + car1.getDriver().getName());
            System.out.println("   Но водитель не знает свою машину (у Driver нет поля Vehicle)");

            // 6. Показываем структуру БД
            System.out.println("\n📈 Структура базы данных:");
            System.out.println("   Таблица drivers:");
            System.out.println("     id | name | license_number");
            System.out.println("   Таблица vehicles:");
            System.out.println("     id | model | license_plate | driver_id (UNIQUE FK) | registration_date");
            System.out.println("   Ограничение UNIQUE на driver_id гарантирует 1:1 связь");

            // 7. Проверяем, что нельзя назначить одного водителя на две машины
            try {
                tx = session.beginTransaction();
                Vehicle car3 = new Vehicle("Ford Focus", "C789FG", LocalDate.now());
                car3.setDriver(driver1); // Пытаемся назначить того же водителя
                session.save(car3);
                tx.commit();
                System.out.println("\n❌ ОШИБКА: Должна была быть ошибка UNIQUE constraint!");
            } catch (Exception e) {
                System.out.println("\n✅ Правильно: Получили ошибку - один водитель не может вести две машины");
                System.out.println("   Сообщение: " + e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void demonstrateSlide12() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            System.out.println("\n✅ @Embedded Example");

            // Создаём встроенные объекты
            Address address = new Address(
                    "123 Tech Street",
                    "San Francisco",
                    "94107",
                    "USA"
            );

            ContactInfo contactInfo = new ContactInfo(
                    "info@techcorp.com",
                    "+1-555-1234",
                    "www.techcorp.com"
            );

            // Создаём компанию с встроенными объектами
            Company company = new Company("TechCorp Inc.", address, contactInfo);

            // Сохраняем (все поля сохранятся в одной таблице)
            session.save(company);

            tx.commit();

            System.out.println("\n💾 Company saved with embedded objects:");
            System.out.println("   ID: " + company.getId());
            System.out.println("   Name: " + company.getName());
            System.out.println("   Address: " + company.getAddress());
            System.out.println("   Contact: " + company.getContactInfo());

            // Загружаем обратно и проверяем
            session.clear();
            Company loadedCompany = session.get(Company.class, company.getId());

            System.out.println("\n📥 Loaded from database:");
            System.out.println("   Street: " + loadedCompany.getAddress().getStreet());
            System.out.println("   City: " + loadedCompany.getAddress().getCity());
            System.out.println("   Email: " + loadedCompany.getContactInfo().getEmail());
            System.out.println("   Phone: " + loadedCompany.getContactInfo().getPhone());

            // Показываем SQL структуру
            System.out.println("\n📊 Table 'companies' structure:");
            System.out.println("   id (PK)");
            System.out.println("   name");
            System.out.println("   street, city, zip_code, country ← Address fields");
            System.out.println("   contact_email, contact_phone, company_website ← ContactInfo fields");
            System.out.println("   (Все в одной таблице!)");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void demonstrateSlide11() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            System.out.println("\n✅ @OneToOne Examples (New Entities)");

            // === 1. Односторонний OneToOne ===
            System.out.println("\n1. Односторонний OneToOne (Passport → Person):");
            Person person1 = new Person("John Doe");
            session.save(person1);

            Passport passport = new Passport("AB123456", "USA");
            passport.setPerson(person1);
            session.save(passport);

            System.out.println("   Person: " + person1.getName());
            System.out.println("   Passport: " + passport.getNumber());
            System.out.println("   Passport knows person: " + (passport.getPerson() != null));
            System.out.println("   Person knows passport: " + (person1.getPassport() == null ? "No" : "Yes"));

            // === 2. Двусторонний OneToOne ===
            System.out.println("\n2. Двусторонний OneToOne (Person ↔ Passport):");
            Person person2 = new Person("Alice Smith");
            Passport passport2 = new Passport("CD789012", "Canada");

            // Устанавливаем связь с двух сторон
            person2.setPassport(passport2);
            passport2.setPerson(person2);

            session.save(person2); // passport2 сохранится каскадно

            System.out.println("   Person: " + person2.getName());
            System.out.println("   Passport country: " + passport2.getCountry());
            System.out.println("   Person knows passport: " + (person2.getPassport() != null));

            // === 3. @MapsId ===
            System.out.println("\n3. @MapsId (Car ↔ Engine with shared ID):");
            Car car = new Car("Tesla Model 3", "Red");
            session.save(car);

            Engine engine = new Engine("Electric", 450);
            engine.setCar(car); // ID установится автоматически из car.id

            session.save(engine);

            System.out.println("   Car ID: " + car.getId());
            System.out.println("   Engine ID: " + engine.getId());
            System.out.println("   IDs are equal: " + car.getId().equals(engine.getId()));
            System.out.println("   Car model: " + car.getModel());
            System.out.println("   Engine type: " + engine.getType());

            tx.commit();

            // Проверяем структуру БД
            System.out.println("\n📊 Database tables created:");
            System.out.println("   - persons (id, name)");
            System.out.println("   - passports (id, number, country, person_id) ← FK");
            System.out.println("   - cars (id, model, color)");
            System.out.println("   - engines (id, type, horsepower) ← id = FK to cars.id");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void demonstrateSlide10() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            // 1. Создаём проекты
            Project webApp = new Project("Web Application");
            Project mobileApp = new Project("Mobile App");

            // 2. Создаём разработчиков
            Developer alice = new Developer("Alice");
            Developer bob = new Developer("Bob");
            Developer charlie = new Developer("Charlie");

            // 3. Сохраняем разработчиков (явно)
            session.save(alice);
            session.save(bob);
            session.save(charlie);

            // 4. Устанавливаем связи
            webApp.getDevelopers().add(alice);
            webApp.getDevelopers().add(bob);

            mobileApp.getDevelopers().add(bob);
            mobileApp.getDevelopers().add(charlie);

            // 5. Сохраняем проекты (разработчики уже сохранены)
            session.save(webApp);
            session.save(mobileApp);

            tx.commit();

            System.out.println("\n✅ @ManyToMany with @JoinTable Example");
            System.out.println("Project '" + webApp.getName() + "' has developers: " +
                    webApp.getDevelopers().size());
            System.out.println("Project '" + mobileApp.getName() + "' has developers: " +
                    mobileApp.getDevelopers().size());
            System.out.println("Developer 'Bob' works on projects: " + bob.getProjects().size());

            // Проверяем промежуточную таблицу через запрос
            List<Object[]> results = session.createNativeQuery(
                    "SELECT project_id, developer_id FROM project_developer ORDER BY project_id"
            ).list();

            System.out.println("\n📊 Intermediate table 'project_developer' contents:");
            for (Object[] row : results) {
                System.out.println("  Project ID: " + row[0] + ", Developer ID: " + row[1]);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void demonstrateSlide9() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            // 1. Создаём теги
            Tag javaTag = new Tag("Java");
            Tag springTag = new Tag("Spring");
            Tag hibernateTag = new Tag("Hibernate");

            // 2. Создаём статьи (НЕ сохраняем явно)
            Article article1 = new Article("Getting Started with Java");
            Article article2 = new Article("Spring Boot Tutorial");

            // 3. Устанавливаем связи
            javaTag.getArticles().add(article1);
            springTag.getArticles().add(article2);
            hibernateTag.getArticles().add(article1);
            hibernateTag.getArticles().add(article2);

            // 4. Сохраняем только теги (статьи сохранятся каскадно)
            session.save(javaTag);
            session.save(springTag);
            session.save(hibernateTag);

            tx.commit();

            System.out.println("✅ @ManyToMany Example (Articles & Tags)");
            System.out.println("Tag 'Java' has articles: " + javaTag.getArticles().size());
            System.out.println("Tag 'Hibernate' has articles: " + hibernateTag.getArticles().size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void demonstrateSlide8() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            // 1. Создаём университет
            University university = new University("JavaRush University");

            // 2. Создаём студентов и добавляем в коллекцию
            Student student1 = new Student("Alex");
            Student student2 = new Student("Maria");

            university.getStudents().add(student1);
            university.getStudents().add(student2);

            // 3. Сохраняем университет (студенты сохранятся каскадно)
            session.save(university);

            tx.commit();

            System.out.println("✅ @OneToMany Example");
            System.out.println("University: " + university.getName());
            System.out.println("Students count: " + university.getStudents().size());
            university.getStudents().forEach(s -> System.out.println("  - " + s.getName()));

        }
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
            // Важно! Обязательно устанавливаем связь с обеих сторон
            author.getBooks().add(book1);
            author.getBooks().add(book2);

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

    private static void demonstrateSlide6() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            // 1. Создаём категорию
            Category electronics = new Category("Electronics");
            session.save(electronics);

            // 2. Создаём продукты с @ManyToOne связью
            Product laptop = new Product("Laptop");
            Product phone = new Product("Phone");

            laptop.setCategory(electronics);  // Many-to-One связь
            phone.setCategory(electronics);   // Многие продукты → одна категория

            session.save(laptop);
            session.save(phone);

            tx.commit();

            System.out.println("✅ @ManyToOne Example");
            System.out.println("Category: " + electronics.getName());
            System.out.println("Products in category:");
            System.out.println("  - " + laptop.getName());
            System.out.println("  - " + phone.getName());

            // Проверяем связь
            Product savedLaptop = session.get(Product.class, laptop.getId());
            System.out.println("Laptop category: " + savedLaptop.getCategory().getName());

        }
    }

    private static void demonstrateSlide7() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Создаём тестовые данные
            Worker worker1 = new Worker("John Smith");
            Worker worker2 = new Worker("Alice Johnson");
            Worker director = new Worker("Director Bob");

            session.save(worker1);
            session.save(worker2);
            session.save(director);

            // Задачи для worker1
            Task task1 = new Task("Fix bugs", LocalDate.of(2024, 1, 15));
            Task task2 = new Task("Write docs", LocalDate.of(2024, 2, 20));
            task1.setWorker(worker1);
            task2.setWorker(worker1);

            // Просроченная задача для worker2
            Task task3 = new Task("Update server", LocalDate.of(2023, 12, 1));
            task3.setWorker(worker2);

            // Неназначенная задача
            Task task4 = new Task("Plan meeting", LocalDate.of(2024, 3, 1));
            // worker не установлен - задача неназначенная

            session.save(task1);
            session.save(task2);
            session.save(task3);
            session.save(task4);

            transaction.commit();

            System.out.println("\n✅ Slide 7: HQL Query Examples");
            System.out.println("Test data created.");

            // ========== ЗАПРОС 1 ==========
            // Все задачи, назначенные на John Smith
            System.out.println("\n1. Все задачи John Smith:");
            Query<Task> query1 = session.createQuery(
                    "from Task where worker.name = :workerName", Task.class
            );
            query1.setParameter("workerName", "John Smith");
            List<Task> johnsTasks = query1.list();
            johnsTasks.forEach(t -> System.out.println("  - " + t.getDescription()));

            // ========== ЗАПРОС 2 ==========
            // Сотрудники с просроченными задачами
            System.out.println("\n2. Сотрудники с просроченными задачами:");
            Query<Worker> query2 = session.createQuery(
                    "select distinct worker from Task where deadline < current_date", Worker.class
            );
            List<Worker> workersWithOverdue = query2.list();
            workersWithOverdue.forEach(w -> System.out.println("  - " + w.getName()));

            // ========== ЗАПРОС 3 ==========
            // Назначаем все неназначенные задачи на директора
            System.out.println("\n3. Назначаем неназначенные задачи на директора...");
            transaction = session.beginTransaction();

            Query<?> query3 = session.createQuery(
                    "update Task set worker = :director where worker is null"
            );
            query3.setParameter("director", director);
            int updatedCount = query3.executeUpdate();

            transaction.commit();
            System.out.println("   Назначено задач: " + updatedCount);

            // Проверяем результат
            Query<Task> query4 = session.createQuery(
                    "from Task where worker.name = 'Director Bob'", Task.class
            );
            List<Task> directorsTasks = query4.list();
            System.out.println("   Теперь у директора задач: " + directorsTasks.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
