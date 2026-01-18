package com.javarush;

import com.javarush.entity.*;
import com.javarush.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;
import java.util.TimeZone;

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

        demonstrateSlide11();

        demonstrateSlide12();

        demonstrateSlide13();

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

    private static void demonstrateSlide10() {
        System.out.println("\n=== Слайд 10: @GeneratedValue ===");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            System.out.println("📌 Пример 1: IDENTITY (автоинкремент в MySQL)");
            IdentityItem item1 = new IdentityItem("Item 1");
            IdentityItem item2 = new IdentityItem("Item 2");

            System.out.println("  Before save - ID 1: " + item1.getId());
            System.out.println("  Before save - ID 2: " + item2.getId());

            session.save(item1);
            session.save(item2);

            System.out.println("  After save - ID 1: " + item1.getId());
            System.out.println("  After save - ID 2: " + item2.getId());

            System.out.println("\n📌 Пример 2: SEQUENCE (эмуляция для MySQL)");
            SequenceItem seqItem = new SequenceItem("Sequence Item");
            session.save(seqItem);
            System.out.println("  Sequence Item ID: " + seqItem.getId());

            System.out.println("\n📌 Пример 3: TABLE (отдельная таблица генерации)");
            TableItem tableItem = new TableItem("Table Item");
            session.save(tableItem);
            System.out.println("  Table Item ID: " + tableItem.getId());

            transaction.commit();
        }
    }

    private static void demonstrateSlide11() {
        System.out.println("\n=== Слайд 11: Маппинг дат с @Temporal ===");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Создаём объект Date для демонстрации
            Date now = new Date();

            // Форматируем для наглядности
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            System.out.println("📅 Исходная дата (java.util.Date):");
            System.out.println("  Полная дата: " + timestampFormat.format(now));
            System.out.println("  Только дата: " + dateFormat.format(now));
            System.out.println("  Только время: " + timeFormat.format(now));

            // Создаём событие
            LegacyEvent event = new LegacyEvent(
                    "Team Meeting",
                    now,   // eventTimestamp -> TIMESTAMP (по умолчанию)
                    now,   // eventDate -> DATE (только дата)
                    now,   // eventTime -> TIME (только время)
                    now    // eventTimestampExplicit -> TIMESTAMP (явно)
            );

            session.save(event);
            transaction.commit();

            System.out.println("\n✅ LegacyEvent сохранён с разными @Temporal типами:");
            System.out.println(event);

            // Загружаем обратно
            session.clear();
            LegacyEvent loadedEvent = session.get(LegacyEvent.class, event.getId());

            System.out.println("\n📦 Загружено из базы:");
            System.out.println("  eventTimestamp: " + loadedEvent.getEventTimestamp());
            System.out.println("  eventDate: " + loadedEvent.getEventDate());
            System.out.println("  eventTime: " + loadedEvent.getEventTime());
            System.out.println("  eventTimestampExplicit: " + loadedEvent.getEventTimestampExplicit());

            // Покажем разницу в форматировании
            System.out.println("\n📊 Как хранится в SQL:");
            System.out.println("  event_timestamp -> TIMESTAMP: " + timestampFormat.format(loadedEvent.getEventTimestamp()));
            System.out.println("  event_date -> DATE: " + dateFormat.format(loadedEvent.getEventDate()));
            System.out.println("  event_time -> TIME: " + timeFormat.format(loadedEvent.getEventTime()));
        }

    }

    private static void demonstrateSlide12() {
        System.out.println("\n=== Слайд 12: Современный маппинг дат (java.time) ===");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Создаём различные java.time объекты
            LocalDate localDate = LocalDate.of(2024, 1, 18);
            LocalTime localTime = LocalTime.of(14, 30, 45);
            LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
            Instant instant = Instant.now();
            OffsetDateTime offsetDateTime = OffsetDateTime.of(localDateTime, ZoneOffset.ofHours(3));
            ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("Europe/Moscow"));

            System.out.println("🕐 Созданы java.time объекты:");
            System.out.println("  LocalDate: " + localDate);
            System.out.println("  LocalTime: " + localTime);
            System.out.println("  LocalDateTime: " + localDateTime);
            System.out.println("  Instant: " + instant);
            System.out.println("  OffsetDateTime (+03:00): " + offsetDateTime);
            System.out.println("  ZonedDateTime (Europe/Moscow): " + zonedDateTime);

            // Создаём современное событие
            ModernEvent event = new ModernEvent(
                    "Java Conference 2024",
                    localDate,
                    localTime,
                    localDateTime,
                    instant,
                    offsetDateTime,
                    zonedDateTime
            );

            session.save(event);
            transaction.commit();

            System.out.println("\n✅ ModernEvent сохранён с java.time типами:");
            System.out.println(event);

            // Загружаем обратно
            session.clear();
            ModernEvent loadedEvent = session.get(ModernEvent.class, event.getId());

            System.out.println("\n📦 Загружено из базы:");
            System.out.println("  eventDate (LocalDate): " + loadedEvent.getEventDate());
            System.out.println("  eventTime (LocalTime): " + loadedEvent.getEventTime());
            System.out.println("  eventDateTime (LocalDateTime): " + loadedEvent.getEventDateTime());
            System.out.println("  eventInstant (Instant): " + loadedEvent.getEventInstant());
            System.out.println("  eventOffset (OffsetDateTime): " + loadedEvent.getEventOffset());
            System.out.println("  eventZoned (ZonedDateTime): " + loadedEvent.getEventZoned());

            // Покажем SQL-типы
            System.out.println("\n📊 Соответствие Java типов → SQL типов:");
            System.out.println("  LocalDate → DATE");
            System.out.println("  LocalTime → TIME");
            System.out.println("  LocalDateTime → TIMESTAMP");
            System.out.println("  Instant → TIMESTAMP (UTC)");
            System.out.println("  OffsetDateTime → TIMESTAMP WITH TIME ZONE");
            System.out.println("  ZonedDateTime → TIMESTAMP WITH TIME ZONE");
        }
    }

    private static void demonstrateSlide13() {
        System.out.println("\n=== Слайд 13: Установка временной зоны ===");

        // Покажем текущие настройки
        System.out.println("📊 Текущие настройки часовых поясов:");
        System.out.println("  1. JVM TimeZone: " + TimeZone.getDefault().getID());
        System.out.println("  2. System default: " + ZoneId.systemDefault());
        System.out.println("  3. Current instant (UTC): " + Instant.now());
        System.out.println("  4. Local time in Moscow: " +
                ZonedDateTime.now(ZoneId.of("Europe/Moscow")));

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Создаём объекты в разных поясах
            LocalDate localDate = LocalDate.of(2024, 6, 15); // Без пояса
            LocalDateTime localDateTime = LocalDateTime.of(2024, 6, 15, 14, 30); // Без пояса
            Instant instant = Instant.now(); // Всегда UTC
            OffsetDateTime offsetDateTime = OffsetDateTime.of(
                    localDateTime,
                    ZoneOffset.ofHours(3) // +03:00
            );
            Date utilDate = new Date(); // Зависит от JVM timezone

            TimeZoneDemo demo = new TimeZoneDemo(
                    "Timezone Demo",
                    localDate,
                    localDateTime,
                    instant,
                    offsetDateTime,
                    utilDate
            );

            System.out.println("\n📝 Сохраняемый объект:");
            System.out.println("  LocalDate: " + demo.getLocalDate());
            System.out.println("  LocalDateTime: " + demo.getLocalDateTime());
            System.out.println("  Instant: " + demo.getInstant());
            System.out.println("  OffsetDateTime: " + demo.getOffsetDateTime());
            System.out.println("  UtilDate: " + demo.getUtilDate());

            session.save(demo);
            transaction.commit();

            System.out.println("\n✅ Объект сохранён в БД");

            // Загружаем обратно
            session.clear();
            TimeZoneDemo loaded = session.get(TimeZoneDemo.class, demo.getId());

            System.out.println("\n📦 Загружено из БД:");
            System.out.println("  LocalDate: " + loaded.getLocalDate());
            System.out.println("  LocalDateTime: " + loaded.getLocalDateTime());
            System.out.println("  Instant: " + loaded.getInstant());
            System.out.println("  OffsetDateTime: " + loaded.getOffsetDateTime());
            System.out.println("  UtilDate: " + loaded.getUtilDate());

            // Демонстрация проблемы с Date
            System.out.println("\n⚠️  Потенциальные проблемы:");
            System.out.println("  - java.util.Date зависит от JVM TimeZone");
            System.out.println("  - Если JVM TimeZone поменяется, Date будет интерпретироваться по-другому");
            System.out.println("  - Решение: использовать Instant или хранить в UTC");

            System.out.println("\n🎯 Рекомендации:");
            System.out.println("  1. Храните даты в БД в UTC");
            System.out.println("  2. Установите JVM TimeZone: -Duser.timezone=UTC");
            System.out.println("  3. Используйте java.time вместо java.util.Date");
            System.out.println("  4. Для Hibernate можно установить: hibernate.jdbc.time_zone=UTC");
        }
    }

}
