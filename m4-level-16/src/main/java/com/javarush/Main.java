package com.javarush;

import com.javarush.entity.slide3.Customer;
import com.javarush.entity.slide4.Product;
import com.javarush.entity.slide5.Employee;
import com.javarush.entity.slide6.Developer;
import com.javarush.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        demonstrateSlide3();
        demonstrateSlide4();
        demonstrateSlide5();
        demonstrateSlide6();
        HibernateUtil.shutdown();
    }

    public static void demonstrateSlide6() {
        System.out.println("\n=== Demo for Slide 6: Complex Conditions with Criteria API ===");

        // Подготовка тестовых данных
        prepareDeveloperData();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            System.out.println("\n--- Complex Query: Find developers who ---");
            System.out.println("(Salary > 80000 OR Position like 'Senior%')");
            System.out.println("AND");
            System.out.println("(Knows Java = true OR Hire Date after 2022-01-01)");

            // Создаем CriteriaQuery
            CriteriaQuery<Developer> query = builder.createQuery(Developer.class);
            Root<Developer> root = query.from(Developer.class);

            // Шаг 1: Создаем отдельные предикаты (как на слайде)
            System.out.println("\n1. Creating individual predicates...");

            // Условие 1: Зарплата больше 80000
            Predicate highSalary = builder.gt(root.get("salary"), 80000.0);
            System.out.println("   Predicate 1: salary > 80000");

            // Условие 2: Должность начинается с "Senior"
            Predicate seniorPosition = builder.like(root.get("position"), "Senior%");
            System.out.println("   Predicate 2: position LIKE 'Senior%'");

            // Условие 3: Знает Java
            Predicate knowsJava = builder.equal(root.get("knowsJava"), true);
            System.out.println("   Predicate 3: knowsJava = true");

            // Условие 4: Дата найма после 2022-01-01
            Predicate recentHire = builder.greaterThan(
                    root.get("hireDate"),
                    LocalDate.of(2022, 1, 1)
            );
            System.out.println("   Predicate 4: hireDate > 2022-01-01");

            // Шаг 2: Комбинируем предикаты в сложные условия
            System.out.println("\n2. Combining predicates with OR...");

            // (Salary > 80000 OR Position like 'Senior%')
            Predicate salaryOrPosition = builder.or(highSalary, seniorPosition);
            System.out.println("   Combined: (salary > 80000 OR position LIKE 'Senior%')");

            // (Knows Java = true OR Hire Date after 2022-01-01)
            Predicate skillsOrRecent = builder.or(knowsJava, recentHire);
            System.out.println("   Combined: (knowsJava = true OR hireDate > 2022-01-01)");

            // Шаг 3: Создаем финальное условие с AND
            System.out.println("\n3. Creating final condition with AND...");
            Predicate finalCondition = builder.and(salaryOrPosition, skillsOrRecent);
            System.out.println("   Final: (salary>80000 OR Senior%) AND (knowsJava=true OR hired>2022)");

            // Шаг 4: Собираем и выполняем запрос
            System.out.println("\n4. Building and executing query...");
            query.select(root).where(finalCondition);

            List<Developer> results = session.createQuery(query).getResultList();

            // Вывод результатов
            System.out.println("\nQuery Result: Found " + results.size() + " developer(s)");
            System.out.println("=".repeat(50));

            for (Developer dev : results) {
                System.out.println(String.format(
                        "  - %-15s | %-12s | $%,8.0f | Java:%-5s | Spring:%-5s | Hired: %s",
                        dev.getName(),
                        dev.getPosition(),
                        dev.getSalary(),
                        dev.getKnowsJava() ? "Yes" : "No",
                        dev.getKnowsSpring() ? "Yes" : "No",
                        dev.getHireDate()
                ));
            }

            // Выводим SQL-подобное представление запроса
            System.out.println("\nEquivalent SQL-like query:");
            System.out.println("SELECT * FROM slide6_developers");
            System.out.println("WHERE (salary > 80000 OR position LIKE 'Senior%')");
            System.out.println("  AND (knows_java = true OR hire_date > '2022-01-01')");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void prepareDeveloperData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Очистка таблицы
            session.createQuery("delete from Developer").executeUpdate();

            // Создание тестовых данных
            Developer d1 = new Developer();
            d1.setName("John Carter");
            d1.setPosition("Senior Backend");
            d1.setSalary(95000.0);
            d1.setHireDate(LocalDate.of(2021, 3, 15));
            d1.setKnowsJava(true);
            d1.setKnowsSpring(true);

            Developer d2 = new Developer();
            d2.setName("Emma Watson");
            d2.setPosition("Middle Frontend");
            d2.setSalary(75000.0);
            d2.setHireDate(LocalDate.of(2022, 6, 10));
            d2.setKnowsJava(false); // Не знает Java
            d2.setKnowsSpring(false);

            Developer d3 = new Developer();
            d3.setName("Alex Turner");
            d3.setPosition("Junior Fullstack");
            d3.setSalary(55000.0);
            d3.setHireDate(LocalDate.of(2023, 1, 20));
            d3.setKnowsJava(true);
            d3.setKnowsSpring(false);

            Developer d4 = new Developer();
            d4.setName("Michael Scott");
            d4.setPosition("Senior DevOps");
            d4.setSalary(110000.0);
            d4.setHireDate(LocalDate.of(2020, 8, 5));
            d4.setKnowsJava(true);
            d4.setKnowsSpring(true);

            Developer d5 = new Developer();
            d5.setName("Sarah Connor");
            d5.setPosition("Lead Architect");
            d5.setSalary(130000.0);
            d5.setHireDate(LocalDate.of(2019, 11, 30));
            d5.setKnowsJava(true);
            d5.setKnowsSpring(true);

            Developer d6 = new Developer();
            d6.setName("Robert Plant");
            d6.setPosition("Middle Backend");
            d6.setSalary(70000.0);
            d6.setHireDate(LocalDate.of(2021, 9, 12));
            d6.setKnowsJava(false); // Не знает Java
            d6.setKnowsSpring(false);

            session.save(d1);
            session.save(d2);
            session.save(d3);
            session.save(d4);
            session.save(d5);
            session.save(d6);

            transaction.commit();
            System.out.println("Test data: 6 developers inserted with varied profiles.");
        }
    }

    public static void demonstrateSlide5() {
        System.out.println("\n=== Demo for Slide 5: Criteria API Examples ===");

        // Подготовка тестовых данных
        prepareEmployeeData();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            System.out.println("\n--- Example 1: Salary > 10000 ---");
            demoExample1(session, builder, 10000.0);

            System.out.println("\n--- Example 2: Salary < 50000 ---");
            demoExample2(session, builder, 50000.0);

            System.out.println("\n--- Example 3: Occupation contains 'Developer' ---");
            demoExample3(session, builder, "%Developer%");

            System.out.println("\n--- Example 4: Salary between 30000 and 80000 ---");
            demoExample4(session, builder, 30000.0, 80000.0);

            System.out.println("\n--- Example 5: Name is NULL ---");
            demoExample5(session, builder);

            System.out.println("\n--- Example 6: Name is NOT NULL ---");
            demoExample6(session, builder);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Пример 1: Зарплата больше указанного значения
    private static void demoExample1(Session session, CriteriaBuilder builder, Double minSalary) {
        CriteriaQuery<Employee> query = builder.createQuery(Employee.class);
        Root<Employee> root = query.from(Employee.class);

        query.select(root)
                .where(builder.gt(root.get("salary"), minSalary));

        List<Employee> results = session.createQuery(query).getResultList();
        System.out.println("Found " + results.size() + " employee(s) with salary > " + minSalary);
        results.forEach(e -> System.out.println("  - " + e.getName() + ", " + e.getOccupation() + ", $" + e.getSalary()));
    }

    // Пример 2: Зарплата меньше указанного значения
    private static void demoExample2(Session session, CriteriaBuilder builder, Double maxSalary) {
        CriteriaQuery<Employee> query = builder.createQuery(Employee.class);
        Root<Employee> root = query.from(Employee.class);

        query.select(root)
                .where(builder.lt(root.get("salary"), maxSalary));

        List<Employee> results = session.createQuery(query).getResultList();
        System.out.println("Found " + results.size() + " employee(s) with salary < " + maxSalary);
        results.forEach(e -> System.out.println("  - " + e.getName() + ", " + e.getOccupation() + ", $" + e.getSalary()));
    }

    // Пример 3: Должность содержит подстроку
    private static void demoExample3(Session session, CriteriaBuilder builder, String pattern) {
        CriteriaQuery<Employee> query = builder.createQuery(Employee.class);
        Root<Employee> root = query.from(Employee.class);

        query.select(root)
                .where(builder.like(root.get("occupation"), pattern));

        List<Employee> results = session.createQuery(query).getResultList();
        System.out.println("Found " + results.size() + " employee(s) with occupation like '" + pattern + "'");
        results.forEach(e -> System.out.println("  - " + e.getName() + ", " + e.getOccupation() + ", $" + e.getSalary()));
    }

    // Пример 4: Зарплата в диапазоне
    private static void demoExample4(Session session, CriteriaBuilder builder, Double from, Double to) {
        CriteriaQuery<Employee> query = builder.createQuery(Employee.class);
        Root<Employee> root = query.from(Employee.class);

        query.select(root)
                .where(builder.between(root.get("salary"), from, to));

        List<Employee> results = session.createQuery(query).getResultList();
        System.out.println("Found " + results.size() + " employee(s) with salary between " + from + " and " + to);
        results.forEach(e -> System.out.println("  - " + e.getName() + ", " + e.getOccupation() + ", $" + e.getSalary()));
    }

    // Пример 5: Имя равно null
    private static void demoExample5(Session session, CriteriaBuilder builder) {
        CriteriaQuery<Employee> query = builder.createQuery(Employee.class);
        Root<Employee> root = query.from(Employee.class);

        query.select(root)
                .where(builder.isNull(root.get("name")));

        List<Employee> results = session.createQuery(query).getResultList();
        System.out.println("Found " + results.size() + " employee(s) with NULL name");
        results.forEach(e -> System.out.println("  - ID: " + e.getId() + ", Occupation: " + e.getOccupation() + ", $" + e.getSalary()));
    }

    // Пример 6: Имя не равно null
    private static void demoExample6(Session session, CriteriaBuilder builder) {
        CriteriaQuery<Employee> query = builder.createQuery(Employee.class);
        Root<Employee> root = query.from(Employee.class);

        query.select(root)
                .where(builder.isNotNull(root.get("name")));

        List<Employee> results = session.createQuery(query).getResultList();
        System.out.println("Found " + results.size() + " employee(s) with NOT NULL name");
        results.forEach(e -> System.out.println("  - " + e.getName() + ", " + e.getOccupation() + ", $" + e.getSalary()));
    }

    private static void prepareEmployeeData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Очистка таблицы
            session.createQuery("delete from Employee").executeUpdate();

            // Создание тестовых данных (все на английском)
            Employee e1 = new Employee();
            e1.setName("John Smith");
            e1.setOccupation("Senior Developer");
            e1.setSalary(120000.0);

            Employee e2 = new Employee();
            e2.setName("Emma Wilson");
            e2.setOccupation("QA Engineer");
            e2.setSalary(65000.0);

            Employee e3 = new Employee();
            e3.setName("Alex Johnson");
            e3.setOccupation("Junior Developer");
            e3.setSalary(45000.0);

            Employee e4 = new Employee();
            e4.setName("Michael Brown");
            e4.setOccupation("Project Manager");
            e4.setSalary(95000.0);

            Employee e5 = new Employee();
            // Намеренно оставляем имя null для демонстрации
            e5.setName(null);
            e5.setOccupation("System Administrator");
            e5.setSalary(75000.0);

            Employee e6 = new Employee();
            e6.setName("Sarah Davis");
            e6.setOccupation("DevOps Engineer");
            e6.setSalary(85000.0);

            Employee e7 = new Employee();
            e7.setName("Robert Taylor");
            e7.setOccupation("Team Lead");
            e7.setSalary(110000.0);

            Employee e8 = new Employee();
            e8.setName("Lisa Anderson");
            e8.setOccupation("Business Analyst");
            e8.setSalary(60000.0);

            session.save(e1);
            session.save(e2);
            session.save(e3);
            session.save(e4);
            session.save(e5);
            session.save(e6);
            session.save(e7);
            session.save(e8);

            transaction.commit();
            System.out.println("Test data: 8 employees inserted (one with NULL name).");
        }
    }

    public static void demonstrateSlide4() {
        System.out.println("\n=== Demo for Slide 4: Constructing a Query Step-by-Step ===");

        // Подготовка тестовых данных
        prepareProductData();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // 1. ПОЛУЧЕНИЕ CRITERIABUILDER (Шаг 1 со слайда)
            System.out.println("\n1. Getting CriteriaBuilder from session...");
            CriteriaBuilder builder = session.getCriteriaBuilder();

            // 2. СОЗДАНИЕ CRITERIAQUERY (Шаг 2 со слайда)
            System.out.println("2. Creating CriteriaQuery<Product>...");
            CriteriaQuery<Product> critQuery = builder.createQuery(Product.class);

            // 3. ОПРЕДЕЛЕНИЕ FROM (часть структуры запроса)
            System.out.println("3. Defining Root<Product> (FROM clause)...");
            Root<Product> root = critQuery.from(Product.class);

            // 4. ЗАДАНИЕ SELECT (часть структуры запроса)
            System.out.println("4. Setting SELECT clause...");
            critQuery.select(root);

            // 5. СОЗДАНИЕ УСЛОВИЙ WHERE с помощью CriteriaBuilder
            System.out.println("5. Creating WHERE conditions using CriteriaBuilder...");

            // Условие 1: category = 'Electronics'
            Predicate categoryPredicate = builder.equal(root.get("category"), "Electronics");

            // Условие 2: price > 500.0
            Predicate pricePredicate = builder.greaterThan(root.get("price"), 500.0);

            // Условие 3: inStock = true
            Predicate stockPredicate = builder.equal(root.get("inStock"), true);

            // Объединяем условия через AND
            Predicate finalCondition = builder.and(categoryPredicate, pricePredicate, stockPredicate);

            // 6. ДОБАВЛЕНИЕ WHERE в запрос
            System.out.println("6. Adding WHERE clause to CriteriaQuery...");
            critQuery.where(finalCondition);

            // 7. СОЗДАНИЕ И ВЫПОЛНЕНИЕ ЗАПРОСА
            System.out.println("7. Creating and executing final Query...");
            org.hibernate.query.Query<Product> query = session.createQuery(critQuery);
            List<Product> results = query.getResultList();

            // 8. ВЫВОД РЕЗУЛЬТАТОВ
            System.out.println("\nQuery Result: Found " + results.size() + " product(s)");
            System.out.println("SQL would be: SELECT * FROM slide4_products WHERE category = 'Electronics' AND price > 500.0 AND in_stock = true");

            for (Product product : results) {
                System.out.println("  - " + product.getName() +
                        " | $" + product.getPrice() +
                        " | " + product.getCategory() +
                        " | In stock: " + product.getInStock());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void prepareProductData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Очистка таблицы
            session.createQuery("delete from Product").executeUpdate();

            // Создание тестовых данных
            Product p1 = new Product();
            p1.setName("Laptop");
            p1.setPrice(1200.0);
            p1.setCategory("Electronics");
            p1.setInStock(true);

            Product p2 = new Product();
            p2.setName("Smartphone");
            p2.setPrice(800.0);
            p2.setCategory("Electronics");
            p2.setInStock(true);

            Product p3 = new Product();
            p3.setName("Headphones");
            p3.setPrice(150.0);
            p3.setCategory("Electronics");
            p3.setInStock(true);

            Product p4 = new Product();
            p4.setName("Tablet");
            p4.setPrice(450.0);
            p4.setCategory("Electronics");
            p4.setInStock(false); // Нет в наличии

            Product p5 = new Product();
            p5.setName("Book");
            p5.setPrice(25.0);
            p5.setCategory("Books");
            p5.setInStock(true);

            session.save(p1);
            session.save(p2);
            session.save(p3);
            session.save(p4);
            session.save(p5);

            transaction.commit();
            System.out.println("Test data: 5 products inserted.");
        }
    }

    public static void demonstrateSlide3() {
        System.out.println("\n=== Demo for Slide 3: Basic Criteria API ===");

        // 1. Сначала подготовим тестовые данные
        prepareTestData();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // 2. Получаем CriteriaBuilder
            CriteriaBuilder builder = session.getCriteriaBuilder();

            // 3. Создаем CriteriaQuery, указывая ожидаемый тип результата (Customer)
            CriteriaQuery<Customer> critQuery = builder.createQuery(Customer.class);

            // 4. Определяем корневую сущность (FROM). Root — это аналог псевдонима в SQL.
            Root<Customer> root = critQuery.from(Customer.class);

            // 5. Указываем, что мы выбираем (SELECT). Здесь выбираем всю сущность.
            critQuery.select(root);

            // 6. Создаем исполняемый Query из CriteriaQuery
            org.hibernate.query.Query<Customer> query = session.createQuery(critQuery);

            // 7. Выполняем запрос и получаем результат
            List<Customer> results = query.getResultList();

            // 8. Выводим результат
            System.out.println("All customers (" + results.size() + "):");
            for (Customer customer : results) {
                System.out.println("  -> ID: " + customer.getId() +
                        ", Name: " + customer.getFullName() +
                        ", City: " + customer.getCity() +
                        ", Points: " + customer.getLoyaltyPoints());
            }
        }
    }

    private static void prepareTestData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Очищаем таблицу перед вставкой (опционально, для чистоты демо)
            session.createQuery("delete from Customer").executeUpdate();

            // Создаем и сохраняем несколько клиентов (только английские строки!)
            Customer c1 = new Customer();
            c1.setFullName("John Smith");
            c1.setCity("New York");
            c1.setLoyaltyPoints(150);

            Customer c2 = new Customer();
            c2.setFullName("Emma Wilson");
            c2.setCity("London");
            c2.setLoyaltyPoints(75);

            Customer c3 = new Customer();
            c3.setFullName("Alex Johnson");
            c3.setCity("Berlin");
            c3.setLoyaltyPoints(200);

            session.save(c1);
            session.save(c2);
            session.save(c3);

            transaction.commit();
            System.out.println("Test data prepared: 3 customers inserted.");
        }
    }
}
