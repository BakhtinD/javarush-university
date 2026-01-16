package com.javarush;

import com.javarush.entity.Employee;
import com.javarush.entity.EmployeeTask;
import com.javarush.entity.Product;
import com.javarush.entity.User;
import com.javarush.util.HibernateUtil;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {

        // Демонстрация состояний объекта
        demonstrateEntityStates();

        demonstratePersistentState();

        demonstrateDetachedAndRemovedStates();

        demonstratePersistMethod();

        demonstrateSaveMethod();

        demonstrateMergeMethod(); // слайд 11

        demonstrateUpdateMethod(); // слайд 12

        demonstrateSaveOrUpdateMethod(); // слайд 13

        demonstrateGetLoadFindMethods(); // слайд 14

        demonstrateGetMethod(); // слайд 15

        demonstrateLoadMethod();  // слайд 16

        demonstrateFindMethod(); // слайд 17

        demonstrateRefreshMethod(); // слайд 18

        demonstrateDeletionMethods(); // слайд 19

        // shutdown
        HibernateUtil.shutdown();
    }

    /**
     * Демонстрация четырёх состояний Entity-объекта в Hibernate
     */
    private static void demonstrateEntityStates() {
        System.out.println("=== Демонстрация состояний Entity-объекта ===");

        // 1. TRANSIENT (Временное состояние)
        System.out.println("\n1. Состояние TRANSIENT:");
        User transientUser = new User("Anna", "anna@example.com", 1);
        System.out.println("Создан объект: " + transientUser);
        System.out.println("ID объекта: " + transientUser.getId()); // null - нет в БД
        System.out.println("Объект создан через new, Hibernate о нём не знает");

        // 2. PERSISTENT (Управляемое состояние)
        System.out.println("\n2. Состояние PERSISTENT:");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Сохраняем объект - переводим в PERSISTENT состояние
            session.persist(transientUser);
            System.out.println("Вызван session.persist()");
            System.out.println("ID после persist: " + transientUser.getId()); // ID появился!
            System.out.println("Объект теперь отслеживается Hibernate");

            // Меняем объект - изменение будет сохранено в БД автоматически
            transientUser.setName("Anna Ivanova");
            System.out.println("Изменено имя на: " + transientUser.getName());

            transaction.commit();
            System.out.println("Транзакция завершена, изменения сохранены в БД");

            // 3. DETACHED (Отсоединённое состояние)
            System.out.println("\n3. Состояние DETACHED:");
            System.out.println("Сессия закрывается...");
        } // сессия закрывается автоматически

        System.out.println("Сессия закрыта, объект теперь DETACHED");
        System.out.println("Hibernate больше не отслеживает объект");

        // Пытаемся изменить detached объект
        transientUser.setEmail("anna.new@example.com");
        System.out.println("Изменён email на: " + transientUser.getEmail());
        System.out.println("Но эти изменения НЕ будут сохранены в БД автоматически!");

        // 4. REMOVED (Удалённое состояние) и снова TRANSIENT
        System.out.println("\n4. Состояние REMOVED:");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Чтобы удалить, нужно сначала получить объект в PERSISTENT состоянии
            User userToRemove = session.get(User.class, transientUser.getId());
            System.out.println("Получили объект из БД: " + userToRemove.getName());

            // Помечаем для удаления
            session.remove(userToRemove);
            System.out.println("Вызван session.remove() - объект в состоянии REMOVED");

            transaction.commit();
            System.out.println("Транзакция завершена, объект удалён из БД");

            // После удаления и закрытия сессии объект снова становится TRANSIENT
            System.out.println("После удаления объект можно использовать как обычный Java-объект");
        }

        System.out.println("\n=== Демонстрация завершена ===");

    }

    private static void demonstratePersistentState() {
        System.out.println("\n=== Демонстрация PERSISTENT состояния ===");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Способ 1: Сохранить новый объект (Transient → Persistent)
            System.out.println("1. Сохранение нового объекта:");
            User newUser = new User("Robert", "robert@example.com", 2);
            System.out.println("До persist - ID: " + newUser.getId() + ", состояние: Transient");

            session.persist(newUser); // Transient → Persistent
            System.out.println("После persist - ID: " + newUser.getId() + ", состояние: Persistent");

            // Демонстрация автоматического отслеживания изменений
            System.out.println("\n2. Автоматическое отслеживание изменений:");
            newUser.setName("Robert Johnson");
            newUser.setLevel(3);
            System.out.println("Изменены name и level. Никакого update() не вызываем!");

            // Способ 2: Загрузить существующий объект
            System.out.println("\n3. Загрузка существующего объекта:");
            Integer savedUserId = newUser.getId();

            // Закоммитим сначала, чтобы объект был в БД
            transaction.commit();

            // Новая транзакция для демонстрации загрузки
            transaction = session.beginTransaction();

            User loadedUser = session.get(User.class, savedUserId);
            System.out.println("Загружен пользователь: " + loadedUser.getName());
            System.out.println("Состояние: Persistent (управляется Hibernate)");

            // Меняем загруженный объект
            loadedUser.setEmail("robert.j@example.com");
            System.out.println("Изменен email. Изменение будет сохранено автоматически.");

            // Демонстрация proxy (на примере связи с задачами, если бы она была)
            System.out.println("\n4. Proxy объекты (на примере Employee):");
            Employee emp = new Employee("Mike", "Developer", 5000);
            session.persist(emp);

            // Для демонстрации proxy создадим связанную сущность
            EmployeeTask task = new EmployeeTask("Fix bug", emp, new Date(), "Pending");
            session.persist(task);

            transaction.commit();

            // Новая сессия для демонстрации proxy
            try (Session newSession = HibernateUtil.getSessionFactory().openSession()) {
                // get() возвращает реальный объект или proxy
                Employee employeeProxy = newSession.load(Employee.class, emp.getId());
                System.out.println("load() вернул: " + employeeProxy.getClass().getName());
                System.out.println("Это proxy? " + employeeProxy.getClass().getName().contains("$"));

                // При обращении к полю происходит загрузка из БД
                System.out.println("Обращаемся к полю name...");
                System.out.println("Имя сотрудника: " + employeeProxy.getName());
            }

            transaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("=== Демонстрация завершена ===");
    }

    private static void demonstrateDetachedAndRemovedStates() {
        System.out.println("\n=== Демонстрация DETACHED и REMOVED состояний ===");

        // Сначала создадим тестового пользователя
        User testUser = null;
        Integer userId = null;

        // Создаём пользователя
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            testUser = new User("David", "david@example.com", 5);
            session.persist(testUser);
            userId = testUser.getId();

            transaction.commit();
            System.out.println("Создан тестовый пользователь с ID: " + userId);
        }

        // Часть 1: DETACHED состояние
        System.out.println("\n--- Часть 1: DETACHED состояние ---");

        User detachedUser = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Загружаем объект - он становится Persistent
            detachedUser = session.get(User.class, userId);
            System.out.println("1. Загружен пользователь: " + detachedUser.getName());
            System.out.println("   Состояние: Persistent (управляется Hibernate)");

            // Изменяем объект
            detachedUser.setEmail("david.new@example.com");
            System.out.println("2. Изменён email: " + detachedUser.getEmail());

            // ЗАКРЫВАЕМ сессию БЕЗ коммита
            System.out.println("3. Закрываем сессию БЕЗ коммита...");
        } // Сессия закрывается здесь

        System.out.println("4. Сессия закрыта. Состояние объекта: DETACHED");
        System.out.println("   Изменения email НЕ сохранены в БД!");

        // Проверяем, что изменения не сохранились
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User reloadedUser = session.get(User.class, userId);
            System.out.println("5. Загружаем заново из БД:");
            System.out.println("   Email в БД: " + reloadedUser.getEmail());
            System.out.println("   (старый email, изменения потеряны)");
        }

        // Часть 2: Проблема с Proxy и LazyInitializationException
        System.out.println("\n--- Часть 2: Proxy и LazyInitializationException ---");

        Employee detachedEmployee = null;
        Integer employeeId = null;

        // Создаём Employee с задачами
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Employee emp = new Employee("Tom", "Manager", 7000);
            session.persist(emp);
            employeeId = emp.getId();

            // Создаём задачу для сотрудника
            EmployeeTask task1 = new EmployeeTask("Prepare report", emp, new Date(), "Pending");
            EmployeeTask task2 = new EmployeeTask("Meeting", emp, new Date(), "In Progress");
            session.persist(task1);
            session.persist(task2);

            transaction.commit();

            // Загружаем через load() (возвращает proxy)
            detachedEmployee = session.load(Employee.class, employeeId);
            System.out.println("1. load() вернул: " + detachedEmployee.getClass().getName());
            System.out.println("   Это proxy? " + detachedEmployee.getClass().getName().contains("$"));
        } // Сессия закрывается

        System.out.println("2. Сессия закрыта. Employee теперь DETACHED");

        try {
            // Пытаемся обратиться к полю proxy после закрытия сессии
            System.out.println("3. Пытаемся получить имя сотрудника...");
            System.out.println("   Имя: " + detachedEmployee.getName()); // 💥 Может быть исключение!
        } catch (Exception e) {
            System.out.println("4.  Исключение: " + e.getClass().getSimpleName());
            System.out.println("   " + e.getMessage());
            System.out.println("   Это LazyInitializationException - частая ошибка!");
        }

        // Часть 3: REMOVED состояние
        System.out.println("\n--- Часть 3: REMOVED состояние ---");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Загружаем для удаления
            User userToRemove = session.get(User.class, userId);
            System.out.println("1. Загружен для удаления: " + userToRemove.getName());

            // Удаляем - переводим в состояние REMOVED
            session.remove(userToRemove);
            System.out.println("2. Вызван session.remove()");
            System.out.println("   Состояние объекта: REMOVED");
            System.out.println("   Объект ещё существует в памяти!");
            System.out.println("   ID: " + userToRemove.getId());
            System.out.println("   Имя: " + userToRemove.getName());

            // Можно ли использовать удалённый объект?
            userToRemove.setLevel(99);
            System.out.println("3. Меняем level на 99 (но это не имеет смысла)");

            System.out.println("4. Коммитим транзакцию...");
            transaction.commit();
            System.out.println("   Теперь запись УДАЛЕНА из БД");
            System.out.println("   Но Java-объект всё ещё в памяти!");

            // Пробуем загрузить удалённого пользователя
            User shouldBeNull = session.get(User.class, userId);
            System.out.println("5. Пробуем загрузить удалённого пользователя:");
            System.out.println("   Результат: " + (shouldBeNull == null ? "null" : shouldBeNull));
        }

        System.out.println("\n=== Демонстрация завершена ===");
    }

    private static void demonstratePersistMethod() {
        System.out.println("\n=== Демонстрация метода persist() ===");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            System.out.println("1. Создаём новый Transient объект:");
            User user = new User("Ethan", "ethan@example.com", 7);
            System.out.println("   До persist - ID: " + user.getId());

            System.out.println("\n2. Вызываем persist():");
            session.persist(user);
            System.out.println("   После persist - ID: " + user.getId());
            System.out.println("   Состояние: Transient → Persistent");
            System.out.println("    SQL INSERT ещё НЕ выполнен!");

            System.out.println("\n3. Меняем объект ДО коммита:");
            user.setName("Ethan Hunt");
            user.setLevel(10);
            System.out.println("   Имя: " + user.getName());
            System.out.println("   Уровень: " + user.getLevel());
            System.out.println("   Изменения отслеживаются Hibernate");

            System.out.println("\n4. Добавляем ещё один объект:");
            User user2 = new User("Olivia", "olivia@example.com", 5);
            session.persist(user2);
            System.out.println("   Второй объект добавлен в очередь на сохранение");

            System.out.println("\n5. Коммитим транзакцию:");
            System.out.println("   Сейчас Hibernate выполнит:");
            System.out.println("   - INSERT для первого пользователя");
            System.out.println("   - INSERT для второго пользователя");
            System.out.println("    Возможен batch-режим (одним запросом)");

            transaction.commit();

            System.out.println("\n6. Проверяем результат:");
            System.out.println("   ID первого пользователя: " + user.getId());
            System.out.println("   ID второго пользователя: " + user2.getId());
            System.out.println("   Оба сохранены в БД!");

            // Демонстрация: persist() на persistent объекте
            System.out.println("\n7. Пробуем persist() на persistent объекте:");
            session.persist(user); // Уже persistent
            System.out.println("   Ничего не произошло - объект уже управляется");

            // Демонстрация: изменение после persist()
            System.out.println("\n8. Меняем persistent объект:");
            user.setEmail("ethan.hunt@example.com");
            System.out.println("   Email изменён на: " + user.getEmail());
            System.out.println("   При следующем коммите будет UPDATE");

            Transaction transaction2 = session.beginTransaction();
            transaction2.commit(); // UPDATE выполнится здесь

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\n=== Демонстрация завершена ===");
    }

    private static void demonstrateSaveMethod() {
        System.out.println("\n=== Демонстрация метода save() ===");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            System.out.println("1. Создаём новый объект:");
            User user = new User("Jack", "jack@example.com", 8);
            System.out.println("   До save - ID: " + user.getId());

            System.out.println("\n2. Вызываем save() (возвращает ID):");
            Serializable generatedId = session.save(user);
            System.out.println("   save() вернул: " + generatedId);
            System.out.println("   ID в объекте: " + user.getId());
            System.out.println("   ID совпадают: " + generatedId.equals(user.getId()));

            System.out.println("\n3. Сравнение с persist():");
            User user2 = new User("Kate", "kate@example.com", 9);
            System.out.println("   persist() - void метод:");
            session.persist(user2);
            System.out.println("   ID после persist: " + user2.getId());

            System.out.println("\n4. Разные типы ID (демонстрация на Product):");
            Product product1 = new Product("Laptop", "Electronics", 999.99, 10);
            // Product использует Integer ID (GenerationType.IDENTITY)
            Serializable productId = session.save(product1);
            System.out.println("   Product ID: " + productId + " (тип: " + productId.getClass().getSimpleName() + ")");

            System.out.println("\n5. Поведение при установленном ID:");
            User userWithId = new User("Leo", "leo@example.com", 10);
            // Пробуем установить ID вручную (рискованно!)
            // userWithId.setId(9999); // Раскомментируйте для демонстрации проблемы

            try {
                Serializable manualId = session.save(userWithId);
                System.out.println("   save() с ручным ID вернул: " + manualId);
            } catch (Exception e) {
                System.out.println("    Исключение: " + e.getMessage());
            }

            System.out.println("\n6. Отложенное выполнение:");
            System.out.println("   Несмотря на то, что save() вернул ID,");
            System.out.println("   SQL INSERT ещё НЕ выполнен в БД!");
            System.out.println("   Проверим лог SQL...");

            // Меняем объект до коммита
            user.setName("Jackson");
            System.out.println("   Изменили имя на: " + user.getName());

            System.out.println("\n7. Коммитим транзакцию:");
            System.out.println("   Сейчас выполнится INSERT с именем 'Jackson'");
            transaction.commit();

            System.out.println("\n8. Проверяем сохранение:");
            User savedUser = session.get(User.class, user.getId());
            System.out.println("   Имя в БД: " + (savedUser != null ? savedUser.getName() : "null"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\n=== Демонстрация завершена ===");
    }

    private static void demonstrateMergeMethod() {
        System.out.println("\n=== Демонстрация метода merge() ===");

        // Создадим тестового пользователя для демонстрации
        User testUser = null;
        Integer testUserId = null;

        // Создаём пользователя
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            testUser = new User("Nathan", "nathan@example.com", 11);
            session.persist(testUser);
            testUserId = testUser.getId();
            transaction.commit();
            System.out.println("Создан тестовый пользователь ID: " + testUserId);
        }

        // Сценарий 1: Transient объект → INSERT
        System.out.println("\n--- Сценарий 1: Transient объект → INSERT ---");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            User transientUser = new User("Oliver", "oliver@example.com", 12);
            System.out.println("Transient объект, ID до merge: " + transientUser.getId());

            User mergedUser = (User) session.merge(transientUser); // Создаст новую запись
            System.out.println("merge() вернул новый объект");
            System.out.println("Оригинал ID: " + transientUser.getId());
            System.out.println("Результат merge ID: " + mergedUser.getId());
            System.out.println("Это разные объекты: " + (transientUser != mergedUser));

            transaction.commit();
            System.out.println("Выполнен INSERT для нового пользователя");
        }

        // Сценарий 2: Detached объект → UPDATE
        System.out.println("\n--- Сценарий 2: Detached объект → UPDATE ---");
        User detachedUser = null;

        // Сначала загружаем и отсоединяем
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            detachedUser = session.get(User.class, testUserId);
            System.out.println("Загружен пользователь: " + detachedUser.getName());
        } // Сессия закрывается → detached

        System.out.println("Объект теперь DETACHED");

        // Меняем detached объект
        String oldName = detachedUser.getName();
        detachedUser.setName("Nathaniel");
        detachedUser.setLevel(20);
        System.out.println("Изменения в detached объекте:");
        System.out.println("  Имя: " + oldName + " → " + detachedUser.getName());
        System.out.println("  Уровень: 11 → " + detachedUser.getLevel());

        // Используем merge() для сохранения
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            System.out.println("\nВызываем merge() для detached объекта:");
            User managedUser = (User) session.merge(detachedUser);

            System.out.println("merge() вернул новый управляемый объект");
            System.out.println("detachedUser == managedUser: " + (detachedUser == managedUser));
            System.out.println("detachedUser имя: " + detachedUser.getName());
            System.out.println("managedUser имя: " + managedUser.getName());

            // Покажем, что только managedUser отслеживается
            managedUser.setEmail("nathan.new@example.com");
            System.out.println("Меняем email у managedUser: " + managedUser.getEmail());

            detachedUser.setEmail("detached@example.com");
            System.out.println("Меняем email у detachedUser: " + detachedUser.getEmail());
            System.out.println("(это изменение НЕ сохранится)");

            transaction.commit();
            System.out.println("Выполнен UPDATE в БД");
        }

        // Проверим, что сохранилось
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User fromDb = session.get(User.class, testUserId);
            System.out.println("\nПроверяем БД:");
            System.out.println("  Имя: " + fromDb.getName());
            System.out.println("  Email: " + fromDb.getEmail());
            System.out.println("  Уровень: " + fromDb.getLevel());
        }

        // Сценарий 3: Persistent объект → ничего
        System.out.println("\n--- Сценарий 3: Persistent объект → ничего ---");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            User persistentUser = session.get(User.class, testUserId);
            System.out.println("Persistent объект: " + persistentUser.getName());

            User result = (User) session.merge(persistentUser);
            System.out.println("merge() на persistent объекте:");
            System.out.println("persistentUser == result: " + (persistentUser == result));
            System.out.println("Ничего не происходит, возвращается тот же объект");

            // Докажем, что это тот же объект
            persistentUser.setLevel(30);
            System.out.println("Меняем уровень на 30");
            System.out.println("У result тоже уровень: " + result.getLevel());

            transaction.commit();
        }

        // Сценарий 4: Объект с ручным ID
        System.out.println("\n--- Сценарий 4: Объект с несуществующим ID ---");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            User userWithFakeId = new User("Ghost", "ghost@example.com", 99);
            userWithFakeId.setId(99999); // Несуществующий ID

            System.out.println("Объект с ID=99999 (не существует в БД)");

            try {
                User merged = (User) session.merge(userWithFakeId);
                System.out.println("merge() выполнен, ID результата: " + merged.getId());
                System.out.println(" Hibernate создал НОВУЮ запись с новым ID!");
                System.out.println("Старый ID (99999) проигнорирован");
            } catch (Exception e) {
                System.out.println("Исключение: " + e.getMessage());
            }

            transaction.commit();
        }

        System.out.println("\n=== Демонстрация завершена ===");
    }

    private static void demonstrateUpdateMethod() {
        System.out.println("\n=== Демонстрация метода update() ===");

        // Создадим тестового пользователя
        User testUser = null;
        Integer testUserId = null;

        // Создаём пользователя
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            testUser = new User("Paul", "paul@example.com", 15);
            session.persist(testUser);
            testUserId = testUser.getId();
            transaction.commit();
            System.out.println("Создан тестовый пользователь ID: " + testUserId);
        }

        // Сценарий 1: Корректное использование update() с detached объектом
        System.out.println("\n--- Сценарий 1: Корректное использование update() ---");

        User detachedUser = null;

        // Загружаем и отсоединяем
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            detachedUser = session.get(User.class, testUserId);
            System.out.println("Загружен пользователь: " + detachedUser.getName());
            System.out.println("Email: " + detachedUser.getEmail());
        } // Сессия закрыта → detached

        System.out.println("Объект теперь DETACHED");

        // Меняем detached объект
        detachedUser.setName("Paul Newman");
        detachedUser.setEmail("paul.newman@example.com");
        System.out.println("Изменения в detached объекте:");
        System.out.println("  Имя: Paul → " + detachedUser.getName());
        System.out.println("  Email: paul@example.com → " + detachedUser.getEmail());

        // Используем update() для сохранения
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            System.out.println("\nВызываем update() для detached объекта:");
            session.update(detachedUser);
            System.out.println("detachedUser теперь PERSISTENT (управляется сессией)");
            System.out.println("Обратите внимание: сам объект изменил состояние!");

            // Демонстрация: изменения после update() тоже сохранятся
            detachedUser.setLevel(25);
            System.out.println("Меняем уровень после update(): " + detachedUser.getLevel());

            transaction.commit();
            System.out.println("Выполнен UPDATE в БД");
        }

        // Сценарий 2: update() на Transient объекте (ошибка)
        System.out.println("\n--- Сценарий 2: update() на Transient объекте (ошибка) ---");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            User transientUser = new User("George", "george@example.com", 16);
            // Нет ID - Transient состояние
            System.out.println("Transient объект, ID: " + transientUser.getId());

            try {
                session.update(transientUser); // Ошибка!
                System.out.println("update() выполнен"); // Не дойдём сюда
            } catch (Exception e) {
                System.out.println("Исключение: " + e.getClass().getSimpleName());
                System.out.println("Сообщение: " + e.getMessage());
                System.out.println("update() не работает с Transient объектами!");
            }

            transaction.rollback(); // Откатываем, так как была ошибка
        }

        // Сценарий 3: update() на Persistent объекте (бессмысленно)
        System.out.println("\n--- Сценарий 3: update() на Persistent объекте ---");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            User persistentUser = session.get(User.class, testUserId);
            System.out.println("Persistent объект: " + persistentUser.getName());

            System.out.println("Вызываем update() на persistent объекте:");
            session.update(persistentUser);
            System.out.println("Ничего не произошло - объект уже управляется");
            System.out.println("Это лишняя операция, но не ошибка");

            persistentUser.setName("Paul Updated Again");
            System.out.println("Меняем имя: " + persistentUser.getName());

            transaction.commit();
            System.out.println("UPDATE выполнится при коммите");
        }

        // Сценарий 4: Сравнение update() и merge()
        System.out.println("\n--- Сценарий 4: Сравнение update() и merge() ---");

        // Подготовим два одинаковых detached объекта
        User detachedForUpdate = null;
        User detachedForMerge = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Загружаем один объект дважды (в реальности это были бы разные экземпляры)
            User original = session.get(User.class, testUserId);

            // Создаём "копии" для демонстрации
            detachedForUpdate = new User(original.getName(), original.getEmail(), original.getLevel());
            detachedForUpdate.setId(original.getId());

            detachedForMerge = new User(original.getName(), original.getEmail(), original.getLevel());
            detachedForMerge.setId(original.getId());
        }

        System.out.println("Два одинаковых detached объекта:");
        System.out.println("  Object1 для update: " + detachedForUpdate.getName());
        System.out.println("  Object2 для merge: " + detachedForMerge.getName());

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Сначала загружаем тот же объект в сессию
            User inSession = session.get(User.class, testUserId);
            System.out.println("\nВ сессии уже есть объект с ID=" + testUserId);

            // Пробуем update() - будет проблема
            System.out.println("\nПробуем update():");
            try {
                detachedForUpdate.setName("Update Name");
                session.update(detachedForUpdate); //  Проблема!
                System.out.println("update() выполнен");
            } catch (Exception e) {
                System.out.println(" Исключение: " + e.getClass().getSimpleName());
                System.out.println("Нельзя иметь два persistent объекта с одним ID в сессии!");
            }

            // Пробуем merge() - работает
            System.out.println("\nПробуем merge():");
            detachedForMerge.setName("Merge Name");
            User merged = (User) session.merge(detachedForMerge); //  Работает
            System.out.println("merge() выполнен успешно");
            System.out.println("merge() вернул новый объект: " + (merged != detachedForMerge));
            System.out.println("Оригинальный detached объект остался detached");

            transaction.commit();
        }

        System.out.println("\n=== Демонстрация завершена ===");
    }

    private static void demonstrateSaveOrUpdateMethod() {
        System.out.println("\n=== Демонстрация метода saveOrUpdate() ===");

        // Будем использовать отдельные ID, которые точно существуют в текущей сессии
        Integer testId1 = null;
        Integer testId2 = null;

        // Создадим двух тестовых пользователей в отдельной транзакции
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            User user1 = new User("Thomas", "thomas@example.com", 17);
            User user2 = new User("Victor", "victor@example.com", 18);

            session.persist(user1);
            session.persist(user2);

            transaction.commit();

            testId1 = user1.getId();
            testId2 = user2.getId();

            System.out.println("Созданы тестовые пользователи:");
            System.out.println("  User1 - ID: " + testId1 + ", Имя: Thomas");
            System.out.println("  User2 - ID: " + testId2 + ", Имя: Victor");
        }

        // Часть 1: Простое использование saveOrUpdate() - INSERT
        System.out.println("\n--- Часть 1: INSERT с новым объектом ---");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            User newUser = new User("Walter", "walter@example.com", 20);
            System.out.println("Новый объект до saveOrUpdate:");
            System.out.println("  ID: " + newUser.getId() + " (null = Transient)");
            System.out.println("  Имя: " + newUser.getName());

            session.saveOrUpdate(newUser); // INSERT

            System.out.println("\nПосле saveOrUpdate:");
            System.out.println("  ID: " + newUser.getId() + " (установлен Hibernate)");
            System.out.println("  Состояние: Transient → Persistent");
            System.out.println("   Объект был изменён (ID установлен)");

            transaction.commit();
            System.out.println("INSERT выполнен при коммите");
        }

        // Часть 2: UPDATE существующего объекта
        System.out.println("\n--- Часть 2: UPDATE существующего объекта ---");

        // Создадим detached объект на основе существующего
        User detachedUser = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Загружаем существующий объект
            detachedUser = session.get(User.class, testId1);
            System.out.println("Загружен объект из БД:");
            System.out.println("  ID: " + detachedUser.getId());
            System.out.println("  Имя: " + detachedUser.getName());
            System.out.println("  Email: " + detachedUser.getEmail());

            // Сессия закрывается - объект становится detached
        }

        // Меняем detached объект
        System.out.println("\nИзменяем detached объект:");
        String oldName = detachedUser.getName();
        detachedUser.setName("Thomas Anderson");
        detachedUser.setEmail("thomas.anderson@example.com");
        System.out.println("  Имя: " + oldName + " → " + detachedUser.getName());
        System.out.println("  Email: обновлён");

        // Используем saveOrUpdate() для сохранения изменений
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            System.out.println("\nВызываем saveOrUpdate() на detached объекте:");
            session.saveOrUpdate(detachedUser);

            System.out.println("  Состояние: Detached → Persistent");
            System.out.println("   Оригинальный объект теперь управляется сессией");

            // Демонстрация: изменения после saveOrUpdate() тоже сохранятся
            detachedUser.setLevel(99);
            System.out.println("  Меняем уровень после saveOrUpdate(): " + detachedUser.getLevel());

            transaction.commit();
            System.out.println("UPDATE выполнен при коммите");
        }

        // Часть 3: Сравнение с merge() - безопасность
        System.out.println("\n--- Часть 3: Сравнение saveOrUpdate() и merge() ---");

        // Подготовим два одинаковых detached объекта
        User userForSaveOrUpdate = new User("Compare1", "compare1@example.com", 30);
        userForSaveOrUpdate.setId(testId2); // Используем существующий ID

        User userForMerge = new User("Compare2", "compare2@example.com", 31);
        userForMerge.setId(testId2); // Тот же ID

        System.out.println("\nДва объекта с одинаковым ID=" + testId2 + ":");
        System.out.println("  Object1 (для saveOrUpdate): " + userForSaveOrUpdate.getName());
        System.out.println("  Object2 (для merge): " + userForMerge.getName());

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Сначала загрузим оригинал в сессию
            User originalInSession = session.get(User.class, testId2);
            System.out.println("\nВ сессии уже есть объект с ID=" + testId2);
            System.out.println("  Имя в сессии: " + originalInSession.getName());

            System.out.println("\n1. Пробуем saveOrUpdate() на Object1:");
            try {
                session.saveOrUpdate(userForSaveOrUpdate);
                System.out.println("   Должно быть исключение!");
                System.out.println("  В сессии не может быть двух объектов с одним ID");
            } catch (Exception e) {
                System.out.println("   Ожидаемое исключение: " + e.getClass().getSimpleName());
                System.out.println("  Сообщение: " + e.getMessage());
            }

            System.out.println("\n2. Пробуем merge() на Object2:");
            User mergedUser = (User) session.merge(userForMerge);
            System.out.println("   merge() выполнен успешно");
            System.out.println("  Оригинал ID: " + userForMerge.getId());
            System.out.println("  Результат ID: " + mergedUser.getId());
            System.out.println("  Это разные объекты: " + (userForMerge != mergedUser));
            System.out.println("   merge() не изменяет оригинал и безопасно работает");

            System.out.println("\n3. Проверяем, что в сессии:");
            System.out.println("  Оригинал в сессии: " + originalInSession.getName());
            System.out.println("  Результат merge: " + mergedUser.getName());
            System.out.println("  originalInSession == mergedUser: " + (originalInSession == mergedUser));

            transaction.commit();
        }

        // Часть 4: Демонстрация проблемы с несуществующими ID
        System.out.println("\n--- Часть 4: Проблема saveOrUpdate() с несуществующими ID ---");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            System.out.println("\nОбъект с ID=999999 (не существует в БД):");
            User problematicUser = new User("Problem", "problem@example.com", 40);
            problematicUser.setId(999999);

            System.out.println("  Перед saveOrUpdate ID: " + problematicUser.getId());

            try {
                session.saveOrUpdate(problematicUser);
                System.out.println("  saveOrUpdate() выполнен");

                // Hibernate может:
                // 1. Выполнить UPDATE (и провалиться с OptimisticLockException)
                // 2. Выполнить INSERT с ID=999999 (если БД позволяет)
                // 3. Изменить ID и выполнить INSERT

                System.out.println("  После saveOrUpdate ID: " + problematicUser.getId());

                transaction.commit();
                System.out.println("  ✅ Транзакция закоммичена");

                // Проверим, что в БД
                try (Session checkSession = HibernateUtil.getSessionFactory().openSession()) {
                    User fromDb = checkSession.get(User.class, problematicUser.getId());
                    if (fromDb != null) {
                        System.out.println("  В БД найдена запись с ID=" + fromDb.getId());
                    } else {
                        System.out.println("  ❌ Запись не найдена в БД");
                    }
                }

            } catch (Exception e) {
                System.out.println("  💥 Исключение при saveOrUpdate: " + e.getClass().getSimpleName());
                System.out.println("  Сообщение: " + e.getMessage());
                System.out.println("  💡 saveOrUpdate() попытался выполнить UPDATE, но записи нет!");
                transaction.rollback();
            }
        }

        // Часть 5: Выводы и рекомендации
        System.out.println("\n--- Часть 5: Выводы и рекомендации ---");

        System.out.println("\n📌 saveOrUpdate() - особенности:");
        System.out.println("  • Может изменять переданный объект (устанавливать ID)");
        System.out.println("  • Определяет INSERT/UPDATE по наличию ID");
        System.out.println("  • Может вызывать исключения при несуществующих ID");
        System.out.println("  • Часть legacy Hibernate API (не JPA стандарт)");

        System.out.println("\n📌 Когда использовать saveOrUpdate():");
        System.out.println("  • В legacy-коде для совместимости");
        System.out.println("  • Для простых CRUD операций");
        System.out.println("  • Когда точно контролируете состояние объектов");

        System.out.println("\n📌 Современная альтернатива:");
        System.out.println("  if (object.getId() == null) {");
        System.out.println("      session.persist(object);  // Для новых");
        System.out.println("  } else {");
        System.out.println("      session.merge(object);    // Для существующих");
        System.out.println("  }");

        System.out.println("\n📌 Или ещё проще:");
        System.out.println("  session.merge(object);  // Работает для обоих случаев");
        System.out.println("  // Но помните: merge() возвращает новый объект!");

        System.out.println("\n=== Демонстрация завершена ===");
    }

    private static void demonstrateGetLoadFindMethods() {
        System.out.println("\n=== Демонстрация get(), load() и find() ===");

        // Создадим тестового пользователя
        Integer testUserId = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            User testUser = new User("Edward", "edward@example.com", 50);
            session.persist(testUser);
            transaction.commit();
            testUserId = testUser.getId();
            System.out.println("Создан тестовый пользователь ID: " + testUserId);
        }

        // Часть 1: Сравнение get() и load()
        System.out.println("\n--- Часть 1: Сравнение get() и load() ---");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("\n1. Метод get() - существующий объект:");
            User userGet = session.get(User.class, testUserId);
            System.out.println("   Результат: " + (userGet != null ? "Объект найден" : "null"));
            System.out.println("   Тип: " + (userGet != null ? userGet.getClass().getSimpleName() : "N/A"));
            System.out.println("   Данные доступны сразу: " + (userGet != null ? userGet.getName() : "N/A"));

            System.out.println("\n2. Метод load() - существующий объект:");
            User userLoad = session.load(User.class, testUserId);
            System.out.println("   Результат: proxy объект (не null)");
            System.out.println("   Тип: " + userLoad.getClass().getName());
            System.out.println("   Это proxy? " + userLoad.getClass().getName().contains("$"));

            System.out.println("\n3. Обращение к данным proxy:");
            System.out.println("   До обращения к getName()...");
            String name = userLoad.getName(); // Загрузка происходит ЗДЕСЬ
            System.out.println("   Имя: " + name);
            System.out.println("   💡 SELECT выполнен только при обращении к getName()!");

            System.out.println("\n4. Сравнение производительности:");
            System.out.println("   get() → SELECT выполняется сразу");
            System.out.println("   load() → SELECT выполняется при первом обращении");
            System.out.println("   load() полезен, если объект может не понадобиться");
        }

        // Часть 2: Поведение при несуществующих объектах
        System.out.println("\n--- Часть 2: Несуществующие объекты ---");

        Integer nonExistentId = 99999;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("\n1. get() с несуществующим ID=" + nonExistentId + ":");
            User userGet = session.get(User.class, nonExistentId);
            System.out.println("   Результат: " + (userGet == null ? "null" : "объект"));
            System.out.println("   ✅ Безопасно - просто возвращает null");

            System.out.println("\n2. load() с несуществующим ID=" + nonExistentId + ":");
            User userLoad = session.load(User.class, nonExistentId);
            System.out.println("   Результат: proxy создан (не null)");
            System.out.println("   Тип: " + userLoad.getClass().getName());
            System.out.println("   💡 Пока никакого исключения!");

            System.out.println("\n3. Пробуем обратиться к proxy:");
            try {
                String name = userLoad.getName(); // Попытка загрузки
                System.out.println("   Имя: " + name);
            } catch (Exception e) {
                System.out.println("   💥 Исключение: " + e.getClass().getSimpleName());
                System.out.println("   Сообщение: " + e.getMessage());
                System.out.println("   load() создаёт proxy, но при обращении выбрасывает исключение!");
            }

            System.out.println("\n4. find() с несуществующим ID=" + nonExistentId + ":");
            User userFind = session.find(User.class, nonExistentId);
            System.out.println("   Результат: " + (userFind == null ? "null" : "объект"));
            System.out.println("   ✅ Поведение как у get() - безопасно возвращает null");
        }

        // Часть 3: Метод find() - JPA стандарт
        System.out.println("\n--- Часть 3: Метод find() (JPA стандарт) ---");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("\n1. find() с существующим объектом:");
            User userFind = session.find(User.class, testUserId);
            System.out.println("   Результат: " + (userFind != null ? "Объект найден" : "null"));
            System.out.println("   Тип: " + userFind.getClass().getSimpleName());
            System.out.println("   Данные доступны сразу: " + userFind.getName());

            System.out.println("\n2. find() с дополнительными параметрами:");
            System.out.println("   // В реальном коде:");
            System.out.println("   Map<String, Object> properties = new HashMap<>();");
            System.out.println("   properties.put(\"javax.persistence.lock.timeout\", 5000);");
            System.out.println("   User user = session.find(User.class, id, properties);");

            System.out.println("\n3. Сравнение find() и get():");
            System.out.println("   find() - часть JPA спецификации");
            System.out.println("   get() - родной метод Hibernate");
            System.out.println("   В большинстве случаев ведут себя одинаково");
        }

        // Часть 4: Практический пример с lazy loading
        System.out.println("\n--- Часть 4: Практический пример с lazy loading ---");

        // Создадим Employee с Task для демонстрации
        Integer employeeId = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Employee emp = new Employee("Frank", "Manager", 6000);
            session.persist(emp);

            EmployeeTask task1 = new EmployeeTask("Report", emp, new Date(), "Pending");
            EmployeeTask task2 = new EmployeeTask("Meeting", emp, new Date(), "Done");
            session.persist(task1);
            session.persist(task2);

            transaction.commit();
            employeeId = emp.getId();
            System.out.println("Создан сотрудник с задачами ID: " + employeeId);
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("\nДемонстрация lazy loading с load():");

            // Важно: tasks в Employee - lazy по умолчанию
            Employee empProxy = session.load(Employee.class, employeeId);
            System.out.println("1. load() вернул: " + empProxy.getClass().getName());
            System.out.println("   Это proxy? " + empProxy.getClass().getName().contains("$"));

            System.out.println("\n2. Обращаемся к имени сотрудника:");
            System.out.println("   До обращения...");
            String name = empProxy.getName(); // SELECT для Employee выполняется здесь
            System.out.println("   Имя: " + name);

            System.out.println("\n3. Обращаемся к задачам (lazy collection):");
            System.out.println("   До обращения к tasks...");
            List<EmployeeTask> tasks = empProxy.getTasks(); // SELECT для tasks выполняется здесь
            System.out.println("   Количество задач: " + tasks.size());
            System.out.println("   💡 Два SELECT: один для Employee, второй для Tasks");
        }

        // Часть 5: Рекомендации и выводы
        System.out.println("\n--- Часть 5: Рекомендации ---");

        System.out.println("\n📌 Когда использовать get()/find():");
        System.out.println("  • Когда нужно проверить существование объекта");
        System.out.println("  • Для обязательных ссылок (не null)");
        System.out.println("  • В большинстве CRUD операций");
        System.out.println("  • Когда нужны данные сразу");

        System.out.println("\n📌 Когда использовать load():");
        System.out.println("  • Когда уверены, что объект существует");
        System.out.println("  • Для ленивой загрузки (оптимизация)");
        System.out.println("  • При работе со связями, которые могут не понадобиться");
        System.out.println("  • В коде, где объект точно будет использован позже");

        System.out.println("\n📌 Предупреждения:");
        System.out.println("  • load() + закрытая сессия = LazyInitializationException");
        System.out.println("  • load() с несуществующим ID = исключение при обращении");
        System.out.println("  • Всегда проверяйте null после get()/find()");

        System.out.println("\n📌 Современная практика:");
        System.out.println("  // Для EntityManager (JPA):");
        System.out.println("  User user = entityManager.find(User.class, id);");
        System.out.println("  ");
        System.out.println("  // Для Session (Hibernate):");
        System.out.println("  User user = session.get(User.class, id); // или find()");
        System.out.println("  // load() используйте осторожно!");

        System.out.println("\n=== Демонстрация завершена ===");
    }

    private static void demonstrateGetMethod() {
        System.out.println("\n=== Демонстрация метода get() ===");

        // Создадим несколько тестовых пользователей
        List<Integer> testUserIds = new ArrayList<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            User user1 = new User("George", "george@example.com", 60);
            User user2 = new User("Helen", "helen@example.com", 61);
            User user3 = new User("Ivan", "ivan@example.com", 62);

            session.persist(user1);
            session.persist(user2);
            session.persist(user3);

            transaction.commit();

            testUserIds.add(user1.getId());
            testUserIds.add(user2.getId());
            testUserIds.add(user3.getId());

            System.out.println("Созданы тестовые пользователи:");
            System.out.println("  ID: " + user1.getId() + ", Имя: George");
            System.out.println("  ID: " + user2.getId() + ", Имя: Helen");
            System.out.println("  ID: " + user3.getId() + ", Имя: Ivan");
        }

        // Часть 1: Базовое использование get()
        System.out.println("\n--- Часть 1: Базовое использование get() ---");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Integer existingId = testUserIds.get(0);

            System.out.println("\n1. Получение существующего объекта:");
            System.out.println("   session.get(User.class, " + existingId + ")");

            User user = session.get(User.class, existingId);

            System.out.println("   Результат: " + (user != null ? "Объект найден" : "null"));
            System.out.println("   Имя: " + user.getName());
            System.out.println("   Email: " + user.getEmail());
            System.out.println("   💡 SELECT выполнен сразу при вызове get()");

            System.out.println("\n2. Повторное получение того же объекта:");
            System.out.println("   session.get(User.class, " + existingId + ") второй раз");

            User sameUser = session.get(User.class, existingId);

            System.out.println("   Результат: " + (sameUser != null ? "Объект найден" : "null"));
            System.out.println("   Это тот же объект? " + (user == sameUser));
            System.out.println("   💡 Второй get() взял объект из кэша сессии");
            System.out.println("   💡 Нет повторного SELECT в БД");
        }

        // Часть 2: Работа с несуществующими объектами
        System.out.println("\n--- Часть 2: Несуществующие объекты ---");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Integer nonExistentId = 999999;

            System.out.println("\n1. get() с несуществующим ID=" + nonExistentId + ":");
            User user = session.get(User.class, nonExistentId);

            System.out.println("   Результат: " + (user == null ? "null" : "объект"));
            System.out.println("   💡 Безопасно - просто возвращает null");
            System.out.println("   💡 SELECT выполнен, но вернул 0 строк");

            System.out.println("\n2. Безопасная обработка результата:");
            System.out.println("   User user = session.get(User.class, id);");
            System.out.println("   ");
            System.out.println("   if (user != null) {");
            System.out.println("       // Объект существует - работаем");
            System.out.println("       user.setName(\"Updated\");");
            System.out.println("   } else {");
            System.out.println("       // Объект не существует");
            System.out.println("       System.out.println(\"Не найден\");");
            System.out.println("       // Или создаём нового...");
            System.out.println("       user = new User(...);");
            System.out.println("       session.persist(user);");
            System.out.println("   }");
        }

        // Часть 3: Разные типы первичных ключей
        System.out.println("\n--- Часть 3: Разные типы первичных ключей ---");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            System.out.println("\n1. Product с автоинкрементным ID (Integer):");
            Product product = new Product("Laptop", "Electronics", 999.99, 10);
            session.persist(product);

            System.out.println("   Сохранён продукт ID: " + product.getId());

            // Получаем по Integer ID
            Product loadedProduct = session.get(Product.class, product.getId());
            System.out.println("   get(Product.class, " + product.getId() + "):");
            System.out.println("   Название: " + loadedProduct.getName());

            System.out.println("\n2. Демонстрация с String ID (предположим):");
            System.out.println("   // Если бы Product использовал String ID:");
            System.out.println("   Product p = new Product();");
            System.out.println("   p.setId(\"PROD-001\"); // String ID");
            System.out.println("   session.persist(p);");
            System.out.println("   ");
            System.out.println("   // Получение по String:");
            System.out.println("   Product p2 = session.get(Product.class, \"PROD-001\");");

            transaction.commit();
        }

        // Часть 4: Использование в транзакциях
        System.out.println("\n--- Часть 4: Использование в транзакциях ---");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("\n1. Загрузка и обновление в одной транзакции:");

            Transaction transaction = session.beginTransaction();

            Integer userId = testUserIds.get(1);
            System.out.println("   Загружаем пользователя ID=" + userId);

            User user = session.get(User.class, userId);
            if (user != null) {
                System.out.println("   Текущее имя: " + user.getName());
                System.out.println("   Текущий уровень: " + user.getLevel());

                // Меняем объект
                user.setName("Helen Updated");
                user.setLevel(user.getLevel() + 1);

                System.out.println("   Новое имя: " + user.getName());
                System.out.println("   Новый уровень: " + user.getLevel());
                System.out.println("   💡 Объект Persistent - изменения отслеживаются");
            }

            System.out.println("\n   Коммитим транзакцию...");
            transaction.commit();
            System.out.println("   ✅ UPDATE выполнен в БД");

            // Проверим, что сохранилось
            System.out.println("\n2. Проверка обновления:");
            User checkUser = session.get(User.class, userId);
            System.out.println("   Имя в БД: " + checkUser.getName());
            System.out.println("   Уровень в БД: " + checkUser.getLevel());
        }

        // Часть 5: Кэширование и производительность
        System.out.println("\n--- Часть 5: Кэширование ---");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("\nДемонстрация кэша первого уровня (сессии):");

            Integer userId = testUserIds.get(2);

            System.out.println("1. Первый get() - загрузка из БД:");
            long start1 = System.currentTimeMillis();
            User user1 = session.get(User.class, userId);
            long time1 = System.currentTimeMillis() - start1;
            System.out.println("   Время: " + time1 + "ms");
            System.out.println("   💡 SELECT выполнен в БД");

            System.out.println("\n2. Второй get() - из кэша сессии:");
            long start2 = System.currentTimeMillis();
            User user2 = session.get(User.class, userId);
            long time2 = System.currentTimeMillis() - start2;
            System.out.println("   Время: " + time2 + "ms");
            System.out.println("   💡 Объект взят из кэша (быстрее)");
            System.out.println("   Это тот же объект? " + (user1 == user2));

            System.out.println("\n3. Третий get() с другим ID:");
            long start3 = System.currentTimeMillis();
            User user3 = session.get(User.class, testUserIds.get(0));
            long time3 = System.currentTimeMillis() - start3;
            System.out.println("   Время: " + time3 + "ms");
            System.out.println("   💡 SELECT выполнен (не было в кэше)");
        }

        // Часть 6: Рекомендации
        System.out.println("\n--- Часть 6: Рекомендации по использованию get() ---");

        System.out.println("\n📌 Когда использовать get():");
        System.out.println("  • Для загрузки объектов по ID");
        System.out.println("  • Когда нужна проверка существования объекта");
        System.out.println("  • В большинстве CRUD операций");
        System.out.println("  • Когда нужны данные сразу");

        System.out.println("\n📌 Преимущества get():");
        System.out.println("  • Простой и понятный");
        System.out.println("  • Безопасный (возвращает null если нет)");
        System.out.println("  • Немедленная загрузка данных");
        System.out.println("  • Кэширование в рамках сессии");

        System.out.println("\n📌 Что запомнить:");
        System.out.println("  • Всегда проверяйте результат на null");
        System.out.println("  • get() выполняет SELECT сразу");
        System.out.println("  • Объекты кэшируются в сессии");
        System.out.println("  • Для массовой загрузки используйте HQL, а не цикл с get()");

        System.out.println("\n📌 Пример правильного использования:");
        System.out.println("  public User getUserById(Integer id) {");
        System.out.println("      try (Session session = sessionFactory.openSession()) {");
        System.out.println("          return session.get(User.class, id);");
        System.out.println("      }");
        System.out.println("  }");
        System.out.println("  ");
        System.out.println("  // Использование:");
        System.out.println("  User user = getUserById(123);");
        System.out.println("  if (user != null) {");
        System.out.println("      // работаем с пользователем");
        System.out.println("  }");

        System.out.println("\n=== Демонстрация завершена ===");
    }

    private static void demonstrateFindMethod() {
        System.out.println("\n=== Демонстрация метода find() ===");

        // Создадим тестового пользователя
        Integer testUserId = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            User testUser = new User("James", "james@example.com", 70);
            session.persist(testUser);
            transaction.commit();
            testUserId = testUser.getId();
            System.out.println("Создан тестовый пользователь ID: " + testUserId);
        }

        // Часть 1: Базовое сравнение find() и get()
        System.out.println("\n--- Часть 1: Сравнение find() и get() ---");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("\n1. find() с существующим объектом:");
            User userFind = session.find(User.class, testUserId);
            System.out.println("   Результат: " + (userFind != null ? "Объект найден" : "null"));
            System.out.println("   Имя: " + userFind.getName());
            System.out.println("   Email: " + userFind.getEmail());

            System.out.println("\n2. get() с тем же объектом:");
            User userGet = session.get(User.class, testUserId);
            System.out.println("   Результат: " + (userGet != null ? "Объект найден" : "null"));
            System.out.println("   Имя: " + userGet.getName());

            System.out.println("\n3. Сравнение объектов:");
            System.out.println("   Это один объект? " + (userFind == userGet));
            System.out.println("   💡 Оба метода вернули тот же объект из кэша сессии");

            System.out.println("\n4. Проверка на несуществующий объект:");
            Integer nonExistentId = 888888;

            User findNull = session.find(User.class, nonExistentId);
            User getNull = session.get(User.class, nonExistentId);

            System.out.println("   find() результат: " + (findNull == null ? "null" : "объект"));
            System.out.println("   get() результат: " + (getNull == null ? "null" : "объект"));
            System.out.println("   💡 Оба метода возвращают null для несуществующих объектов");
        }

        // Часть 2: JPA совместимость
        System.out.println("\n--- Часть 2: JPA стандарт и совместимость ---");

        System.out.println("\n1. find() в JPA стандарте:");
        System.out.println("   // EntityManager - стандартный JPA интерфейс");
        System.out.println("   EntityManager em = entityManagerFactory.createEntityManager();");
        System.out.println("   User user = em.find(User.class, id);");
        System.out.println("   em.close();");

        System.out.println("\n2. find() в Hibernate Session:");
        System.out.println("   // Session - Hibernate-специфичный интерфейс");
        System.out.println("   Session session = sessionFactory.openSession();");
        System.out.println("   User user = session.find(User.class, id);");
        System.out.println("   session.close();");

        System.out.println("\n3. Почему это важно:");
        System.out.println("   • Код с find() будет работать с любым JPA провайдером");
        System.out.println("   • Код с get() работает только с Hibernate");
        System.out.println("   • Миграция на другой ORM проще с find()");

        // Часть 3: Дополнительные параметры find()
        System.out.println("\n--- Часть 3: Дополнительные возможности find() ---");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            System.out.println("\n1. find() с LockMode (блокировка):");
            System.out.println("   // В реальном коде для пессимистичной блокировки:");
            System.out.println("   User user = session.find(User.class, testUserId, LockMode.PESSIMISTIC_WRITE);");
            System.out.println("   // Теперь другие транзакции не могут изменять эту запись");

            System.out.println("\n2. find() с properties:");
            System.out.println("   Map<String, Object> props = new HashMap<>();");
            System.out.println("   props.put(\"javax.persistence.query.timeout\", 10000);");
            System.out.println("   props.put(\"org.hibernate.flushMode\", FlushMode.COMMIT);");
            System.out.println("   User user = session.find(User.class, id, props);");

            System.out.println("\n3. find() в EntityManager имеет больше перегрузок:");
            System.out.println("   // find(Class<T> entityClass, Object primaryKey)");
            System.out.println("   // find(Class<T> entityClass, Object primaryKey, LockModeType lockMode)");
            System.out.println("   // find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties)");

            transaction.commit();
        }

        // Часть 4: Современный стиль с Optional
        System.out.println("\n--- Часть 4: Современный стиль программирования ---");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("\n1. Традиционный подход:");
            System.out.println("   User user = session.find(User.class, id);");
            System.out.println("   if (user != null) {");
            System.out.println("       // работаем с user");
            System.out.println("   }");

            System.out.println("\n2. Современный подход с Optional (Java 8+):");
            System.out.println("   Optional<User> userOpt = Optional.ofNullable(session.find(User.class, id));");
            System.out.println("   ");
            System.out.println("   userOpt.ifPresent(user -> {");
            System.out.println("       // работаем с user");
            System.out.println("       System.out.println(user.getName());");
            System.out.println("   });");
            System.out.println("   ");
            System.out.println("   // Или с обработкой отсутствия:");
            System.out.println("   User user = userOpt.orElseGet(() -> createDefaultUser());");
            System.out.println("   User user2 = userOpt.orElseThrow(() -> new UserNotFoundException());");

            System.out.println("\n3. Практический пример:");
            Optional<User> userOpt = Optional.ofNullable(session.find(User.class, testUserId));

            System.out.println("   Optional для существующего пользователя:");
            userOpt.ifPresent(user -> {
                System.out.println("     Пользователь найден: " + user.getName());
            });

            System.out.println("\n   Optional для несуществующего пользователя:");
            Optional<User> nonExistentOpt = Optional.ofNullable(session.find(User.class, 999999));
            System.out.println("     isPresent: " + nonExistentOpt.isPresent());
            nonExistentOpt.ifPresentOrElse(
                    user -> System.out.println("     Найден: " + user.getName()),
                    () -> System.out.println("     Пользователь не найден")
            );
        }

        // Часть 5: Производительность и кэширование
        System.out.println("\n--- Часть 5: Производительность ---");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("\nДемонстрация кэширования:");

            System.out.println("1. Первый find() - загрузка из БД:");
            long start1 = System.currentTimeMillis();
            User user1 = session.find(User.class, testUserId);
            long time1 = System.currentTimeMillis() - start1;
            System.out.println("   Время: " + time1 + "ms");
            System.out.println("   💡 SELECT выполнен в БД");

            System.out.println("\n2. Второй find() - из кэша сессии:");
            long start2 = System.currentTimeMillis();
            User user2 = session.find(User.class, testUserId);
            long time2 = System.currentTimeMillis() - start2;
            System.out.println("   Время: " + time2 + "ms");
            System.out.println("   💡 Объект взят из кэша (быстрее)");
            System.out.println("   Это тот же объект? " + (user1 == user2));

            System.out.println("\n3. Сравнение с get():");
            long start3 = System.currentTimeMillis();
            User user3 = session.get(User.class, testUserId);
            long time3 = System.currentTimeMillis() - start3;
            System.out.println("   Время get(): " + time3 + "ms");
            System.out.println("   Это тот же объект? " + (user1 == user3));
        }

        // Часть 6: Рекомендации и выводы
        System.out.println("\n--- Часть 6: Рекомендации ---");

        System.out.println("\n📌 Когда использовать find():");
        System.out.println("  • При написании переносимого кода (JPA-совместимого)");
        System.out.println("  • При использовании EntityManager вместо Session");
        System.out.println("  • В новых проектах, следующих стандартам JPA");
        System.out.println("  • Когда нужны дополнительные параметры (LockMode, properties)");

        System.out.println("\n📌 Когда использовать get():");
        System.out.println("  • В legacy-коде, использующем Hibernate API");
        System.out.println("  • Когда работаете только с Hibernate");
        System.out.println("  • Для простоты, если не нужна JPA совместимость");

        System.out.println("\n📌 Преимущества find():");
        System.out.println("  • JPA стандарт - гарантированное поведение");
        System.out.println("  • Совместимость с другими ORM");
        System.out.println("  • Дополнительные параметры (в EntityManager)");
        System.out.println("  • Современный дизайн API");

        System.out.println("\n📌 Что запомнить:");
        System.out.println("  • find() и get() практически идентичны в Hibernate");
        System.out.println("  • Оба возвращают null для несуществующих объектов");
        System.out.println("  • Оба используют кэш сессии");
        System.out.println("  • Выбор между ними - вопрос стиля и совместимости");

        System.out.println("\n📌 Примеры использования:");
        System.out.println("  // Для JPA-совместимого кода:");
        System.out.println("  @PersistenceContext");
        System.out.println("  private EntityManager entityManager;");
        System.out.println("  ");
        System.out.println("  public User getUser(Integer id) {");
        System.out.println("      return entityManager.find(User.class, id);");
        System.out.println("  }");
        System.out.println("  ");
        System.out.println("  // Для Hibernate-специфичного кода:");
        System.out.println("  public User getUser(Integer id) {");
        System.out.println("      try (Session session = sessionFactory.openSession()) {");
        System.out.println("          return session.find(User.class, id); // или get()");
        System.out.println("      }");
        System.out.println("  }");

        System.out.println("\n=== Демонстрация завершена ===");
    }

    private static void demonstrateLoadMethod() {
        System.out.println("\n=== Демонстрация метода load() ===");

        // Создадим тестового пользователя
        Integer testUserId = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            User testUser = new User("Kevin", "kevin@example.com", 80);
            session.persist(testUser);
            transaction.commit();
            testUserId = testUser.getId();
            System.out.println("Создан тестовый пользователь ID: " + testUserId);
        }

        // Часть 1: Основное поведение load() - proxy и ленивая загрузка
        System.out.println("\n--- Часть 1: Proxy и ленивая загрузка ---");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("\n1. Вызов load() - создание proxy:");
            User userProxy = session.load(User.class, testUserId);

            System.out.println("   Результат: " + (userProxy != null ? "Объект (proxy)" : "null"));
            System.out.println("   Класс: " + userProxy.getClass().getName());
            System.out.println("   Это proxy? " + userProxy.getClass().getName().contains("$"));
            System.out.println("   💡 SELECT ещё НЕ выполнен!");
            System.out.println("   💡 Создан proxy-объект с ID=" + testUserId);

            System.out.println("\n2. Первое обращение к данным - ленивая загрузка:");
            System.out.println("   Вызываем userProxy.getName()...");
            String name = userProxy.getName(); // SELECT выполняется ЗДЕСЬ!
            System.out.println("   Имя: " + name);
            System.out.println("   💡 SELECT выполнен при первом обращении");

            System.out.println("\n3. Второе обращение - данные уже загружены:");
            String email = userProxy.getEmail(); // Нет SELECT
            System.out.println("   Email: " + email);
            System.out.println("   💡 Нет повторного SELECT");

            System.out.println("\n4. Сравнение с get():");
            System.out.println("   get() → SELECT сразу, возвращает реальный объект или null");
            System.out.println("   load() → proxy сразу, SELECT при обращении, исключение если объекта нет");
        }

        // Часть 2: Опасности load() - несуществующие объекты
        System.out.println("\n--- Часть 2: Несуществующие объекты ---");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Integer nonExistentId = 777777;

            System.out.println("\n1. load() с несуществующим ID=" + nonExistentId + ":");
            User userProxy = session.load(User.class, nonExistentId);

            System.out.println("   Сразу после load(): proxy создан (не null)");
            System.out.println("   Класс: " + userProxy.getClass().getName());
            System.out.println("   Это proxy? " + userProxy.getClass().getName().contains("$"));
            System.out.println("   💡 load() оптимистично создал proxy");
            System.out.println("   💡 Пока нет исключения!");

            System.out.println("\n2. Попытка обращения к несуществующему proxy:");
            try {
                String name = userProxy.getName(); // Попытка загрузки
                System.out.println("   Имя: " + name); // Не дойдём сюда
            } catch (Exception e) {
                System.out.println("   💥 Исключение: " + e.getClass().getSimpleName());
                System.out.println("   Сообщение: " + e.getMessage());
                System.out.println("   💡 Исключение при попытке загрузки данных!");
            }

            System.out.println("\n3. Сравнение с get():");
            User userGet = session.get(User.class, nonExistentId);
            System.out.println("   get() результат: " + (userGet == null ? "null" : "объект"));
            System.out.println("   get() безопаснее для несуществующих объектов");
        }

        // Часть 3: Проблема с закрытой сессией (LazyInitializationException)
        System.out.println("\n--- Часть 3: Проблема закрытой сессии ---");

        User detachedProxy = null;

        // Создаём proxy в одной сессии
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("\n1. Создаём proxy в сессии:");
            detachedProxy = session.load(User.class, testUserId);
            System.out.println("   Proxy создан: " + detachedProxy.getClass().getName());
            System.out.println("   Сессия открыта, proxy валиден");
        } // Сессия закрывается здесь

        System.out.println("\n2. Сессия закрыта. Пробуем обратиться к proxy:");
        try {
            String name = detachedProxy.getName(); // Попытка загрузить данные
            System.out.println("   Имя: " + name); // Не дойдём сюда
        } catch (Exception e) {
            System.out.println("   💥 Исключение: " + e.getClass().getSimpleName());
            System.out.println("   Сообщение: " + e.getMessage());
            System.out.println("   💡 LazyInitializationException - сессия закрыта!");
            System.out.println("   💡 Proxy требует открытую сессию для загрузки данных");
        }

        // Часть 4: Практическое использование load()
        System.out.println("\n--- Часть 4: Когда load() полезен ---");

        // Создадим Employee с Task для демонстрации lazy связей
        Integer employeeId = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Employee emp = new Employee("Laura", "Director", 9000);
            session.persist(emp);

            EmployeeTask task1 = new EmployeeTask("Strategy", emp, new Date(), "Planning");
            EmployeeTask task2 = new EmployeeTask("Budget", emp, new Date(), "In Progress");
            session.persist(task1);
            session.persist(task2);

            transaction.commit();
            employeeId = emp.getId();
            System.out.println("Создан сотрудник с задачами ID: " + employeeId);
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("\n1. Демонстрация lazy связей:");

            // load() для связи, которая может не понадобиться
            System.out.println("   Загружаем Employee через load():");
            Employee empProxy = session.load(Employee.class, employeeId);
            System.out.println("   Создан proxy для Employee");

            System.out.println("\n2. Условная логика:");
            System.out.println("   // Много кода, который может не потребовать данных сотрудника");
            System.out.println("   // ...");

            boolean needEmployeeDetails = true; // Условная переменная

            if (needEmployeeDetails) {
                System.out.println("   Нужны детали сотрудника...");
                String empName = empProxy.getName(); // SELECT для Employee только здесь
                System.out.println("   Имя сотрудника: " + empName);
                System.out.println("   💡 SELECT выполнен только когда понадобилось");
            } else {
                System.out.println("   Детали сотрудника не нужны");
                System.out.println("   💡 SELECT НЕ выполнен - экономия ресурсов");
            }

            System.out.println("\n3. Lazy коллекции:");
            System.out.println("   // tasks в Employee - lazy по умолчанию");
            System.out.println("   List<EmployeeTask> tasks = empProxy.getTasks();");
            System.out.println("   // SELECT для tasks выполнится только при обращении");
        }

        // Часть 5: Работа с proxy (инициализация, проверка)
        System.out.println("\n--- Часть 5: Работа с proxy ---");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("\n1. Создание proxy через load():");
            User proxy = session.load(User.class, testUserId);

            System.out.println("2. Проверка, инициализирован ли proxy:");
            boolean initialized = Hibernate.isInitialized(proxy);
            System.out.println("   Инициализирован? " + initialized);

            System.out.println("\n3. Явная инициализация proxy:");
            if (!initialized) {
                System.out.println("   Инициализируем...");
                Hibernate.initialize(proxy); // Явная загрузка данных
                System.out.println("   Теперь инициализирован? " + Hibernate.isInitialized(proxy));
            }

            System.out.println("\n4. Получение реального объекта из proxy:");
            // Разворачивание proxy (unproxying)
            if (proxy instanceof HibernateProxy) {
                User realUser = (User) ((HibernateProxy) proxy).getHibernateLazyInitializer()
                        .getImplementation();
                System.out.println("   Реальный класс: " + realUser.getClass().getSimpleName());
            }
        }

        // Часть 6: Рекомендации и выводы
        System.out.println("\n--- Часть 6: Рекомендации ---");

        System.out.println("\n📌 Когда использовать load():");
        System.out.println("  • Уверены, что объект существует в БД");
        System.out.println("  • Нужна ленивая загрузка для оптимизации");
        System.out.println("  • Работаете со связями, которые могут не понадобиться");
        System.out.println("  • Для установки связей без загрузки данных");

        System.out.println("\n📌 Когда НЕ использовать load():");
        System.out.println("  • Нужно проверить существование объекта");
        System.out.println("  • Объект может не существовать");
        System.out.println("  • Сессия может закрыться до использования объекта");
        System.out.println("  • Для простых CRUD операций");

        System.out.println("\n📌 Преимущества load():");
        System.out.println("  • Ленивая загрузка - экономия ресурсов");
        System.out.println("  • Меньше запросов к БД если объект не понадобится");
        System.out.println("  • Полезен для установки связей");

        System.out.println("\n📌 Опасности load():");
        System.out.println("  • ObjectNotFoundException при несуществующих объектах");
        System.out.println("  • LazyInitializationException при закрытой сессии");
        System.out.println("  • Непредсказуемое время выполнения SELECT");

        System.out.println("\n📌 Правила безопасности:");
        System.out.println("  1. Всегда имейте открытую сессию при работе с proxy");
        System.out.println("  2. Используйте Hibernate.initialize() если нужны данные");
        System.out.println("  3. Для проверки существования используйте get(), не load()");
        System.out.println("  4. Обрабатывайте исключения при работе с load()");

        System.out.println("\n📌 Альтернатива:");
        System.out.println("  // Вместо load() с проверкой:");
        System.out.println("  User user = session.get(User.class, id);");
        System.out.println("  if (user != null) {");
        System.out.println("      // безопасная работа");
        System.out.println("  }");

        System.out.println("\n=== Демонстрация завершена ===");
    }

    private static void demonstrateRefreshMethod() {
        System.out.println("\n=== Демонстрация метода refresh() ===");

        // Создадим пользователя
        Integer userId = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            User user = new User("Michael", "michael@example.com", 90);
            session.persist(user);
            tx.commit();
            userId = user.getId();
            System.out.println("Создан пользователь ID: " + userId);
        }

        // Демонстрация refresh()
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            // 1. Загружаем объект
            User user = session.get(User.class, userId);
            System.out.println("\n1. Загружен пользователь:");
            System.out.println("   Имя: " + user.getName());
            System.out.println("   Уровень: " + user.getLevel());

            // 2. Меняем локально (ещё не в БД)
            user.setName("Michael Changed");
            user.setLevel(95);
            System.out.println("\n2. Локальные изменения:");
            System.out.println("   Имя: " + user.getName());
            System.out.println("   Уровень: " + user.getLevel());

            // 3. Refresh - вернёт значения из БД
            System.out.println("\n3. Вызываем refresh():");
            session.refresh(user);
            System.out.println("   После refresh:");
            System.out.println("   Имя: " + user.getName()); // Вернулось исходное
            System.out.println("   Уровень: " + user.getLevel()); // Вернулось исходное
            System.out.println("   💡 Локальные изменения потеряны!");

            tx.commit();
        }

        // Демонстрация с триггерами/хранимыми процедурами
        System.out.println("\n--- Практический случай ---");
        System.out.println("Представьте, что в БД есть триггер,");
        System.out.println("который при сохранении пользователя:");
        System.out.println("1. Приводит имя к верхнему регистру");
        System.out.println("2. Увеличивает уровень на 1");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            User newUser = new User("david", "david@example.com", 50);
            session.persist(newUser);

            System.out.println("\nСоздан пользователь:");
            System.out.println("   Имя: " + newUser.getName());
            System.out.println("   Уровень: " + newUser.getLevel());

            System.out.println("\nТриггер в БД мог изменить данные...");
            System.out.println("Refresh чтобы получить актуальные данные:");

            session.refresh(newUser);
            System.out.println("   После refresh (если бы был триггер):");
            System.out.println("   Имя: " + newUser.getName() + " (может быть 'DAVID')");
            System.out.println("   Уровень: " + newUser.getLevel() + " (может быть 51)");

            tx.commit();
        }

        System.out.println("\n=== Демонстрация завершена ===");
    }

    private static void demonstrateDeletionMethods() {
        System.out.println("\n=== Демонстрация способов удаления ===");

        // Подготовка: создадим пользователей для удаления
        List<Integer> userIds = new ArrayList<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            for (int i = 1; i <= 5; i++) {
                User user = new User("User" + i, "user" + i + "@example.com", i * 10);
                session.persist(user);
                userIds.add(user.getId());
            }

            tx.commit();
            System.out.println("Создано 5 тестовых пользователей");
        }

        // 1. Удаление методом remove()
        System.out.println("\n1. Удаление remove():");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            User user = session.get(User.class, userIds.get(0));
            System.out.println("   Удаляем: " + user.getName());

            session.remove(user); // Помечаем для удаления
            System.out.println("   remove() вызван, объект в состоянии REMOVED");
            System.out.println("   💡 Фактическое удаление в БД при коммите");

            tx.commit();
            System.out.println("   ✅ Пользователь удалён из БД");
        }

        // 2. Каскадное удаление (покажем на Employee и Tasks)
        System.out.println("\n2. Каскадное удаление:");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            // Создаём сотрудника с задачами
            Employee emp = new Employee("Manager", "Boss", 5000);
            session.persist(emp);

            EmployeeTask task1 = new EmployeeTask("Report", emp, new Date(), "New");
            EmployeeTask task2 = new EmployeeTask("Meeting", emp, new Date(), "New");
            session.persist(task1);
            session.persist(task2);

            tx.commit();

            System.out.println("   Создан Employee с 2 задачами");
            System.out.println("   💡 Без каскада: нужно удалять задачи отдельно");
            System.out.println("   💡 С каскадом: удаление Employee удалит задачи");
        }

        // 3. Orphan removal
        System.out.println("\n3. Orphan removal:");
        System.out.println("   // В аннотации @OneToMany:");
        System.out.println("   @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)");
        System.out.println("   private List<Task> tasks;");
        System.out.println("   ");
        System.out.println("   При удалении задачи из списка,");
        System.out.println("   она автоматически удаляется из БД");

        // 4. Удаление JPQL
        System.out.println("\n4. Удаление JPQL:");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            int deleted = session.createQuery("DELETE FROM User WHERE id = :id")
                    .setParameter("id", userIds.get(1))
                    .executeUpdate();

            System.out.println("   Удалено записей: " + deleted);
            System.out.println("   💡 Прямое удаление в БД, минуя кэш");

            tx.commit();
        }

        // 5. Удаление NativeQuery
        System.out.println("\n5. Удаление NativeQuery:");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            int deleted = session.createNativeQuery("DELETE FROM users WHERE id = :id")
                    .setParameter("id", userIds.get(2))
                    .executeUpdate();

            System.out.println("   Удалено записей: " + deleted);
            System.out.println("   💡 Нативный SQL, полезно для сложных удалений");

            tx.commit();
        }

        // 6. Мягкое удаление (soft delete)
        System.out.println("\n6. Мягкое удаление (soft delete):");
        System.out.println("   // Вместо удаления помечаем запись:");
        System.out.println("   @Entity");
        System.out.println("   @Table(name = \"users\")");
        System.out.println("   @SQLDelete(sql = \"UPDATE users SET deleted = true WHERE id = ?\")");
        System.out.println("   @Where(clause = \"deleted = false\")");
        System.out.println("   public class User {");
        System.out.println("       private boolean deleted;");
        System.out.println("   }");
        System.out.println("   ");
        System.out.println("   Преимущества:");
        System.out.println("   • История изменений");
        System.out.println("   • Возможность восстановления");
        System.out.println("   • Нет потери связанных данных");

        // Проверим, сколько осталось пользователей
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery("SELECT COUNT(*) FROM User", Long.class)
                    .getSingleResult();
            System.out.println("\nОсталось пользователей в БД: " + count);
        }

        System.out.println("\n=== Демонстрация завершена ===");
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