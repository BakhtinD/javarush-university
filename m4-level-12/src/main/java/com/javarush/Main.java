package com.javarush;

import com.javarush.entity.*;
import com.javarush.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

public class Main {

    public static void main(String[] args) {

        demonstrateSlide3();

        demonstrateSlide4();

        demonstrateSlide5();

        demonstrateSlide6();

        demonstrateSlide7();

        demonstrateSlide8();

        demonstrateSlide9();

        HibernateUtil.shutdown();
    }

    private static void demonstrateSlide3() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Создаём пользователя с разными типами данных
            User user = new User(
                    "ivan_java",
                    "ivan@example.com",
                    42,
                    true,                           // boolean
                    95.5,                           // Double
                    new BigDecimal("55000.75"),     // BigDecimal
                    LocalDate.of(1990, 5, 15),      // LocalDate
                    new Date(),                     // Date
                    "avatar_data".getBytes()        // byte[]
            );

            session.save(user);
            transaction.commit();

            System.out.println("✅ Пользователь сохранён с разными типами данных:");
            System.out.println(user);
        }
    }

    private static void demonstrateSlide4() {
        System.out.println("\n=== Слайд 4: @Type (Boolean маппинг) ===");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Document doc = new Document(
                    "Hibernate Guide",
                    true,        // isSigned -> 'Y'
                    false,       // isArchived -> 0
                    true,        // isPublic -> 1 (BIT)
                    LocalDate.of(2024, 1, 18)
            );

            session.save(doc);
            transaction.commit();

            System.out.println("✅ Документ сохранён с разными Boolean-маппингами:");
            System.out.println(doc);
        }
    }

    private static void demonstrateSlide5() {
        System.out.println("\n=== Слайд 5: Маппинг enum ===");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Создаём продукт с двумя вариантами маппинга enum
            ProductWithEnum product = new ProductWithEnum(
                    "Laptop",
                    ProductCategory.ELECTRONICS, // categoryOrdinal -> 0
                    ProductCategory.ELECTRONICS   // categoryString -> "ELECTRONICS"
            );

            session.save(product);
            transaction.commit();

            System.out.println("✅ Продукт сохранён с enum:");
            System.out.println(product);

            // Загрузим обратно, чтобы убедиться, что маппинг работает
            ProductWithEnum loadedProduct = session.get(ProductWithEnum.class, product.getId());
            System.out.println("📦 Загружено из БД:");
            System.out.println("  categoryOrdinal: " + loadedProduct.getCategoryOrdinal());
            System.out.println("  categoryString: " + loadedProduct.getCategoryString());
        }

    }

    private static void demonstrateSlide6() {
        System.out.println("\n=== Слайд 6: Маппинг Boolean ===");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Создаём вопрос викторины с разными Boolean-маппингами
            QuizQuestion question = new QuizQuestion(
                    "Is Java an object-oriented language?",
                    true,      // isActive -> BIT/TINYINT (1)
                    true,      // isApproved -> numeric_boolean (1)
                    true,      // isVerified -> yes_no ('Y')
                    true,      // isCorrect -> BIT через @Type
                    'T'        // isPublic -> CHAR(1) 'T'
            );

            session.save(question);
            transaction.commit();

            System.out.println("✅ Вопрос викторины сохранён с разными Boolean-маппингами:");
            System.out.println(question);

            // Проверим SQL-логи
            System.out.println("\n📊 В SQL это выглядит так:");
            System.out.println("- is_active: 1 (BIT/TINYINT)");
            System.out.println("- is_approved: 1 (numeric_boolean)");
            System.out.println("- is_verified: 'Y' (yes_no)");
            System.out.println("- is_correct: 1 (BIT через NumericBooleanType)");
            System.out.println("- is_public: 'T' (CHAR(1))");
        }
    }

    private static void demonstrateSlide7() {
        System.out.println("\n=== Слайд 7: Вычисляемые поля (@Transient, @Formula) ===");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Создаём прямоугольник
            Rectangle rectangle = new Rectangle(
                    "My Rectangle",
                    10,   // width
                    5     // height
                    // perimeter вычисляется в конструкторе = (10+5)*2 = 30
                    // area вычисляется через @Formula = 10*5 = 50
                    // shapeType вычисляется через @Formula = 'WIDE' (width > height)
            );

            System.out.println("📐 Before saving:");
            System.out.println("  Name: " + rectangle.getName());
            System.out.println("  Width: " + rectangle.getWidth());
            System.out.println("  Height: " + rectangle.getHeight());
            System.out.println("  Perimeter (@Transient): " + rectangle.getPerimeter());
            System.out.println("  Area (@Formula): " + rectangle.getArea());
            System.out.println("  Shape Type (@Formula): " + rectangle.getShapeType());

            session.save(rectangle);
            transaction.commit();

            System.out.println("\n✅ Rectangle saved to database");

            // Очистим кэш и загрузим заново, чтобы увидеть @Formula в действии
            session.clear();

            Rectangle loadedRectangle = session.get(Rectangle.class, rectangle.getId());
            System.out.println("\n📦 Loaded from database:");
            System.out.println("  Name: " + loadedRectangle.getName());
            System.out.println("  Width: " + loadedRectangle.getWidth());
            System.out.println("  Height: " + loadedRectangle.getHeight());
            System.out.println("  Perimeter (@Transient): " + loadedRectangle.getPerimeter() + " (lost after load)");
            System.out.println("  Area (@Formula): " + loadedRectangle.getArea() + " (calculated by DB)");
            System.out.println("  Shape Type (@Formula): " + loadedRectangle.getShapeType() + " (calculated by DB)");
        }
    }

    private static void demonstrateSlide8() {
        System.out.println("\n=== Слайд 8: @Embedded и @Embeddable ===");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Создаём встроенные объекты
            FullName fullName = new FullName("John", "Doe", "Michael");

            Address homeAddress = new Address(
                    "USA",
                    "New York",
                    "5th Avenue",
                    "123",
                    "10001"
            );

            Address deliveryAddress = new Address(
                    "USA",
                    "Brooklyn",
                    "Park Slope",
                    "456",
                    "11215"
            );

            // Создаём клиента с встроенными объектами
            Customer customer = new Customer(
                    fullName,
                    homeAddress,
                    deliveryAddress,
                    "john.doe@example.com",
                    LocalDate.now()
            );

            session.save(customer);
            transaction.commit();

            System.out.println("✅ Customer saved with embedded objects:");
            System.out.println(customer);

            // Загружаем и проверяем
            session.clear();
            Customer loadedCustomer = session.get(Customer.class, customer.getId());

            System.out.println("\n📦 Loaded from database:");
            System.out.println("  Full Name: " + loadedCustomer.getFullName());
            System.out.println("  Home Address: " + loadedCustomer.getAddress());
            System.out.println("  Delivery Address: " + loadedCustomer.getDeliveryAddress());
            System.out.println("  Email: " + loadedCustomer.getEmail());
            System.out.println("  Registration Date: " + loadedCustomer.getRegistrationDate());

            // Покажем структуру таблицы
            System.out.println("\n📊 Table structure (columns):");
            System.out.println("  - first_name, last_name, middle_name (from FullName)");
            System.out.println("  - country, city, street, house_number, zip_code (from Address)");
            System.out.println("  - delivery_country, delivery_city, delivery_street, delivery_house_number, delivery_zip_code (from Address with @AttributeOverrides)");
            System.out.println("  - email, registration_date");
        }
    }

    private static void demonstrateSlide9() {
        System.out.println("\n=== Слайд 9: Аннотация @Id ===");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            System.out.println("📌 Пример 1: Simple Integer ID");
            SimpleItem item1 = new SimpleItem("Laptop");
            item1.setId(1);
            session.save(item1);
            System.out.println("  Saved: " + item1);

            System.out.println("\n📌 Пример 2: UUID ID");
            UuidItem item2 = new UuidItem("Smartphone");
            session.save(item2);
            System.out.println("  Saved: " + item2);
            System.out.println("  UUID: " + item2.getId());

            System.out.println("\n📌 Пример 3: Composite ID with @EmbeddedId");
            ProjectAssignment assignment = new ProjectAssignment(
                    "PROJ-2024",
                    1001,
                    "Lead Developer",
                    LocalDate.of(2024, 1, 1)
            );
            session.save(assignment);
            System.out.println("  Saved: " + assignment);

            transaction.commit();

            // Загружаем обратно
            System.out.println("\n📦 Загружаем из базы:");

            SimpleItem loaded1 = session.get(SimpleItem.class, 1);
            System.out.println("  SimpleItem: " + loaded1);

            UuidItem loaded2 = session.get(UuidItem.class, item2.getId());
            System.out.println("  UuidItem: " + loaded2);

            // Для @EmbeddedId
            AssignmentId assignmentId = new AssignmentId("PROJ-2024", 1001);
            ProjectAssignment loaded3 = session.get(ProjectAssignment.class, assignmentId);
            System.out.println("  ProjectAssignment: " + loaded3);
        }
    }

}