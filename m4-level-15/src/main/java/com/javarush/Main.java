package com.javarush;

import com.javarush.entity.Product;
import com.javarush.entity.User;
import com.javarush.entity.discriminator.Contract;
import com.javarush.entity.discriminator.Document;
import com.javarush.entity.discriminator.Invoice;
import com.javarush.entity.discriminator.Report;
import com.javarush.entity.joinedtablestrategy.Account;
import com.javarush.entity.joinedtablestrategy.BusinessAccount;
import com.javarush.entity.joinedtablestrategy.CheckingAccount;
import com.javarush.entity.joinedtablestrategy.SavingsAccount;
import com.javarush.entity.primarykey.BaseOrder;
import com.javarush.entity.primarykey.OnlineOrder;
import com.javarush.entity.primarykey.StoreOrder;
import com.javarush.entity.primarykey.WholesaleOrder;
import com.javarush.entity.singletable.Admin;
import com.javarush.entity.singletable.Employee;
import com.javarush.entity.singletable.Person;
import com.javarush.entity.singletable.RegularUser;
import com.javarush.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        demonstrateMappedSuperclass();

        demonstrateSingleTableInheritance();

        demonstrateDiscriminator();

        demonstrateJoinedTableInheritance();

        demonstratePrimaryKeyJoinColumn();

        HibernateUtil.shutdown();

    }

    private static void demonstratePrimaryKeyJoinColumn() {
        System.out.println("=== Демонстрация @PrimaryKeyJoinColumn ===");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Очищаем таблицы
            session.createQuery("DELETE FROM StoreOrder").executeUpdate();
            session.createQuery("DELETE FROM WholesaleOrder").executeUpdate();
            session.createQuery("DELETE FROM OnlineOrder").executeUpdate();
            session.createQuery("DELETE FROM BaseOrder").executeUpdate();

            System.out.println("\n1. Создаем заказы разных типов:");

            // Онлайн заказ
            OnlineOrder onlineOrder = new OnlineOrder();
            onlineOrder.setOrderNumber("ONL-2024-001");
            onlineOrder.setCustomerName("Alexey");
            onlineOrder.setTotalAmount(12500.0);
            onlineOrder.setCustomerEmail("alexey@example.com");
            onlineOrder.setShippingAddress("Tokio");
            onlineOrder.setPaymentMethod("CREDIT_CARD");
            onlineOrder.setTrackingNumber("TRK-123456789");

            // Оптовый заказ с кастомным PrimaryKeyJoinColumn
            WholesaleOrder wholesaleOrder = new WholesaleOrder();
            wholesaleOrder.setOrderNumber("WHO-2024-001");
            wholesaleOrder.setCustomerName("Apple");
            wholesaleOrder.setTotalAmount(250000.0);
            wholesaleOrder.setCompanyName("Google");
            wholesaleOrder.setTaxId("7701123456");
            wholesaleOrder.setDeliveryTerms("FOB");
            wholesaleOrder.setPurchaseOrderNumber("PO-2024-1001");

            // Магазинный заказ (без @PrimaryKeyJoinColumn)
            StoreOrder storeOrder = new StoreOrder();
            storeOrder.setOrderNumber("STR-2024-001");
            storeOrder.setCustomerName("Mary");
            storeOrder.setTotalAmount(3500.0);
            storeOrder.setStoreLocation("Zoom Inc");
            storeOrder.setCashierId("EMP-007");
            storeOrder.setPaymentType("CARD");
            storeOrder.setCustomerFeedbackRating(5);

            // Сохраняем
            session.save(onlineOrder);
            session.save(wholesaleOrder);
            session.save(storeOrder);

            transaction.commit();
            System.out.println("✅ 3 заказа сохранены в разные таблицы");

            // Показываем структуру таблиц
            System.out.println("\n2. Структура таблиц в БД:");
            System.out.println("   Таблица base_orders:");
            System.out.println("     order_id (PK), order_number, customer_name, order_date, total_amount, status");

            System.out.println("\n   Таблица online_orders:");
            System.out.println("     online_order_id (PK, FK -> base_orders.order_id) - кастомное имя!");
            System.out.println("     customer_email, shipping_address, payment_method, ...");

            System.out.println("\n   Таблица wholesale_orders:");
            System.out.println("     wholesale_ref_id (PK, FK -> base_orders.order_id) - кастомное имя!");
            System.out.println("     company_name, tax_id, delivery_terms, ...");

            System.out.println("\n   Таблица store_orders:");
            System.out.println("     order_id (PK, FK -> base_orders.order_id) - имя по умолчанию!");
            System.out.println("     store_location, cashier_id, receipt_number, ...");

            // Демонстрация SQL схемы
            System.out.println("\n3. SQL-схема, созданная Hibernate:");
            System.out.println("   -- Онлайн заказы:");
            System.out.println("   CREATE TABLE online_orders (");
            System.out.println("     online_order_id BIGINT NOT NULL,");
            System.out.println("     customer_email VARCHAR(255) NOT NULL,");
            System.out.println("     ...");
            System.out.println("     PRIMARY KEY (online_order_id),");
            System.out.println("     FOREIGN KEY (online_order_id) REFERENCES base_orders(order_id)");
            System.out.println("   )");

            System.out.println("\n   -- Оптовые заказы:");
            System.out.println("   CREATE TABLE wholesale_orders (");
            System.out.println("     wholesale_ref_id BIGINT NOT NULL,");
            System.out.println("     company_name VARCHAR(255) NOT NULL,");
            System.out.println("     ...");
            System.out.println("     PRIMARY KEY (wholesale_ref_id),");
            System.out.println("     FOREIGN KEY (wholesale_ref_id) REFERENCES base_orders(order_id)");
            System.out.println("   )");

            // Запрос данных
            System.out.println("\n4. Запрос всех онлайн заказов:");
            List<OnlineOrder> onlineOrders = session.createQuery(
                            "FROM OnlineOrder", OnlineOrder.class)
                    .list();

            for (OnlineOrder order : onlineOrders) {
                System.out.printf("   Заказ №%s: %s, доставка: %s%n",
                        order.getOrderNumber(),
                        order.getCustomerEmail(),
                        order.getShippingAddress());
            }

            // Показываем JOIN запрос
            System.out.println("\n5. SQL-запрос, который выполняет Hibernate:");
            System.out.println("   SELECT o.*, oo.*");
            System.out.println("   FROM base_orders o");
            System.out.println("   INNER JOIN online_orders oo ON o.order_id = oo.online_order_id");
            System.out.println("   WHERE o.status = 'NEW'");

            // Сравнение с именем по умолчанию
            System.out.println("\n6. Сравнение с именем по умолчанию:");
            System.out.println("   Без @PrimaryKeyJoinColumn:");
            System.out.println("     Колонка будет называться 'order_id' (как в родительской таблице)");

            System.out.println("\n   С @PrimaryKeyJoinColumn(name = 'online_order_id'):");
            System.out.println("     Колонка будет называться 'online_order_id'");

            // Преимущества кастомного имени
            System.out.println("\n7. Зачем менять имя колонки?");
            System.out.println("   ✅ Читаемость: яснее связь между таблицами");
            System.out.println("   ✅ Консистентность: можно использовать naming conventions");
            System.out.println("   ✅ Миграция: совместимость с существующей БД");
            System.out.println("   ✅ Ясность: явное указание, что это внешний ключ");

            // Проверка связи
            System.out.println("\n8. Проверка связи данных:");
            BaseOrder baseOrder = session.get(BaseOrder.class, onlineOrder.getOrderId());
            if (baseOrder instanceof OnlineOrder) {
                OnlineOrder retrieved = (OnlineOrder) baseOrder;
                System.out.printf("   Получен онлайн заказ через связь: %s → %s%n",
                        retrieved.getOrderId(),
                        retrieved.getOnlineOrderId());
            }

            // Дополнительный пример с referencedColumnName
            System.out.println("\n9. Использование referencedColumnName:");
            System.out.println("   @PrimaryKeyJoinColumn(");
            System.out.println("     name = 'custom_id',");
            System.out.println("     referencedColumnName = 'parent_id'  // ссылаемся на другую колонку");
            System.out.println("   )");

            System.out.println("\n   Это полезно, когда:");
            System.out.println("   - В родительской таблице PK называется не 'id'");
            System.out.println("   - Нужно ссылаться на другую уникальную колонку");
            System.out.println("   - Есть композитный первичный ключ");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void demonstrateJoinedTableInheritance() {
        System.out.println("=== Демонстрация Joined Table Inheritance ===");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Очищаем таблицы (в обратном порядке из-за foreign keys)
            session.createQuery("DELETE FROM BusinessAccount").executeUpdate();
            session.createQuery("DELETE FROM CheckingAccount").executeUpdate();
            session.createQuery("DELETE FROM SavingsAccount").executeUpdate();
            session.createQuery("DELETE FROM Account").executeUpdate();

            System.out.println("\n1. Создаем разные типы счетов:");

            // Сберегательный счет
            SavingsAccount savings = new SavingsAccount();
            savings.setAccountNumber("SAV-001-2024");
            savings.setAccountHolder("Ivan");
            savings.setOpeningDate(LocalDate.now());
            savings.setBalance(50000.0);
            savings.setInterestRate(new BigDecimal("4.5"));
            savings.setMinimumBalance(1000.0);
            savings.setInterestPaymentFrequency("QUARTERLY");
            savings.setWithdrawalLimitPerMonth(5);

            // Расчетный счет
            CheckingAccount checking = new CheckingAccount();
            checking.setAccountNumber("CHK-002-2024");
            checking.setAccountHolder("Petr");
            checking.setOpeningDate(LocalDate.now());
            checking.setBalance(15000.0);
            checking.setOverdraftLimit(5000.0);
            checking.setMonthlyFee(300.0);
            checking.setTransactionLimitPerDay(20);

            // Бизнес-счет
            BusinessAccount business = new BusinessAccount();
            business.setAccountNumber("BUS-003-2024");
            business.setAccountHolder("'IP'");
            business.setOpeningDate(LocalDate.now());
            business.setBalance(250000.0);
            business.setCompanyName("'IP'");
            business.setTaxIdNumber("7701234567");
            business.setBusinessType("LLC");
            business.setAuthorizedSignersCount(2);
            business.setCommercialLoanEligible(true);

            // Сохраняем счета (данные будут распределены по разным таблицам)
            session.save(savings);
            session.save(checking);
            session.save(business);

            transaction.commit();
            System.out.println("✅ 3 счета сохранены в 4 таблицы:");
            System.out.println("   - accounts (родительская)");
            System.out.println("   - savings_accounts");
            System.out.println("   - checking_accounts");
            System.out.println("   - business_accounts");

            // Показываем SQL-структуру
            System.out.println("\n2. Структура таблиц в базе данных:");
            System.out.println("   Таблица accounts:");
            System.out.println("     id, account_number, account_holder, opening_date, balance, currency, is_active");

            System.out.println("\n   Таблица savings_accounts:");
            System.out.println("     account_id (FK -> accounts.id), interest_rate, minimum_balance, ...");

            System.out.println("\n   Таблица checking_accounts:");
            System.out.println("     account_id (FK -> accounts.id), overdraft_limit, monthly_fee, ...");

            // Демонстрация полиморфного запроса
            System.out.println("\n3. Полиморфный запрос всех счетов:");
            List<Account> allAccounts = session.createQuery(
                            "FROM Account ORDER BY balance DESC", Account.class)
                    .list();

            for (Account account : allAccounts) {
                System.out.printf("   №%s: %s - %,.0f %s (%s)%n",
                        account.getAccountNumber(),
                        account.getAccountHolder(),
                        account.getBalance(),
                        account.getCurrency(),
                        account.getClass().getSimpleName());
            }

            // Запрос только конкретного типа
            System.out.println("\n4. Запрос только сберегательных счетов:");
            List<SavingsAccount> savingsAccounts = session.createQuery(
                            "FROM SavingsAccount WHERE interestRate > 4.0", SavingsAccount.class)
                    .list();

            for (SavingsAccount sa : savingsAccounts) {
                System.out.printf("   %s: ставка %.1f%%, мин. баланс %,.0f%n",
                        sa.getAccountNumber(),
                        sa.getInterestRate(),
                        sa.getMinimumBalance());
            }

            // Показываем SQL-запросы, которые генерирует Hibernate
            System.out.println("\n5. SQL-запросы, которые выполняет Hibernate:");
            System.out.println("   При сохранении SavingsAccount:");
            System.out.println("     INSERT INTO accounts (...) VALUES (...)");
            System.out.println("     INSERT INTO savings_accounts (...) VALUES (...)");

            System.out.println("\n   При запросе SavingsAccount:");
            System.out.println("     SELECT a.*, s.* FROM accounts a");
            System.out.println("     INNER JOIN savings_accounts s ON a.id = s.account_id");

            // Демонстрация преимуществ
            System.out.println("\n6. Преимущества Joined Table:");
            System.out.println("   ✅ Нет NULL значений в таблицах");
            System.out.println("   ✅ Можно использовать NOT NULL для полей подклассов");
            System.out.println("   ✅ Лучшая нормализация данных");
            System.out.println("   ✅ Легко добавлять новые подклассы");

            System.out.println("\n7. Недостатки Joined Table:");
            System.out.println("   ❌ Медленнее из-за JOIN операций");
            System.out.println("   ❌ Сложнее запросы");
            System.out.println("   ❌ Больше таблиц в базе");

            // Пример сложного запроса
            System.out.println("\n8. Сложный запрос с JOIN:");
            System.out.println("   SELECT a.account_number, a.balance,");
            System.out.println("          s.interest_rate, c.overdraft_limit");
            System.out.println("   FROM accounts a");
            System.out.println("   LEFT JOIN savings_accounts s ON a.id = s.account_id");
            System.out.println("   LEFT JOIN checking_accounts c ON a.id = c.account_id");
            System.out.println("   WHERE a.is_active = true");

            // Проверка связи данных
            System.out.println("\n9. Проверка связей между таблицами:");
            Account testAccount = session.get(Account.class, savings.getId());
            if (testAccount instanceof SavingsAccount) {
                SavingsAccount retrievedSavings = (SavingsAccount) testAccount;
                System.out.printf("   Получен сберегательный счет: %s, ставка: %.1f%%%n",
                        retrievedSavings.getAccountNumber(),
                        retrievedSavings.getInterestRate());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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