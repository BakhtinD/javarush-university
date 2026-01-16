package com.javarush;

import com.javarush.entity.Employee;
import com.javarush.entity.EmployeeTask;
import com.javarush.entity.Product;
import com.javarush.entity.User;
import com.javarush.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.Serializable;
import java.util.Date;

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
            System.out.println("4. 💥 Исключение: " + e.getClass().getSimpleName());
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
            System.out.println("   💡 SQL INSERT ещё НЕ выполнен!");

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
            System.out.println("   💡 Возможен batch-режим (одним запросом)");

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
                System.out.println("   💥 Исключение: " + e.getMessage());
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
                System.out.println("💡 Hibernate создал НОВУЮ запись с новым ID!");
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
            System.out.println("💡 Обратите внимание: сам объект изменил состояние!");

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
                session.update(transientUser); // ❌ Ошибка!
                System.out.println("update() выполнен"); // Не дойдём сюда
            } catch (Exception e) {
                System.out.println("💥 Исключение: " + e.getClass().getSimpleName());
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
                session.update(detachedForUpdate); // 💥 Проблема!
                System.out.println("update() выполнен");
            } catch (Exception e) {
                System.out.println("💥 Исключение: " + e.getClass().getSimpleName());
                System.out.println("Нельзя иметь два persistent объекта с одним ID в сессии!");
            }

            // Пробуем merge() - работает
            System.out.println("\nПробуем merge():");
            detachedForMerge.setName("Merge Name");
            User merged = (User) session.merge(detachedForMerge); // ✅ Работает
            System.out.println("merge() выполнен успешно");
            System.out.println("merge() вернул новый объект: " + (merged != detachedForMerge));
            System.out.println("Оригинальный detached объект остался detached");

            transaction.commit();
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