package com.javarush;

import com.javarush.entity.*;
import com.javarush.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

        demonstrateSlide14();

        demonstrateSlide16();

        demonstrateSlide19();

        HibernateUtil.shutdown();
    }

    public static void demonstrateSlide20() {
        System.out.println("\n=== Слайд 20: Fetch Types Demo (LAZY vs EAGER) ===");

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // 1. Создаем данные для демонстрации
            System.out.println("1. Creating test data...");

            Blog blog = new Blog("Java Programming Blog", "John Doe");

            BlogPost post1 = new BlogPost("Hibernate Basics", "Hibernate is a powerful ORM framework...");
            BlogPost post2 = new BlogPost("Spring Introduction", "Spring Framework makes Java EE easier...");

            BlogComment comment1 = new BlogComment("Alice", "Great article about Hibernate!");
            BlogComment comment2 = new BlogComment("Bob", "Thanks for the Spring tutorial.");
            BlogComment comment3 = new BlogComment("Charlie", "Very helpful, thanks!");

            // Устанавливаем связи
            blog.addPost(post1);
            blog.addPost(post2);
            blog.addComment(comment1);
            blog.addComment(comment2);
            blog.addComment(comment3);

            session.save(blog);

            transaction.commit();

            // 2. Демонстрация EAGER загрузки OneToMany
            System.out.println("\n2. Demonstrating EAGER fetching (Blog -> Posts - fetch = EAGER):");
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            Blog loadedBlog = session.get(Blog.class, blog.getId());
            System.out.println("   Blog loaded: " + loadedBlog.getTitle());

            // Посты загрузятся сразу (EAGER), даже без явного обращения
            System.out.println("   Posts count: " + loadedBlog.getPosts().size());
            System.out.println("   First post title: " + loadedBlog.getPosts().get(0).getTitle());

            // Посмотрим SQL в логах - будет JOIN для posts

            transaction.commit();

            // 3. Демонстрация LAZY загрузки OneToMany
            System.out.println("\n3. Demonstrating LAZY fetching (Blog -> Comments - fetch = LAZY):");
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            Blog blogForComments = session.get(Blog.class, blog.getId());
            System.out.println("   Blog loaded: " + blogForComments.getTitle());

            // Комментарии НЕ загружены сразу (LAZY)
            System.out.println("   Comments initialized: " +
                    org.hibernate.Hibernate.isInitialized(blogForComments.getComments()));

            // При обращении к комментариям - выполняется отдельный запрос
            System.out.println("   Comments count: " + blogForComments.getComments().size());
            System.out.println("   First comment author: " + blogForComments.getComments().get(0).getAuthor());

            transaction.commit();

            // 4. Демонстрация LAZY загрузки ManyToOne
            System.out.println("\n4. Demonstrating LAZY fetching (BlogPost -> Blog - fetch = LAZY):");
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            BlogPost loadedPost = session.get(BlogPost.class, post1.getId());
            System.out.println("   Post loaded: " + loadedPost.getTitle());

            // Блог не загружен сразу (LAZY), только прокси
            System.out.println("   Blog is proxy: " +
                    (loadedPost.getBlog() instanceof org.hibernate.proxy.HibernateProxy));

            // При обращении к блогу - выполняется отдельный запрос
            System.out.println("   Blog author: " + loadedPost.getBlog().getAuthor());

            transaction.commit();

            // 5. Демонстрация EAGER загрузки ManyToOne (по умолчанию)
            System.out.println("\n5. Demonstrating EAGER fetching (BlogComment -> Blog - default for @ManyToOne):");
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            BlogComment loadedComment = session.get(BlogComment.class, comment1.getId());
            System.out.println("   Comment loaded: " + loadedComment.getText());

            // Блог загружен сразу (EAGER по умолчанию для @ManyToOne)
            System.out.println("   Blog is proxy: " +
                    (loadedComment.getBlog() instanceof org.hibernate.proxy.HibernateProxy));
            System.out.println("   Blog title: " + loadedComment.getBlog().getTitle());

            transaction.commit();

            // 6. Проблема LazyInitializationException
            System.out.println("\n6. Demonstrating LazyInitializationException risk:");
            session = HibernateUtil.getSessionFactory().openSession();

            BlogPost postForException = session.get(BlogPost.class, post2.getId());
            session.close(); // Закрываем сессию

            try {
                // Попытка доступа к ленивой связи вне сессии
                System.out.println("   Trying to access blog outside session...");
                String blogAuthor = postForException.getBlog().getAuthor();
                System.out.println("   Success: " + blogAuthor);
            } catch (Exception e) {
                System.out.println("   Error: " + e.getClass().getSimpleName() +
                        " - " + e.getMessage());
            }

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        System.out.println("\n=== End of Slide 20 Demo ===");
    }

    public static void demonstrateSlide19() {
        System.out.println("\n=== Слайд 19: Orphan Removal Demo ===");

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // 1. Создаем геймера с профилем
            System.out.println("1. Creating gamer with profile...");
            Gamer gamer = new Gamer("CyberWarrior", "cyber@example.com");
            GameProfile profile = new GameProfile("avatar1.jpg", 1, 0);

            // Устанавливаем двустороннюю связь
            gamer.setProfile(profile);
            profile.setOwner(gamer);

            session.save(gamer);
            transaction.commit();

            // 2. Показываем, что профиль существует
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            Gamer savedGamer = session.get(Gamer.class, gamer.getId());
            System.out.println("2. Gamer has profile: " +
                    (savedGamer.getProfile() != null ? "Yes" : "No"));

            // 3. Создаем новый профиль и заменяем старый
            System.out.println("3. Replacing old profile with new one...");
            GameProfile newProfile = new GameProfile("avatar2.jpg", 2, 100);
            newProfile.setOwner(savedGamer);
            savedGamer.setProfile(newProfile);

            // Здесь сработает orphanRemoval - старый профиль удалится
            session.saveOrUpdate(savedGamer);
            transaction.commit();

            // 4. Проверяем, удалился ли старый профиль
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            Query<GameProfile> query = session.createQuery(
                    "FROM GameProfile WHERE avatarUrl = :url", GameProfile.class);
            query.setParameter("url", "avatar1.jpg");
            List<GameProfile> oldProfiles = query.list();

            System.out.println("4. Old profile still exists: " +
                    (!oldProfiles.isEmpty() ? "Yes" : "No") +
                    " (found " + oldProfiles.size() + " profiles)");

            // 5. Устанавливаем профиль в null
            System.out.println("5. Setting profile to null...");
            savedGamer = session.get(Gamer.class, gamer.getId());
            savedGamer.setProfile(null);

            session.saveOrUpdate(savedGamer);
            transaction.commit();

            // 6. Проверяем, удалился ли новый профиль
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            query = session.createQuery(
                    "FROM GameProfile WHERE avatarUrl = :url", GameProfile.class);
            query.setParameter("url", "avatar2.jpg");
            List<GameProfile> newProfiles = query.list();

            System.out.println("6. New profile still exists: " +
                    (!newProfiles.isEmpty() ? "Yes" : "No") +
                    " (found " + newProfiles.size() + " profiles)");

            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        System.out.println("=== End of Slide 19 Demo ===\n");
    }

    private static void demonstrateSlide16() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            System.out.println("\n✅ CascadeType Examples");

            // === 1. CascadeType.ALL (сохранение) ===
            System.out.println("\n1. CascadeType.ALL - автоматическое сохранение:");

            Order order = new Order("ORD-001", LocalDateTime.now(), "Иван Иванов");

            // Создаём items (НЕ сохраняем их явно!)
            OrderItem item1 = new OrderItem("Ноутбук", 1, new BigDecimal("1500.00"), new BigDecimal("10"));
            OrderItem item2 = new OrderItem("Мышь", 2, new BigDecimal("50.00"), new BigDecimal("5"));

            // Добавляем items в order
            order.addItem(item1);
            order.addItem(item2);

            // Сохраняем только order - items сохранятся каскадно
            session.save(order);

            System.out.println("   Сохранён Order: " + order.getOrderNumber());
            System.out.println("   Items сохранены автоматически: " + order.getItems().size());

            // === 2. CascadeType.MERGE (обновление) ===
            System.out.println("\n2. CascadeType.MERGE - автоматическое обновление:");

            // Изменяем данные в detached состоянии
            order.setCustomerName("Иван Петров");
            item1.setDiscount(new BigDecimal("15")); // скидка 15% вместо 10%

            // Обновляем только order - item1 обновится каскадно
            session.merge(order);

            System.out.println("   Order обновлён: " + order.getCustomerName());
            System.out.println("   Item обновлён автоматически: скидка " + item1.getDiscount() + "%");

            // === 3. CascadeType.REMOVE (удаление) ===
            System.out.println("\n3. CascadeType.REMOVE - автоматическое удаление:");

            // Удаляем order - все items удалятся каскадно
            session.delete(order);

            System.out.println("   Order удалён");
            System.out.println("   Items удалены автоматически");

            // === 4. Проверка разных cascade комбинаций ===
            System.out.println("\n4. Разные cascade комбинации:");

            Order order2 = new Order("ORD-002", LocalDateTime.now(), "Петр Сидоров");
            OrderItem item3 = new OrderItem("Клавиатура", 1, new BigDecimal("100.00"), BigDecimal.ZERO);

            // Проверяем PERSIST без MERGE
            order2.addItem(item3);
            session.save(order2);

            // Пытаемся изменить без MERGE (должно работать, т.к. объект attached)
            item3.setPrice(new BigDecimal("120.00"));

            System.out.println("   Order сохранён с PERSIST cascade");
            System.out.println("   Item цена изменена: " + item3.getPrice());

            tx.commit();

            // === 5. Проверяем orphanRemoval ===
            System.out.println("\n5. orphanRemoval = true:");
            System.out.println("   Если удалить item из коллекции, он удалится из БД");
            System.out.println("   Пример: order.getItems().remove(item) → item удаляется");

            // === 6. Показываем SQL логи ===
            System.out.println("\n📊 Посмотрите SQL логи в консоли:");
            System.out.println("   При сохранении Order: INSERT в orders + INSERTs в order_items");
            System.out.println("   При удалении Order: DELETE из orders + DELETEs из order_items");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void demonstrateSlide14() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            System.out.println("\n✅ Двусторонний OneToOne Example (Patient ↔ MedicalCard)");

            // 1. Создаём медицинские карты
            MedicalCard card1 = new MedicalCard(
                    "A+",
                    "Пенициллин, орехи",
                    LocalDateTime.of(2024, 1, 15, 10, 30)
            );

            MedicalCard card2 = new MedicalCard(
                    "O-",
                    "Нет",
                    LocalDateTime.of(2024, 2, 20, 14, 45)
            );

            // 2. Создаём пациентов
            Patient patient1 = new Patient(
                    "Ирина Петрова",
                    LocalDate.of(1990, 5, 12),
                    "INS123456"
            );

            Patient patient2 = new Patient(
                    "Сергей Козлов",
                    LocalDate.of(1985, 8, 25),
                    "INS789012"
            );

            // 3. Устанавливаем ДВУСТОРОННЮЮ связь
            patient1.setMedicalCard(card1);
            card1.setPatient(patient1);

            patient2.setMedicalCard(card2);
            card2.setPatient(patient2);

            // 4. Сохраняем пациентов (медкарты сохранятся каскадно)
            session.save(patient1);
            session.save(patient2);

            tx.commit();

            System.out.println("\n📊 Данные сохранены:");
            System.out.println("   Пациент 1: " + patient1.getFullName() +
                    " (страховка: " + patient1.getInsuranceNumber() + ")");
            System.out.println("   Медкарта 1: группа крови " + card1.getBloodType() +
                    ", аллергии: " + card1.getAllergies());

            System.out.println("   Пациент 2: " + patient2.getFullName() +
                    " (страховка: " + patient2.getInsuranceNumber() + ")");
            System.out.println("   Медкарта 2: группа крови " + card2.getBloodType() +
                    ", аллергии: " + card2.getAllergies());

            // 5. Демонстрация двусторонней связи
            System.out.println("\n🔍 Двусторонняя связь:");
            System.out.println("   Пациент → Медкарта: " +
                    patient1.getFullName() + " имеет группу крови " +
                    patient1.getMedicalCard().getBloodType());
            System.out.println("   Медкарта → Пациент: " +
                    card1.getBloodType() + " принадлежит " +
                    card1.getPatient().getFullName());

            // 6. Показываем структуру БД
            System.out.println("\n📈 Структура базы данных:");
            System.out.println("   Таблица patients:");
            System.out.println("     id | full_name | date_of_birth | insurance_number | medical_card_id (FK)");
            System.out.println("   Таблица medical_cards:");
            System.out.println("     id | blood_type | allergies | last_checkup_date");
            System.out.println("   Внимание: В medical_cards НЕТ patient_id!");
            System.out.println("   Связь через patients.medical_card_id → medical_cards.id");

            // 7. Загружаем данные и показываем, что связь работает в обе стороны
            session.clear();

            Patient loadedPatient = session.get(Patient.class, patient1.getId());
            MedicalCard loadedCard = session.get(MedicalCard.class, card1.getId());

            System.out.println("\n🔄 Загружено из базы:");
            System.out.println("   Пациент знает свою медкарту: " +
                    (loadedPatient.getMedicalCard() != null ? "Да" : "Нет"));
            System.out.println("   Медкарта знает своего пациента: " +
                    (loadedCard.getPatient() != null ? "Да" : "Нет"));
            System.out.println("   Это один и тот же пациент: " +
                    loadedPatient.getFullName().equals(loadedCard.getPatient().getFullName()));

        } catch (Exception e) {
            e.printStackTrace();
        }
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
