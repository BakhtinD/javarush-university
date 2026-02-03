package com.javarush;

import com.javarush.entity.slide10.ProductStock;
import com.javarush.entity.slide12.Person;
import com.javarush.entity.slide13.Book;
import com.javarush.entity.slide14.Author;
import com.javarush.entity.slide14.BookDetail;
import com.javarush.entity.slide3.Customer;
import com.javarush.entity.slide4.Product;
import com.javarush.entity.slide5.Employee;
import com.javarush.entity.slide6.Developer;
import com.javarush.entity.slide7.Account;
import com.javarush.entity.slide8.Project;
import com.javarush.entity.slide9.SalesRecord;
import com.javarush.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        demonstrateSlide3();

        demonstrateSlide4();

        demonstrateSlide5();

        demonstrateSlide6();

        demonstrateSlide7();

        demonstrateSlide8();

        demonstrateSlide9();

        demonstrateSlide10();

        demonstrateSlide12();

        demonstrateSlide13();

        demonstrateSlide14();
        
        HibernateUtil.shutdown();
    }


    public static void demonstrateSlide14() {
        System.out.println("\n=== Demo for Slide 14: NativeQuery with Multiple Entity Mapping ===");
        System.out.println("Mapping result to multiple entities in one query (as shown on slide)");

        // Подготовка тестовых данных
        prepareAuthorBookData();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            System.out.println("\n--- Example 1: Basic multi-entity mapping (like on slide) ---");
            demoBasicMultiEntityMapping(session);

            System.out.println("\n--- Example 2: Selective column loading ---");
            demoSelectiveColumnLoading(session);

            System.out.println("\n--- Example 3: Overcoming Lazy Loading ---");
            demoOvercomingLazyLoading(session);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Пример 1: Базовый маппинг нескольких сущностей (как на слайде)
    private static void demoBasicMultiEntityMapping(Session session) {
        System.out.println("SQL with {alias.*} syntax and addEntity()/addJoin():");
        System.out.println("SELECT {b.*}, {a.*} FROM slide14_book_details b");
        System.out.println("JOIN slide14_authors a ON b.author_id = a.id");

        // Нативный запрос с маппингом на две сущности
        List<BookDetail> books = session.createNativeQuery(
                        "SELECT {b.*}, {a.*} " +
                                "FROM slide14_book_details b " +
                                "JOIN slide14_authors a ON b.author_id = a.id " +
                                "WHERE b.price > 30"
                )
                .addEntity("b", BookDetail.class)
                .addJoin("a", "b.author")
                .list();

        System.out.println("\nResults: " + books.size() + " books with authors loaded");
        System.out.println("First book: \"" + books.get(0).getTitle() + "\"");
        System.out.println("Author: " + books.get(0).getAuthor().getFullName());
        System.out.println("Author email: " + books.get(0).getAuthor().getEmail());

        // Проверяем, что связь действительно заполнена
        System.out.println("\nChecking that author association is properly loaded:");
        for (BookDetail book : books) {
            if (book.getAuthor() != null && book.getAuthor().getFullName() != null) {
                System.out.println("✓ " + book.getTitle() + " -> " + book.getAuthor().getFullName());
            }
        }
    }

    // Пример 2: Селективная загрузка только нужных колонок
    private static void demoSelectiveColumnLoading(Session session) {
        System.out.println("\nSelective loading - only specific columns:");
        System.out.println("SELECT b.id, b.title, b.price, a.full_name, a.nationality");
        System.out.println("FROM slide14_book_details b");
        System.out.println("JOIN slide14_authors a ON b.author_id = a.id");

        // Вместо {alias.*} указываем конкретные колонки
        List<Object[]> results = session.createNativeQuery(
                "SELECT b.id as book_id, b.title, b.price, " +
                        "a.id as author_id, a.full_name, a.nationality " +
                        "FROM slide14_book_details b " +
                        "JOIN slide14_authors a ON b.author_id = a.id " +
                        "WHERE a.nationality = 'American'"
        ).list();

        System.out.println("\nSelective results (Object[] arrays):");
        System.out.println("=".repeat(70));
        System.out.println(String.format("%-8s | %-25s | %-8s | %-20s | %-15s",
                "Book ID", "Title", "Price", "Author Name", "Nationality"));
        System.out.println("-".repeat(70));

        for (Object[] row : results) {
            Long bookId = ((Number) row[0]).longValue();
            String title = (String) row[1];
            BigDecimal price = (BigDecimal) row[2];
            Long authorId = ((Number) row[3]).longValue();
            String authorName = (String) row[4];
            String nationality = (String) row[5];

            System.out.println(String.format("%-8d | %-25s | $%-7.2f | %-20s | %-15s",
                    bookId, title, price, authorName, nationality));
        }
    }

    // Пример 3: Преодоление Lazy Loading
    private static void demoOvercomingLazyLoading(Session session) {
        System.out.println("\n--- Testing Lazy Loading issue ---");

        // Сначала получаем автора обычным способом (будет Lazy Loading для книг)
        Author author = session.createQuery(
                        "FROM Author a WHERE a.fullName = :name", Author.class)
                .setParameter("name", "Stephen King")
                .uniqueResult();

        System.out.println("1. Regular HQL query for author:");
        System.out.println("   Author: " + author.getFullName());
        System.out.println("   Books collection initialized: " +
                (author.getBooks() != null ? "Yes" : "No"));
        System.out.println("   But books are LAZY loaded - need separate queries");

        System.out.println("\n2. NativeQuery with multi-entity mapping:");
        System.out.println("   Loading author WITH books in single query");

        // Нативный запрос с немедленной загрузкой книг
        List<Author> authorsWithBooks = session.createNativeQuery(
                        "SELECT {a.*}, {b.*} " +
                                "FROM slide14_authors a " +
                                "LEFT JOIN slide14_book_details b ON a.id = b.author_id " +
                                "WHERE a.full_name = 'Stephen King'"
                )
                .addEntity("a", Author.class)
                .addJoin("b", "a.books")
                .list();

        Author loadedAuthor = authorsWithBooks.get(0);
        System.out.println("   Author: " + loadedAuthor.getFullName());
        System.out.println("   Books count: " + loadedAuthor.getBooks().size());

        // Показываем книги
        System.out.println("   Books by " + loadedAuthor.getFullName() + ":");
        for (BookDetail book : loadedAuthor.getBooks()) {
            System.out.println("     - " + book.getTitle() + " (" + book.getPublicationYear() + ")");
        }
    }

    private static void prepareAuthorBookData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Очистка таблиц
            session.createQuery("delete from BookDetail").executeUpdate();
            session.createQuery("delete from Author").executeUpdate();

            // Создание авторов
            Author a1 = new Author();
            a1.setFullName("Stephen King");
            a1.setNationality("American");
            a1.setEmail("sking@email.com");

            Author a2 = new Author();
            a2.setFullName("J.K. Rowling");
            a2.setNationality("British");
            a2.setEmail("jkrowling@email.com");

            Author a3 = new Author();
            a3.setFullName("Haruki Murakami");
            a3.setNationality("Japanese");
            a3.setEmail("hmurakami@email.com");

            session.save(a1);
            session.save(a2);
            session.save(a3);

            // Создание книг
            BookDetail b1 = new BookDetail();
            b1.setTitle("The Shining");
            b1.setGenre("Horror");
            b1.setPrice(new BigDecimal("12.99"));
            b1.setPublicationYear(1977);
            b1.setPageCount(447);
            b1.setAuthor(a1);

            BookDetail b2 = new BookDetail();
            b2.setTitle("It");
            b2.setGenre("Horror");
            b2.setPrice(new BigDecimal("14.99"));
            b2.setPublicationYear(1986);
            b2.setPageCount(1138);
            b2.setAuthor(a1);

            BookDetail b3 = new BookDetail();
            b3.setTitle("Harry Potter and the Philosopher's Stone");
            b3.setGenre("Fantasy");
            b3.setPrice(new BigDecimal("19.99"));
            b3.setPublicationYear(1997);
            b3.setPageCount(223);
            b3.setAuthor(a2);

            BookDetail b4 = new BookDetail();
            b4.setTitle("Norwegian Wood");
            b4.setGenre("Fiction");
            b4.setPrice(new BigDecimal("11.99"));
            b4.setPublicationYear(1987);
            b4.setPageCount(296);
            b4.setAuthor(a3);

            BookDetail b5 = new BookDetail();
            b5.setTitle("The Stand");
            b5.setGenre("Post-Apocalyptic");
            b5.setPrice(new BigDecimal("16.99"));
            b5.setPublicationYear(1978);
            b5.setPageCount(823);
            b5.setAuthor(a1);

            session.save(b1);
            session.save(b2);
            session.save(b3);
            session.save(b4);
            session.save(b5);

            transaction.commit();
            System.out.println("Test data: 3 authors and 5 books inserted.");
        }
    }

    public static void demonstrateSlide13() {
        System.out.println("\n=== Demo for Slide 13: NativeQuery Entity Mapping ===");
        System.out.println("Showing both Hibernate and JPA approaches for entity mapping");

        // Подготовка тестовых данных
        prepareBookData();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            System.out.println("\n--- Approach 1: Hibernate style (addEntity) ---");
            demoHibernateStyleMapping(session);

            System.out.println("\n--- Approach 2: JPA style (parameter in method) ---");
            demoJpaStyleMapping(session);

            System.out.println("\n--- Comparison: Both approaches give same results ---");
            compareBothApproaches(session);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Способ 1: Стиль Hibernate (исторический)
    private static void demoHibernateStyleMapping(Session session) {
        System.out.println("Query<Book> query = session.createNativeQuery(\"SELECT * FROM slide13_books\")");
        System.out.println("                     .addEntity(Book.class);");

        // Hibernate подход: отдельный вызов addEntity()
        org.hibernate.query.Query<Book> query = session.createNativeQuery("SELECT * FROM slide13_books")
                .addEntity(Book.class);

        List<Book> books = query.list();

        System.out.println("Results: " + books.size() + " Book entities");
        System.out.println("First book: \"" + books.get(0).getTitle() + "\" by " + books.get(0).getAuthor());
    }

    // Способ 2: Стиль JPA (современный)
    private static void demoJpaStyleMapping(Session session) {
        System.out.println("List<Book> books = session.createNativeQuery(");
        System.out.println("    \"SELECT * FROM slide13_books\", Book.class).list();");

        // JPA подход: класс сущности передается как параметр
        List<Book> books = session.createNativeQuery(
                "SELECT * FROM slide13_books", Book.class
        ).list();

        System.out.println("Results: " + books.size() + " Book entities");
        System.out.println("First book: \"" + books.get(0).getTitle() + "\" by " + books.get(0).getAuthor());
    }

    // Сравнение обоих подходов
    private static void compareBothApproaches(Session session) {
        System.out.println("\nExecuting both queries and comparing results...");

        // Подход Hibernate
        List<Book> hibernateResults = session.createNativeQuery("SELECT * FROM slide13_books WHERE price > 20")
                .addEntity(Book.class)
                .list();

        // Подход JPA
        List<Book> jpaResults = session.createNativeQuery(
                "SELECT * FROM slide13_books WHERE price > 20", Book.class
        ).list();

        System.out.println("Hibernate approach count: " + hibernateResults.size());
        System.out.println("JPA approach count: " + jpaResults.size());
        System.out.println("Results are identical: " + hibernateResults.equals(jpaResults));

        // Дополнительный пример с WHERE условием
        System.out.println("\n--- Additional example with WHERE clause ---");

        // JPA стиль с параметром
        List<Book> expensiveBooks = session.createNativeQuery(
                        "SELECT * FROM slide13_books WHERE price > :minPrice AND in_stock = true",
                        Book.class
                )
                .setParameter("minPrice", new BigDecimal("25.00"))
                .list();

        System.out.println("Expensive books in stock: " + expensiveBooks.size());
        for (Book book : expensiveBooks) {
            System.out.println("  - " + book.getTitle() + " ($" + book.getPrice() + ")");
        }
    }

    private static void prepareBookData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Очистка таблицы
            session.createQuery("delete from Book").executeUpdate();

            // Создание тестовых данных
            Book b1 = new Book();
            b1.setTitle("Effective Java");
            b1.setAuthor("Joshua Bloch");
            b1.setPrice(new BigDecimal("45.99"));
            b1.setPublishedDate(LocalDate.of(2018, 1, 1));
            b1.setInStock(true);

            Book b2 = new Book();
            b2.setTitle("Clean Code");
            b2.setAuthor("Robert Martin");
            b2.setPrice(new BigDecimal("39.99"));
            b2.setPublishedDate(LocalDate.of(2008, 8, 1));
            b2.setInStock(true);

            Book b3 = new Book();
            b3.setTitle("Java Concurrency in Practice");
            b3.setAuthor("Brian Goetz");
            b3.setPrice(new BigDecimal("54.99"));
            b3.setPublishedDate(LocalDate.of(2006, 5, 19));
            b3.setInStock(false);

            Book b4 = new Book();
            b4.setTitle("Head First Design Patterns");
            b4.setAuthor("Eric Freeman");
            b4.setPrice(new BigDecimal("32.50"));
            b4.setPublishedDate(LocalDate.of(2004, 10, 25));
            b4.setInStock(true);

            Book b5 = new Book();
            b5.setTitle("The Java Programming Language");
            b5.setAuthor("James Gosling");
            b5.setPrice(new BigDecimal("19.99"));
            b5.setPublishedDate(LocalDate.of(2005, 6, 15));
            b5.setInStock(true);

            session.save(b1);
            session.save(b2);
            session.save(b3);
            session.save(b4);
            session.save(b5);

            transaction.commit();
            System.out.println("Test data: 5 books inserted.");
        }
    }

    public static void demonstrateSlide12() {
        System.out.println("\n=== Demo for Slide 12: NativeQuery without Entity Mapping ===");
        System.out.println("Using NativeQuery with Object[] result (as shown on slide)");

        // Подготовка тестовых данных
        preparePersonData();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            System.out.println("\n--- Example 1: SELECT * (as shown on slide) ---");
            System.out.println("Native SQL: SELECT * FROM slide12_persons");
            demoNativeQuerySelectAll(session);

            System.out.println("\n--- Example 2: SELECT specific columns ---");
            System.out.println("Native SQL: SELECT first_name, last_name, city, age FROM slide12_persons");
            demoNativeQueryPartialSelect(session);

            System.out.println("\n--- Example 3: SELECT with aggregation ---");
            System.out.println("Native SQL: SELECT city, COUNT(*), AVG(age) FROM slide12_persons GROUP BY city");
            demoNativeQueryWithAggregation(session);

            System.out.println("\n--- Example 4: SELECT with complex SQL function ---");
            System.out.println("Native SQL: Using database-specific function (CONCAT for MySQL)");
            demoNativeQueryWithSqlFunctions(session);

            System.out.println("\n--- Example 5: Comparing with HQL approach ---");
            System.out.println("Same query using HQL vs NativeQuery");
            compareWithHql(session);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Пример 1: SELECT * (как на слайде)
    private static void demoNativeQuerySelectAll(Session session) {
        // Как на слайде: NativeQuery без указания класса сущности
        List<Object[]> results = session.createNativeQuery("SELECT * FROM slide12_persons")
                .list();

        System.out.println("\nResult as Object[] arrays (no entity mapping):");
        System.out.println("=".repeat(100));
        System.out.println(String.format("%-3s | %-12s | %-12s | %-4s | %-15s | %-15s | %-25s",
                "ID", "First Name", "Last Name", "Age", "City", "Registration", "Email"));
        System.out.println("-".repeat(100));

        for (Object[] row : results) {
            // Порядок колонок соответствует порядку в таблице
            Long id = ((Number) row[0]).longValue();        // id
            String firstName = (String) row[1];             // first_name
            String lastName = (String) row[2];              // last_name
            Integer age = ((Number) row[3]).intValue();     // age
            String city = (String) row[4];                  // city
            java.sql.Date regDate = (java.sql.Date) row[5]; // registration_date
            String email = row[6] != null ? (String) row[6] : "NULL"; // email (может быть null)

            System.out.println(String.format("%-3d | %-12s | %-12s | %-4d | %-15s | %-15s | %-25s",
                    id, firstName, lastName, age, city, regDate.toString(), email));
        }
        System.out.println("Total rows: " + results.size());
    }

    // Пример 2: Выборка только нескольких колонок
    private static void demoNativeQueryPartialSelect(Session session) {
        // Выбираем только 4 колонки вместо всей таблицы
        List<Object[]> results = session.createNativeQuery(
                "SELECT first_name, last_name, city, age FROM slide12_persons ORDER BY city, last_name"
        ).list();

        System.out.println("\nPartial select - only 4 columns:");
        System.out.println("=".repeat(55));
        System.out.println(String.format("%-12s | %-12s | %-15s | %-4s",
                "First Name", "Last Name", "City", "Age"));
        System.out.println("-".repeat(55));

        for (Object[] row : results) {
            String firstName = (String) row[0];         // first_name
            String lastName = (String) row[1];          // last_name
            String city = (String) row[2];              // city
            Integer age = ((Number) row[3]).intValue(); // age

            System.out.println(String.format("%-12s | %-12s | %-15s | %-4d",
                    firstName, lastName, city, age));
        }
    }

    // Пример 3: Агрегация с GROUP BY
    private static void demoNativeQueryWithAggregation(Session session) {
        List<Object[]> results = session.createNativeQuery(
                "SELECT city, COUNT(*) as person_count, AVG(age) as avg_age " +
                        "FROM slide12_persons " +
                        "GROUP BY city " +
                        "ORDER BY person_count DESC"
        ).list();

        System.out.println("\nAggregation with GROUP BY:");
        System.out.println("=".repeat(50));
        System.out.println(String.format("%-15s | %-13s | %-10s",
                "City", "Person Count", "Avg Age"));
        System.out.println("-".repeat(50));

        for (Object[] row : results) {
            String city = (String) row[0];                     // city
            Long count = ((Number) row[1]).longValue();        // COUNT(*)
            Double avgAge = ((Number) row[2]).doubleValue();   // AVG(age)

            System.out.println(String.format("%-15s | %-13d | %-10.1f",
                    city, count, avgAge));
        }
    }

    // Пример 4: Использование специфичных SQL-функций
    private static void demoNativeQueryWithSqlFunctions(Session session) {
        // Используем CONCAT - специфичная функция MySQL
        List<Object[]> results = session.createNativeQuery(
                "SELECT CONCAT(first_name, ' ', last_name) as full_name, " +
                        "city, " +
                        "TIMESTAMPDIFF(YEAR, registration_date, CURDATE()) as years_registered " +
                        "FROM slide12_persons " +
                        "WHERE email IS NOT NULL " +
                        "ORDER BY years_registered DESC"
        ).list();

        System.out.println("\nUsing database-specific SQL functions (MySQL CONCAT, TIMESTAMPDIFF):");
        System.out.println("=".repeat(70));
        System.out.println(String.format("%-25s | %-15s | %-15s",
                "Full Name", "City", "Years Registered"));
        System.out.println("-".repeat(70));

        for (Object[] row : results) {
            String fullName = (String) row[0];          // CONCAT result
            String city = (String) row[1];              // city
            Long yearsRegistered = ((Number) row[2]).longValue(); // TIMESTAMPDIFF result

            System.out.println(String.format("%-25s | %-15s | %-15d",
                    fullName, city, yearsRegistered));
        }
    }

    // Пример 5: Сравнение с HQL
    private static void compareWithHql(Session session) {
        System.out.println("\nComparison: HQL vs NativeQuery for same result");

        // Способ 1: HQL (нужна сущность Person)
        List<Person> hqlResults = session.createQuery(
                "SELECT p FROM Person p WHERE p.age > 25 ORDER BY p.city", Person.class
        ).list();

        System.out.println("\nHQL approach (returns Person entities):");
        System.out.println("Query: SELECT p FROM Person p WHERE p.age > 25 ORDER BY p.city");
        System.out.println("Results: " + hqlResults.size() + " Person objects");

        // Способ 2: NativeQuery (только данные, без сущностей)
        List<Object[]> nativeResults = session.createNativeQuery(
                "SELECT first_name, last_name, city, age FROM slide12_persons " +
                        "WHERE age > 25 ORDER BY city"
        ).list();

        System.out.println("\nNativeQuery approach (returns Object[] arrays):");
        System.out.println("Query: SELECT first_name, last_name, city, age FROM slide12_persons WHERE age > 25 ORDER BY city");
        System.out.println("Results: " + nativeResults.size() + " Object[] rows");
        System.out.println("\nMemory/Performance consideration:");
        System.out.println("- HQL: Creates full Person entities with lifecycle management");
        System.out.println("- NativeQuery: Simple data transfer, no entity overhead");
    }

    private static void preparePersonData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Очистка таблицы
            session.createQuery("delete from Person").executeUpdate();

            // Создание тестовых данных
            LocalDate baseDate = LocalDate.of(2020, 1, 1);

            Person p1 = new Person();
            p1.setFirstName("John");
            p1.setLastName("Smith");
            p1.setAge(30);
            p1.setCity("New York");
            p1.setRegistrationDate(baseDate.plusMonths(2));
            p1.setEmail("john.smith@email.com");

            Person p2 = new Person();
            p2.setFirstName("Emma");
            p2.setLastName("Wilson");
            p2.setAge(25);
            p2.setCity("London");
            p2.setRegistrationDate(baseDate.plusMonths(5));
            p2.setEmail("emma.wilson@email.com");

            Person p3 = new Person();
            p3.setFirstName("Alex");
            p3.setLastName("Johnson");
            p3.setAge(35);
            p3.setCity("New York");
            p3.setRegistrationDate(baseDate.plusMonths(8));
            p3.setEmail(null); // NULL email

            Person p4 = new Person();
            p4.setFirstName("Michael");
            p4.setLastName("Brown");
            p4.setAge(28);
            p4.setCity("Berlin");
            p4.setRegistrationDate(baseDate.plusMonths(12));
            p4.setEmail("michael.b@email.com");

            Person p5 = new Person();
            p5.setFirstName("Sarah");
            p5.setLastName("Davis");
            p5.setAge(42);
            p5.setCity("London");
            p5.setRegistrationDate(baseDate.plusMonths(3));
            p5.setEmail("sarah.davis@email.com");

            Person p6 = new Person();
            p6.setFirstName("Robert");
            p6.setLastName("Taylor");
            p6.setAge(22);
            p6.setCity("Paris");
            p6.setRegistrationDate(baseDate.plusMonths(15));
            p6.setEmail("robert.t@email.com");

            session.save(p1);
            session.save(p2);
            session.save(p3);
            session.save(p4);
            session.save(p5);
            session.save(p6);

            transaction.commit();
            System.out.println("Test data: 6 persons inserted with varied attributes.");
        }
    }

    public static void demonstrateSlide10() {
        System.out.println("\n=== Demo for Slide 10: CriteriaUpdate and CriteriaDelete ===");

        // Подготовка тестовых данных
        prepareProductStockData();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            System.out.println("\n--- Current Data Before Operations ---");
            displayAllProducts(session);

            System.out.println("\n" + "=".repeat(70));
            System.out.println("PART 1: CriteriaUpdate - Bulk Price Update");
            System.out.println("Update: Increase price by 10% for all Electronics products");
            demoCriteriaUpdate(session, builder);

            System.out.println("\n--- Data After Price Update ---");
            displayAllProducts(session);

            System.out.println("\n" + "=".repeat(70));
            System.out.println("PART 2: CriteriaUpdate - Complex Update with Conditions");
            System.out.println("Update: Mark as inactive and set quantity to 0 for low-stock Clothing");
            demoCriteriaUpdateWithConditions(session, builder);

            System.out.println("\n--- Data After Complex Update ---");
            displayAllProducts(session);

            System.out.println("\n" + "=".repeat(70));
            System.out.println("PART 3: CriteriaDelete - Simple Delete");
            System.out.println("Delete: Remove all inactive products");
            demoCriteriaDelete(session, builder);

            System.out.println("\n--- Data After Delete ---");
            displayAllProducts(session);

            System.out.println("\n" + "=".repeat(70));
            System.out.println("PART 4: CriteriaDelete - Delete with Complex Conditions");
            System.out.println("Delete: Remove all FOOD products with price < 5.00");
            demoCriteriaDeleteWithConditions(session, builder);

            System.out.println("\n--- Final Data After All Operations ---");
            displayAllProducts(session);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Метод для отображения всех продуктов
    private static void displayAllProducts(Session session) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ProductStock> query = builder.createQuery(ProductStock.class);
        Root<ProductStock> root = query.from(ProductStock.class);

        query.select(root).orderBy(builder.asc(root.get("category")),
                builder.asc(root.get("productCode")));

        List<ProductStock> products = session.createQuery(query).getResultList();

        System.out.println("\n" + String.format("%-10s | %-20s | %-12s | %8s | %-10s | %-6s",
                "Code", "Name", "Category", "Qty", "Price", "Active"));
        System.out.println("-".repeat(85));

        for (ProductStock p : products) {
            System.out.println(String.format("%-10s | %-20s | %-12s | %8d | $%7.2f | %-6s",
                    p.getProductCode(),
                    p.getProductName(),
                    p.getCategory(),
                    p.getQuantity(),
                    p.getPrice(),
                    p.getActive() ? "Yes" : "No"
            ));
        }
        System.out.println("Total: " + products.size() + " product(s)");
    }

    // 1. Простое обновление: Увеличение цены для категории
    private static void demoCriteriaUpdate(Session session, CriteriaBuilder builder) {
        Transaction transaction = session.beginTransaction();

        try {
            // Создаем CriteriaUpdate
            CriteriaUpdate<ProductStock> update = builder.createCriteriaUpdate(ProductStock.class);
            Root<ProductStock> root = update.from(ProductStock.class);

            // Явно создаем выражение для умножения
            Expression<BigDecimal> newPriceExpression = builder.prod(
                    root.<BigDecimal>get("price"),
                    new BigDecimal("1.10")
            );

            // Устанавливаем новое значение: price = price * 1.10 (увеличение на 10%)
            update.set(root.<BigDecimal>get("price"), newPriceExpression);

            // Обновляем поле lastUpdated
            update.set(root.get("lastUpdated"), LocalDateTime.now());

            // Условие: только продукты категории ELECTRONICS
            update.where(builder.equal(root.get("category"), "ELECTRONICS"));

            // Выполняем обновление
            int updatedCount = session.createQuery(update).executeUpdate();

            transaction.commit();
            System.out.println("Updated " + updatedCount + " Electronics product(s)");

        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    // 2. Сложное обновление с несколькими условиями
    private static void demoCriteriaUpdateWithConditions(Session session, CriteriaBuilder builder) {
        Transaction transaction = session.beginTransaction();

        try {
            CriteriaUpdate<ProductStock> update = builder.createCriteriaUpdate(ProductStock.class);
            Root<ProductStock> root = update.from(ProductStock.class);

            // Устанавливаем несколько значений
            update.set(root.get("active"), false);
            update.set(root.get("quantity"), 0);
            update.set(root.get("lastUpdated"), LocalDateTime.now());

            // Сложное условие: Clothing с количеством меньше 5
            Predicate categoryCondition = builder.equal(root.get("category"), "CLOTHING");
            Predicate lowStockCondition = builder.lt(root.get("quantity"), 5);
            Predicate activeCondition = builder.equal(root.get("active"), true);

            update.where(builder.and(categoryCondition, lowStockCondition, activeCondition));

            int updatedCount = session.createQuery(update).executeUpdate();

            transaction.commit();
            System.out.println("Deactivated " + updatedCount + " low-stock Clothing product(s)");

        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    // 3. Простое удаление: все неактивные продукты
    private static void demoCriteriaDelete(Session session, CriteriaBuilder builder) {
        Transaction transaction = session.beginTransaction();

        try {
            // Создаем CriteriaDelete
            CriteriaDelete<ProductStock> delete = builder.createCriteriaDelete(ProductStock.class);
            Root<ProductStock> root = delete.from(ProductStock.class);

            // Условие: active = false
            delete.where(builder.equal(root.get("active"), false));

            int deletedCount = session.createQuery(delete).executeUpdate();

            transaction.commit();
            System.out.println("Deleted " + deletedCount + " inactive product(s)");

        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    // 4. Удаление с сложными условиями
    private static void demoCriteriaDeleteWithConditions(Session session, CriteriaBuilder builder) {
        Transaction transaction = session.beginTransaction();

        try {
            CriteriaDelete<ProductStock> delete = builder.createCriteriaDelete(ProductStock.class);
            Root<ProductStock> root = delete.from(ProductStock.class);

            // Условия: FOOD категория И цена меньше 5.00
            Predicate categoryCondition = builder.equal(root.get("category"), "FOOD");
            Predicate priceCondition = builder.lt(root.get("price"), new BigDecimal("5.00"));

            delete.where(builder.and(categoryCondition, priceCondition));

            int deletedCount = session.createQuery(delete).executeUpdate();

            transaction.commit();
            System.out.println("Deleted " + deletedCount + " cheap FOOD product(s)");

        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    private static void prepareProductStockData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Очистка таблицы
            session.createQuery("delete from ProductStock").executeUpdate();

            // Создание тестовых данных
            LocalDateTime now = LocalDateTime.now();

            // Электроника
            ProductStock p1 = new ProductStock();
            p1.setProductCode("ELEC001");
            p1.setProductName("Smartphone X");
            p1.setQuantity(25);
            p1.setPrice(new BigDecimal("699.99"));
            p1.setCategory("ELECTRONICS");
            p1.setLastUpdated(now.minusDays(10));
            p1.setActive(true);

            ProductStock p2 = new ProductStock();
            p2.setProductCode("ELEC002");
            p2.setProductName("Laptop Pro");
            p2.setQuantity(15);
            p2.setPrice(new BigDecimal("1299.99"));
            p2.setCategory("ELECTRONICS");
            p2.setLastUpdated(now.minusDays(5));
            p2.setActive(true);

            ProductStock p3 = new ProductStock();
            p3.setProductCode("ELEC003");
            p3.setProductName("Wireless Headphones");
            p3.setQuantity(8);
            p3.setPrice(new BigDecimal("199.99"));
            p3.setCategory("ELECTRONICS");
            p3.setLastUpdated(now.minusDays(2));
            p3.setActive(true);

            // Одежда
            ProductStock p4 = new ProductStock();
            p4.setProductCode("CLOTH001");
            p4.setProductName("T-Shirt Basic");
            p4.setQuantity(50);
            p4.setPrice(new BigDecimal("19.99"));
            p4.setCategory("CLOTHING");
            p4.setLastUpdated(now.minusDays(30));
            p4.setActive(true);

            ProductStock p5 = new ProductStock();
            p5.setProductCode("CLOTH002");
            p5.setProductName("Jeans Classic");
            p5.setQuantity(3); // Низкий запас
            p5.setPrice(new BigDecimal("49.99"));
            p5.setCategory("CLOTHING");
            p5.setLastUpdated(now.minusDays(15));
            p5.setActive(true);

            ProductStock p6 = new ProductStock();
            p6.setProductCode("CLOTH003");
            p6.setProductName("Winter Jacket");
            p6.setQuantity(0); // Нет в наличии
            p6.setPrice(new BigDecimal("129.99"));
            p6.setCategory("CLOTHING");
            p6.setLastUpdated(now.minusDays(60));
            p6.setActive(false); // Неактивный

            // Еда
            ProductStock p7 = new ProductStock();
            p7.setProductCode("FOOD001");
            p7.setProductName("Chocolate Bar");
            p7.setQuantity(100);
            p7.setPrice(new BigDecimal("2.99"));
            p7.setCategory("FOOD");
            p7.setLastUpdated(now.minusDays(1));
            p7.setActive(true);

            ProductStock p8 = new ProductStock();
            p8.setProductCode("FOOD002");
            p8.setProductName("Premium Coffee");
            p8.setQuantity(40);
            p8.setPrice(new BigDecimal("12.99"));
            p8.setCategory("FOOD");
            p8.setLastUpdated(now.minusDays(3));
            p8.setActive(true);

            ProductStock p9 = new ProductStock();
            p9.setProductCode("FOOD003");
            p9.setProductName("Candy Pack");
            p9.setQuantity(200);
            p9.setPrice(new BigDecimal("1.49")); // Дешевая еда
            p9.setCategory("FOOD");
            p9.setLastUpdated(now.minusDays(7));
            p9.setActive(true);

            // Другие товары
            ProductStock p10 = new ProductStock();
            p10.setProductCode("OTHER001");
            p10.setProductName("Desk Lamp");
            p10.setQuantity(12);
            p10.setPrice(new BigDecimal("34.99"));
            p10.setCategory("OTHER");
            p10.setLastUpdated(now.minusDays(20));
            p10.setActive(true);

            session.save(p1);
            session.save(p2);
            session.save(p3);
            session.save(p4);
            session.save(p5);
            session.save(p6);
            session.save(p7);
            session.save(p8);
            session.save(p9);
            session.save(p10);

            transaction.commit();
            System.out.println("Test data: 10 products inserted with different categories and statuses.");
        }
    }

    public static void demonstrateSlide9() {
        System.out.println("\n=== Demo for Slide 9: Aggregation Functions with Criteria API ===");
        System.out.println("Demonstrating COUNT, AVG, SUM, MIN, MAX functions");

        // Подготовка тестовых данных
        prepareSalesData();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            System.out.println("\n--- Example 1: COUNT (as shown on slide) ---");
            System.out.println("Query: Count all sales records");
            demoCountFunction(session, builder);

            System.out.println("\n--- Example 2: AVG (as shown on slide) ---");
            System.out.println("Query: Calculate average sale amount");
            demoAvgFunction(session, builder);

            System.out.println("\n--- Example 3: SUM ---");
            System.out.println("Query: Calculate total sales amount");
            demoSumFunction(session, builder);

            System.out.println("\n--- Example 4: MIN and MAX ---");
            System.out.println("Query: Find smallest and largest sale amounts");
            demoMinMaxFunctions(session, builder);

            System.out.println("\n--- Example 5: Multiple Aggregations in One Query ---");
            System.out.println("Query: Get comprehensive sales statistics");
            demoMultipleAggregations(session, builder);

            System.out.println("\n--- Example 6: Aggregation with WHERE condition ---");
            System.out.println("Query: Average sale amount for Electronics category in 2023");
            demoAggregationWithWhere(session, builder);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 1. COUNT() - Подсчет всех записей (как на слайде)
    private static void demoCountFunction(Session session, CriteriaBuilder builder) {
        // Способ 1: Как показано на слайде (без явного Root)
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        query.select(builder.count(query.from(SalesRecord.class)));

        Long totalCount = session.createQuery(query).getSingleResult();
        System.out.println("Method 1 (slide style): Total sales records = " + totalCount);

        // Способ 2: С явным Root (более читаемо для сложных запросов)
        CriteriaQuery<Long> query2 = builder.createQuery(Long.class);
        Root<SalesRecord> root = query2.from(SalesRecord.class);
        query2.select(builder.count(root));

        Long totalCount2 = session.createQuery(query2).getSingleResult();
        System.out.println("Method 2 (with explicit Root): Total sales records = " + totalCount2);
    }

    // 2. AVG() - Среднее значение (как на слайде)
    private static void demoAvgFunction(Session session, CriteriaBuilder builder) {
        // Способ 1: Как показано на слайде
        CriteriaQuery<Double> query = builder.createQuery(Double.class);
        query.select(builder.avg(query.from(SalesRecord.class).get("saleAmount")));

        Double averageSale = session.createQuery(query).getSingleResult();
        System.out.println("Method 1 (slide style): Average sale amount = $" +
                String.format("%.2f", averageSale));

        // Способ 2: С явным Root (используем Double, а не BigDecimal)
        CriteriaQuery<Double> query2 = builder.createQuery(Double.class);
        Root<SalesRecord> root = query2.from(SalesRecord.class);
        query2.select(builder.avg(root.get("saleAmount")));

        Double averageSale2 = session.createQuery(query2).getSingleResult();
        System.out.println("Method 2 (with explicit Root): Average sale amount = $" +
                String.format("%.2f", averageSale2));
    }

    // 3. SUM() - Сумма значений
    private static void demoSumFunction(Session session, CriteriaBuilder builder) {
        CriteriaQuery<BigDecimal> query = builder.createQuery(BigDecimal.class);
        Root<SalesRecord> root = query.from(SalesRecord.class);

        query.select(builder.sum(root.get("saleAmount")));

        BigDecimal totalSales = session.createQuery(query).getSingleResult();
        System.out.println("Total sales amount = $" +
                totalSales.setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    // 4. MIN() и MAX() - Минимальное и максимальное значения
    private static void demoMinMaxFunctions(Session session, CriteriaBuilder builder) {
        // MIN
        CriteriaQuery<BigDecimal> minQuery = builder.createQuery(BigDecimal.class);
        Root<SalesRecord> minRoot = minQuery.from(SalesRecord.class);
        minQuery.select(builder.min(minRoot.get("saleAmount")));

        BigDecimal minSale = session.createQuery(minQuery).getSingleResult();
        System.out.println("Smallest sale amount = $" +
                minSale.setScale(2, BigDecimal.ROUND_HALF_UP));

        // MAX
        CriteriaQuery<BigDecimal> maxQuery = builder.createQuery(BigDecimal.class);
        Root<SalesRecord> maxRoot = maxQuery.from(SalesRecord.class);
        maxQuery.select(builder.max(maxRoot.get("saleAmount")));

        BigDecimal maxSale = session.createQuery(maxQuery).getSingleResult();
        System.out.println("Largest sale amount = $" +
                maxSale.setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    // 5. Несколько агрегирующих функций в одном запросе
    // 5. Несколько агрегирующих функций в одном запросе
    private static void demoMultipleAggregations(Session session, CriteriaBuilder builder) {
        System.out.println("\nMultiple aggregations require a different approach:");
        System.out.println("We need to use Tuple or Object[] as result type");

        CriteriaQuery<Object[]> query = builder.createQuery(Object[].class);
        Root<SalesRecord> root = query.from(SalesRecord.class);

        query.multiselect(
                builder.count(root).alias("count"),
                builder.avg(root.get("saleAmount")).alias("avg_amount"),
                builder.sum(root.get("saleAmount")).alias("total_amount"),
                builder.min(root.get("saleAmount")).alias("min_amount"),
                builder.max(root.get("saleAmount")).alias("max_amount")
        );

        Object[] result = session.createQuery(query).getSingleResult();

        System.out.println("\nComprehensive Sales Statistics:");
        System.out.println("Total records: " + result[0]);
        System.out.println("Average sale: $" +
                String.format("%.2f", (Double)result[1])); // Double для avg
        System.out.println("Total sales: $" +
                ((BigDecimal)result[2]).setScale(2, BigDecimal.ROUND_HALF_UP));
        System.out.println("Min sale: $" +
                ((BigDecimal)result[3]).setScale(2, BigDecimal.ROUND_HALF_UP));
        System.out.println("Max sale: $" +
                ((BigDecimal)result[4]).setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    // 6. Агрегация с условием WHERE
    private static void demoAggregationWithWhere(Session session, CriteriaBuilder builder) {
        // Используем Double, так как avg() возвращает Double
        CriteriaQuery<Double> query = builder.createQuery(Double.class);
        Root<SalesRecord> root = query.from(SalesRecord.class);

        // Условия WHERE
        Predicate categoryCondition = builder.equal(root.get("productCategory"), "ELECTRONICS");
        Predicate dateCondition = builder.between(
                root.get("saleDate"),
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31)
        );
        Predicate finalCondition = builder.and(categoryCondition, dateCondition);

        query.select(builder.avg(root.get("saleAmount")))
                .where(finalCondition);

        Double avgElectronics2023 = session.createQuery(query).getSingleResult();

        System.out.println("Average Electronics sale in 2023 = $" +
                (avgElectronics2023 != null ?
                        String.format("%.2f", avgElectronics2023) : "0.00"));
    }

    private static void prepareSalesData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Очистка таблицы
            session.createQuery("delete from SalesRecord").executeUpdate();

            // Создание тестовых данных
            LocalDate baseDate = LocalDate.of(2023, 1, 15);

            // Северный регион
            SalesRecord s1 = new SalesRecord();
            s1.setSalesperson("John Smith");
            s1.setRegion("NORTH");
            s1.setSaleAmount(new BigDecimal("1250.50"));
            s1.setCommissionRate(new BigDecimal("0.05"));
            s1.setSaleDate(baseDate.plusDays(10));
            s1.setProductCategory("ELECTRONICS");

            SalesRecord s2 = new SalesRecord();
            s2.setSalesperson("John Smith");
            s2.setRegion("NORTH");
            s2.setSaleAmount(new BigDecimal("850.75"));
            s2.setCommissionRate(new BigDecimal("0.05"));
            s2.setSaleDate(baseDate.plusDays(25));
            s2.setProductCategory("FURNITURE");

            // Южный регион
            SalesRecord s3 = new SalesRecord();
            s3.setSalesperson("Emma Wilson");
            s3.setRegion("SOUTH");
            s3.setSaleAmount(new BigDecimal("2200.00"));
            s3.setCommissionRate(new BigDecimal("0.07"));
            s3.setSaleDate(baseDate.plusMonths(1).plusDays(5));
            s3.setProductCategory("ELECTRONICS");

            SalesRecord s4 = new SalesRecord();
            s4.setSalesperson("Emma Wilson");
            s4.setRegion("SOUTH");
            s4.setSaleAmount(new BigDecimal("450.25"));
            s4.setCommissionRate(new BigDecimal("0.07"));
            s4.setSaleDate(baseDate.plusMonths(2));
            s4.setProductCategory("CLOTHING");

            // Восточный регион
            SalesRecord s5 = new SalesRecord();
            s5.setSalesperson("Alex Johnson");
            s5.setRegion("EAST");
            s5.setSaleAmount(new BigDecimal("3200.00"));
            s5.setCommissionRate(new BigDecimal("0.06"));
            s5.setSaleDate(baseDate.plusMonths(3));
            s5.setProductCategory("ELECTRONICS");

            SalesRecord s6 = new SalesRecord();
            s6.setSalesperson("Alex Johnson");
            s6.setRegion("EAST");
            s6.setSaleAmount(new BigDecimal("1750.00"));
            s6.setCommissionRate(null); // NULL commission rate
            s6.setSaleDate(baseDate.plusMonths(4));
            s6.setProductCategory("FURNITURE");

            // Западный регион (2024 год для демонстрации фильтрации по дате)
            SalesRecord s7 = new SalesRecord();
            s7.setSalesperson("Michael Brown");
            s7.setRegion("WEST");
            s7.setSaleAmount(new BigDecimal("2800.00"));
            s7.setCommissionRate(new BigDecimal("0.08"));
            s7.setSaleDate(LocalDate.of(2024, 2, 20));
            s7.setProductCategory("ELECTRONICS");

            SalesRecord s8 = new SalesRecord();
            s8.setSalesperson("Michael Brown");
            s8.setRegion("WEST");
            s8.setSaleAmount(new BigDecimal("950.50"));
            s8.setCommissionRate(new BigDecimal("0.08"));
            s8.setSaleDate(LocalDate.of(2024, 3, 15));
            s8.setProductCategory("CLOTHING");

            session.save(s1);
            session.save(s2);
            session.save(s3);
            session.save(s4);
            session.save(s5);
            session.save(s6);
            session.save(s7);
            session.save(s8);

            transaction.commit();
            System.out.println("Test data: 8 sales records inserted with varied amounts and dates.");
        }
    }

    public static void demonstrateSlide8() {
        System.out.println("\n=== Demo for Slide 8: Sorting with Criteria API ===");
        System.out.println("Demonstrating ORDER BY with multiple fields");

        // Подготовка тестовых данных
        prepareProjectData();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            System.out.println("\nQuery: Find ACTIVE projects, sorted by:");
            System.out.println("  1. Priority ASC (highest priority first)");
            System.out.println("  2. Budget DESC (largest budget first)");
            System.out.println("  3. Team Size ASC (smallest teams first)");

            // 1. Создаем CriteriaQuery
            CriteriaQuery<Project> query = builder.createQuery(Project.class);

            // 2. Определяем Root (FROM часть)
            Root<Project> root = query.from(Project.class);

            // 3. Задаем SELECT
            query.select(root);

            // 4. Добавляем WHERE условие (только активные проекты)
            // Используем CriteriaBuilder для создания условия
            Predicate activeStatus = builder.equal(root.get("status"), "ACTIVE");
            query.where(activeStatus);

            // 5. Добавляем ORDER BY с несколькими полями
            // Используем CriteriaBuilder для создания условий сортировки
            javax.persistence.criteria.Order priorityOrder = builder.asc(root.get("priority"));
            javax.persistence.criteria.Order budgetOrder = builder.desc(root.get("budget"));
            javax.persistence.criteria.Order teamSizeOrder = builder.asc(root.get("teamSize"));

            // Комбинируем сортировки в правильном порядке приоритета
            query.orderBy(priorityOrder, budgetOrder, teamSizeOrder);

            System.out.println("\n--- Building the query step by step ---");
            System.out.println("1. CriteriaBuilder builder = session.getCriteriaBuilder();");
            System.out.println("2. CriteriaQuery<Project> query = builder.createQuery(Project.class);");
            System.out.println("3. Root<Project> root = query.from(Project.class);");
            System.out.println("4. query.select(root);");
            System.out.println("5. Predicate activeStatus = builder.equal(root.get(\"status\"), \"ACTIVE\");");
            System.out.println("6. query.where(activeStatus);");
            System.out.println("7. Order priorityOrder = builder.asc(root.get(\"priority\"));");
            System.out.println("8. Order budgetOrder = builder.desc(root.get(\"budget\"));");
            System.out.println("9. Order teamSizeOrder = builder.asc(root.get(\"teamSize\"));");
            System.out.println("10. query.orderBy(priorityOrder, budgetOrder, teamSizeOrder);");

            // 6. Выполняем запрос
            List<Project> results = session.createQuery(query).getResultList();

            // 7. Выводим результаты с указанием порядка сортировки
            System.out.println("\n--- Results (Sorted) ---");
            System.out.println("Found " + results.size() + " active project(s):");
            System.out.println("=".repeat(90));
            System.out.println(String.format("%-20s | %-12s | %-10s | %-10s | %-12s | %-10s",
                    "Project Name", "Client", "Budget", "Team Size", "Start Date", "Priority"));
            System.out.println("-".repeat(90));

            for (Project project : results) {
                System.out.println(String.format("%-20s | %-12s | $%-9.0f | %-10d | %-12s | %-10d",
                        project.getProjectName(),
                        project.getClientName(),
                        project.getBudget(),
                        project.getTeamSize(),
                        project.getStartDate(),
                        project.getPriority()
                ));
            }

            // 8. Демонстрация SQL-подобного представления
            System.out.println("\n--- Equivalent SQL ---");
            System.out.println("SELECT * FROM slide8_projects");
            System.out.println("WHERE status = 'ACTIVE'");
            System.out.println("ORDER BY priority ASC, budget DESC, team_size ASC");

            // 9. Дополнительный пример: сортировка с фильтрацией по дате
            System.out.println("\n\n--- Additional Example: Complex Filtering with Sorting ---");
            System.out.println("Query: Projects started after 2023-01-01, sorted by end date (NULLs last)");

            CriteriaQuery<Project> query2 = builder.createQuery(Project.class);
            Root<Project> root2 = query2.from(Project.class);

            // Условия WHERE
            Predicate afterDate = builder.greaterThan(root2.get("startDate"), LocalDate.of(2023, 1, 1));
            Predicate notCancelled = builder.notEqual(root2.get("status"), "CANCELLED");
            Predicate whereCondition = builder.and(afterDate, notCancelled);

            // Сортировка: по end_date (NULL last), затем по project_name
            javax.persistence.criteria.Order endDateOrder = builder.desc(root2.get("endDate"));
            javax.persistence.criteria.Order nameOrder = builder.asc(root2.get("projectName"));

            query2.select(root2)
                    .where(whereCondition)
                    .orderBy(endDateOrder, nameOrder);

            List<Project> results2 = session.createQuery(query2).getResultList();

            System.out.println("\nFound " + results2.size() + " project(s):");
            for (Project project : results2) {
                String endDateStr = project.getEndDate() != null ?
                        project.getEndDate().toString() : "NULL (ongoing)";
                System.out.println(String.format("  - %-15s | Start: %s | End: %-15s | Status: %s",
                        project.getProjectName(),
                        project.getStartDate(),
                        endDateStr,
                        project.getStatus()
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void prepareProjectData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Очистка таблицы
            session.createQuery("delete from Project").executeUpdate();

            // Создание тестовых данных
            Project p1 = new Project();
            p1.setProjectName("E-Commerce Platform");
            p1.setClientName("RetailCorp");
            p1.setBudget(new BigDecimal("250000.00"));
            p1.setTeamSize(8);
            p1.setStartDate(LocalDate.of(2023, 3, 15));
            p1.setEndDate(LocalDate.of(2024, 2, 28));
            p1.setStatus("ACTIVE");
            p1.setPriority(2);

            Project p2 = new Project();
            p2.setProjectName("Mobile Banking App");
            p2.setClientName("FinanceBank");
            p2.setBudget(new BigDecimal("180000.00"));
            p2.setTeamSize(5);
            p2.setStartDate(LocalDate.of(2023, 6, 1));
            p2.setEndDate(null); // NULL end date (ongoing)
            p2.setStatus("ACTIVE");
            p2.setPriority(1); // Highest priority

            Project p3 = new Project();
            p3.setProjectName("Inventory System");
            p3.setClientName("Warehouse Ltd");
            p3.setBudget(new BigDecimal("95000.00"));
            p3.setTeamSize(3);
            p3.setStartDate(LocalDate.of(2023, 1, 10));
            p3.setEndDate(LocalDate.of(2023, 10, 31));
            p3.setStatus("COMPLETED");
            p3.setPriority(3);

            Project p4 = new Project();
            p4.setProjectName("CRM Implementation");
            p4.setClientName("SalesForce Inc");
            p4.setBudget(new BigDecimal("320000.00"));
            p4.setTeamSize(12);
            p4.setStartDate(LocalDate.of(2022, 11, 1));
            p4.setEndDate(LocalDate.of(2024, 5, 30));
            p4.setStatus("ACTIVE");
            p4.setPriority(2);

            Project p5 = new Project();
            p5.setProjectName("Data Analytics Dashboard");
            p5.setClientName("TechAnalytics");
            p5.setBudget(new BigDecimal("145000.00"));
            p5.setTeamSize(6);
            p5.setStartDate(LocalDate.of(2023, 8, 20));
            p5.setEndDate(LocalDate.of(2024, 4, 15));
            p5.setStatus("ACTIVE");
            p5.setPriority(4);

            Project p6 = new Project();
            p6.setProjectName("Legacy Migration");
            p6.setClientName("OldSystems Corp");
            p6.setBudget(new BigDecimal("275000.00"));
            p6.setTeamSize(10);
            p6.setStartDate(LocalDate.of(2022, 9, 1));
            p6.setEndDate(null); // NULL end date
            p6.setStatus("CANCELLED");
            p6.setPriority(5);

            Project p7 = new Project();
            p7.setProjectName("AI Chatbot");
            p7.setClientName("SupportSolutions");
            p7.setBudget(new BigDecimal("210000.00"));
            p7.setTeamSize(7);
            p7.setStartDate(LocalDate.of(2023, 2, 28));
            p7.setEndDate(LocalDate.of(2023, 12, 15));
            p7.setStatus("COMPLETED");
            p7.setPriority(3);

            session.save(p1);
            session.save(p2);
            session.save(p3);
            session.save(p4);
            session.save(p5);
            session.save(p6);
            session.save(p7);

            transaction.commit();
            System.out.println("Test data: 7 projects inserted with varied statuses and dates.");
        }
    }

    public static void demonstrateSlide7() {
        System.out.println("\n=== Demo for Slide 7: All Criteria API Comparison Operators ===");
        System.out.println("Demonstrating all operators from the comparison table");

        // Подготовка тестовых данных
        prepareAccountData();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            System.out.println("\n" + "=".repeat(70));
            System.out.println("OPERATOR 1: a < b  -> builder.lt(a, b)");
            System.out.println("Query: Find accounts with balance < 5000");
            demoLtOperator(session, builder);

            System.out.println("\n" + "=".repeat(70));
            System.out.println("OPERATOR 2: a > b  -> builder.gt(a, b)");
            System.out.println("Query: Find accounts with credit limit > 10000");
            demoGtOperator(session, builder);

            System.out.println("\n" + "=".repeat(70));
            System.out.println("OPERATOR 3: a OR b  -> builder.or(a, b)");
            System.out.println("Query: Find accounts with status = 'ACTIVE' OR balance > 7000");
            demoOrOperator(session, builder);

            System.out.println("\n" + "=".repeat(70));
            System.out.println("OPERATOR 4: a AND b  -> builder.and(a, b)");
            System.out.println("Query: Find accounts with status = 'ACTIVE' AND balance > 3000");
            demoAndOperator(session, builder);

            System.out.println("\n" + "=".repeat(70));
            System.out.println("OPERATOR 5: a LIKE b  -> builder.like(a, b)");
            System.out.println("Query: Find accounts with email containing 'gmail.com'");
            demoLikeOperator(session, builder);

            System.out.println("\n" + "=".repeat(70));
            System.out.println("OPERATOR 6: a BETWEEN (c, d)  -> builder.between(a, c, d)");
            System.out.println("Query: Find accounts with balance between 2000 and 8000");
            demoBetweenOperator(session, builder);

            System.out.println("\n" + "=".repeat(70));
            System.out.println("OPERATOR 7: a IS NULL  -> builder.isNull(a)");
            System.out.println("Query: Find accounts where accountHolder is NULL");
            demoIsNullOperator(session, builder);

            System.out.println("\n" + "=".repeat(70));
            System.out.println("OPERATOR 8: a IS NOT NULL  -> builder.isNotNull(a)");
            System.out.println("Query: Find accounts where notes is NOT NULL");
            demoIsNotNullOperator(session, builder);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 1. lt() - Less Than
    private static void demoLtOperator(Session session, CriteriaBuilder builder) {
        CriteriaQuery<Account> query = builder.createQuery(Account.class);
        Root<Account> root = query.from(Account.class);

        query.select(root)
                .where(builder.lt(root.get("balance"), new BigDecimal("5000.00")));

        List<Account> results = session.createQuery(query).getResultList();
        System.out.println("Result: " + results.size() + " account(s) found");
        results.forEach(a -> System.out.println("  - #" + a.getAccountNumber() +
                ", Balance: $" + a.getBalance()));
    }

    // 2. gt() - Greater Than
    private static void demoGtOperator(Session session, CriteriaBuilder builder) {
        CriteriaQuery<Account> query = builder.createQuery(Account.class);
        Root<Account> root = query.from(Account.class);

        query.select(root)
                .where(builder.gt(root.get("creditLimit"), new BigDecimal("10000.00")));

        List<Account> results = session.createQuery(query).getResultList();
        System.out.println("Result: " + results.size() + " account(s) found");
        results.forEach(a -> System.out.println("  - #" + a.getAccountNumber() +
                ", Limit: $" + a.getCreditLimit()));
    }

    // 3. or() - Logical OR
    private static void demoOrOperator(Session session, CriteriaBuilder builder) {
        CriteriaQuery<Account> query = builder.createQuery(Account.class);
        Root<Account> root = query.from(Account.class);

        Predicate activeStatus = builder.equal(root.get("status"), "ACTIVE");
        Predicate highBalance = builder.gt(root.get("balance"), new BigDecimal("7000.00"));

        query.select(root)
                .where(builder.or(activeStatus, highBalance));

        List<Account> results = session.createQuery(query).getResultList();
        System.out.println("Result: " + results.size() + " account(s) found");
        results.forEach(a -> System.out.println("  - #" + a.getAccountNumber() +
                ", Status: " + a.getStatus() +
                ", Balance: $" + a.getBalance()));
    }

    // 4. and() - Logical AND
    private static void demoAndOperator(Session session, CriteriaBuilder builder) {
        CriteriaQuery<Account> query = builder.createQuery(Account.class);
        Root<Account> root = query.from(Account.class);

        Predicate activeStatus = builder.equal(root.get("status"), "ACTIVE");
        Predicate minBalance = builder.gt(root.get("balance"), new BigDecimal("3000.00"));

        query.select(root)
                .where(builder.and(activeStatus, minBalance));

        List<Account> results = session.createQuery(query).getResultList();
        System.out.println("Result: " + results.size() + " account(s) found");
        results.forEach(a -> System.out.println("  - #" + a.getAccountNumber() +
                ", Status: " + a.getStatus() +
                ", Balance: $" + a.getBalance()));
    }

    // 5. like() - Pattern Matching
    private static void demoLikeOperator(Session session, CriteriaBuilder builder) {
        CriteriaQuery<Account> query = builder.createQuery(Account.class);
        Root<Account> root = query.from(Account.class);

        query.select(root)
                .where(builder.like(root.get("email"), "%gmail.com%"));

        List<Account> results = session.createQuery(query).getResultList();
        System.out.println("Result: " + results.size() + " account(s) found");
        results.forEach(a -> System.out.println("  - #" + a.getAccountNumber() +
                ", Email: " + a.getEmail()));
    }

    // 6. between() - Range Check
    private static void demoBetweenOperator(Session session, CriteriaBuilder builder) {
        CriteriaQuery<Account> query = builder.createQuery(Account.class);
        Root<Account> root = query.from(Account.class);

        query.select(root)
                .where(builder.between(
                        root.get("balance"),
                        new BigDecimal("2000.00"),
                        new BigDecimal("8000.00")
                ));

        List<Account> results = session.createQuery(query).getResultList();
        System.out.println("Result: " + results.size() + " account(s) found");
        results.forEach(a -> System.out.println("  - #" + a.getAccountNumber() +
                ", Balance: $" + a.getBalance()));
    }

    // 7. isNull() - NULL Check
    private static void demoIsNullOperator(Session session, CriteriaBuilder builder) {
        CriteriaQuery<Account> query = builder.createQuery(Account.class);
        Root<Account> root = query.from(Account.class);

        query.select(root)
                .where(builder.isNull(root.get("accountHolder")));

        List<Account> results = session.createQuery(query).getResultList();
        System.out.println("Result: " + results.size() + " account(s) found");
        results.forEach(a -> System.out.println("  - #" + a.getAccountNumber() +
                ", Holder: " + (a.getAccountHolder() == null ? "NULL" : a.getAccountHolder())));
    }

    // 8. isNotNull() - NOT NULL Check
    private static void demoIsNotNullOperator(Session session, CriteriaBuilder builder) {
        CriteriaQuery<Account> query = builder.createQuery(Account.class);
        Root<Account> root = query.from(Account.class);

        query.select(root)
                .where(builder.isNotNull(root.get("notes")));

        List<Account> results = session.createQuery(query).getResultList();
        System.out.println("Result: " + results.size() + " account(s) found");
        results.forEach(a -> System.out.println("  - #" + a.getAccountNumber() +
                ", Notes: \"" + a.getNotes() + "\""));
    }

    private static void prepareAccountData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Очистка таблицы
            session.createQuery("delete from Account").executeUpdate();

            // Создание тестовых данных
            Account a1 = new Account();
            a1.setAccountNumber("ACC001");
            a1.setAccountHolder("John Smith");
            a1.setBalance(new BigDecimal("4500.00"));
            a1.setCreditLimit(new BigDecimal("15000.00"));
            a1.setStatus("ACTIVE");
            a1.setEmail("john@gmail.com");
            a1.setLastActivity(LocalDateTime.now().minusDays(2));
            a1.setNotes("Preferred customer");

            Account a2 = new Account();
            a2.setAccountNumber("ACC002");
            a2.setAccountHolder("Emma Wilson");
            a2.setBalance(new BigDecimal("12000.00"));
            a2.setCreditLimit(new BigDecimal("8000.00"));
            a2.setStatus("INACTIVE");
            a2.setEmail("emma@yahoo.com");
            a2.setLastActivity(LocalDateTime.now().minusMonths(3));
            a2.setNotes(null);

            Account a3 = new Account();
            a3.setAccountNumber("ACC003");
            a3.setAccountHolder(null); // NULL holder
            a3.setBalance(new BigDecimal("2500.00"));
            a3.setCreditLimit(new BigDecimal("5000.00"));
            a3.setStatus("ACTIVE");
            a3.setEmail("corporate@company.com");
            a3.setLastActivity(LocalDateTime.now().minusDays(10));
            a3.setNotes("Corporate account");

            Account a4 = new Account();
            a4.setAccountNumber("ACC004");
            a4.setAccountHolder("Alex Johnson");
            a4.setBalance(new BigDecimal("7500.00"));
            a4.setCreditLimit(new BigDecimal("12000.00"));
            a4.setStatus("BLOCKED");
            a4.setEmail("alex@gmail.com");
            a4.setLastActivity(LocalDateTime.now().minusDays(30));
            a4.setNotes("Under review");

            Account a5 = new Account();
            a5.setAccountNumber("ACC005");
            a5.setAccountHolder("Michael Brown");
            a5.setBalance(new BigDecimal("3000.00"));
            a5.setCreditLimit(new BigDecimal("6000.00"));
            a5.setStatus("ACTIVE");
            a5.setEmail("michael@outlook.com");
            a5.setLastActivity(LocalDateTime.now().minusDays(1));
            a5.setNotes(null);

            session.save(a1);
            session.save(a2);
            session.save(a3);
            session.save(a4);
            session.save(a5);

            transaction.commit();
            System.out.println("Test data: 5 accounts inserted with varied data for demos.");
        }
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
