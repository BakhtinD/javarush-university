package com.javarush;

import com.javarush.entity.*;
import com.javarush.util.HibernateUtil;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // demonstrateSlideN();

        demonstrateSlide4();

        demonstrateSlide5();

        demonstrateSlide6();

        demonstrateSlide7();

        demonstrateSlide8();

        demonstrateSlide9();

        demonstrateSlide10();

    }

    private static void demonstrateSlide10() {
        System.out.println("=== JOIN FETCH Limitations Demo ===");

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        System.out.println("\n1. Creating test data...");

        // Create students
        Student student1 = new Student();
        student1.setName("Alice Brown");
        session.save(student1);

        Student student2 = new Student();
        student2.setName("Bob Wilson");
        session.save(student2);

        System.out.println("   - 2 students created");

        // Create grades
        for (int i = 1; i <= 6; i++) {
            Grade grade = new Grade();
            grade.setSubject("Math");
            grade.setScore(70 + i * 3);
            grade.setDate(LocalDate.now().minusDays(i));
            grade.setStudent(i % 2 == 0 ? student2 : student1);
            session.save(grade);
        }

        System.out.println("   - 6 grades created");

        tx.commit();
        session.close();

        System.out.println("\n2. PROBLEM: JOIN FETCH with pagination");

        session = HibernateUtil.getSessionFactory().openSession();

        // Problematic query: JOIN FETCH with pagination
        String hql = "SELECT DISTINCT g FROM Grade g " +
                "LEFT JOIN FETCH g.student " +
                "ORDER BY g.date DESC";

        Query<Grade> query = session.createQuery(hql, Grade.class);
        query.setFirstResult(0);
        query.setMaxResults(3); // Want only 3 latest grades

        System.out.println("\n   Query: get 3 latest grades with students");
        System.out.println("   Check SQL logs - warning expected!");

        List<Grade> grades = query.list();

        System.out.println("\n   Result: " + grades.size() + " grades returned");
        grades.forEach(g ->
                System.out.println("   - Grade: " + g.getScore() +
                        " in " + g.getSubject() +
                        ", Student: " + g.getStudent().getName() +
                        ", Date: " + g.getDate())
        );

        session.close();

        System.out.println("\n3. SQL executed (see logs):");
        System.out.println("   SELECT g.*, s.* FROM grade g");
        System.out.println("   LEFT JOIN student s ON g.student_id = s.id");
        System.out.println("   ORDER BY g.date DESC");
        System.out.println("   -- NO LIMIT 3 in SQL!");

        System.out.println("\n   Hibernate loaded ALL 6 grades,");
        System.out.println("   then selected first 3 in memory.");

        System.out.println("\n=== KEY POINT ===");
        System.out.println("JOIN FETCH + pagination = BAD");
        System.out.println("All data loaded → memory waste");

        System.out.println("\n=== END ===");
    }

    private static void demonstrateSlide9() {
        System.out.println("=== JOIN FETCH Demo ===");

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        System.out.println("\n1. Creating test data...");

        Project project = new Project();
        project.setName("Website Redesign");
        session.save(project);
        System.out.println("   - Project created: " + project.getName());

        Task task1 = new Task();
        task1.setDescription("Design homepage");
        task1.setCompleted(true);
        task1.setProject(project);
        session.save(task1);

        Task task2 = new Task();
        task2.setDescription("Implement backend");
        task2.setCompleted(false);
        task2.setProject(project);
        session.save(task2);

        Task task3 = new Task();
        task3.setDescription("Write tests");
        task3.setCompleted(false);
        task3.setProject(project);
        session.save(task3);

        System.out.println("   - 3 tasks created for project");

        tx.commit();
        session.close();

        System.out.println("\n2. PROBLEM: N+1 without JOIN FETCH:");
        System.out.println("   (Check SQL logs - will show 4 queries)");

        session = HibernateUtil.getSessionFactory().openSession();

        // Bad approach: causes N+1
        Project badProject = session.get(Project.class, project.getId());
        System.out.println("\n   Project: " + badProject.getName());

        System.out.println("   Iterating tasks (N+1)...");
        for (Task task : badProject.getTasks()) {
            System.out.println("   - Task: " + task.getDescription() +
                    " (completed: " + task.isCompleted() + ")");
        }

        session.close();

        System.out.println("\n3. SOLUTION: Using JOIN FETCH:");
        System.out.println("   (Check SQL logs - will show only 1 query)");

        session = HibernateUtil.getSessionFactory().openSession();

        // Good approach: JOIN FETCH
        String hql = "SELECT DISTINCT p FROM Project p " +
                "LEFT JOIN FETCH p.tasks t " +
                "WHERE p.id = :projectId";

        Query<Project> query = session.createQuery(hql, Project.class);
        query.setParameter("projectId", project.getId());

        Project goodProject = query.uniqueResult();
        System.out.println("\n   Project with JOIN FETCH: " + goodProject.getName());

        System.out.println("   Tasks initialized? " +
                Hibernate.isInitialized(goodProject.getTasks()));

        System.out.println("   Iterating tasks (single query)...");
        for (Task task : goodProject.getTasks()) {
            System.out.println("   - Task: " + task.getDescription() +
                    " (completed: " + task.isCompleted() + ")");
        }

        session.close();

        System.out.println("\n=== KEY DIFFERENCES ===");
        System.out.println("Without JOIN FETCH:");
        System.out.println("  • 1 query for project");
        System.out.println("  • +1 query for each task (N+1 problem)");
        System.out.println("  • Total: 4 queries for 3 tasks");

        System.out.println("\nWith JOIN FETCH:");
        System.out.println("  • 1 query with JOIN");
        System.out.println("  • All data loaded at once");
        System.out.println("  • Total: 1 query only");

        System.out.println("\n=== END ===");
    }

    private static void demonstrateSlide8() {
        System.out.println("=== N+1 Problem Demo ===");

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        System.out.println("\n1. Creating test data...");

        Customer customer = new Customer();
        customer.setName("John Smith");
        session.save(customer);
        System.out.println("   - Customer created: " + customer.getName());

        // Create 3 orders (in real life could be 1000+)
        Order order1 = new Order();
        order1.setProduct("Laptop");
        order1.setAmount(999.99);
        order1.setCustomer(customer);
        session.save(order1);

        Order order2 = new Order();
        order2.setProduct("Phone");
        order2.setAmount(499.99);
        order2.setCustomer(customer);
        session.save(order2);

        Order order3 = new Order();
        order3.setProduct("Tablet");
        order3.setAmount(299.99);
        order3.setCustomer(customer);
        session.save(order3);

        System.out.println("   - 3 orders created for customer");

        tx.commit();
        session.close();

        System.out.println("\n2. Demonstrating N+1 problem:");
        System.out.println("   (Check SQL logs in console)");

        session = HibernateUtil.getSessionFactory().openSession();

        System.out.println("\n   Loading customer...");
        Customer loadedCustomer = session.get(Customer.class, customer.getId());

        System.out.println("   Customer: " + loadedCustomer.getName());
        System.out.println("   Orders initialized? " +
                Hibernate.isInitialized(loadedCustomer.getOrders()));

        System.out.println("\n3. Iterating through orders (N+1 happens here):");
        System.out.println("   Customer orders:");

        // N+1 PROBLEM: 1 query for customer + 3 queries for orders = 4 queries total
        for (Order order : loadedCustomer.getOrders()) {
            System.out.println("   - Product: " + order.getProduct() +
                    ", Amount: $" + order.getAmount());
        }

        session.close();

        System.out.println("\n=== PROBLEM SUMMARY ===");
        System.out.println("Executed queries:");
        System.out.println("1. SELECT customer WHERE id = ?");
        System.out.println("2. SELECT order WHERE customer_id = ? AND id = ?");
        System.out.println("3. SELECT order WHERE customer_id = ? AND id = ?");
        System.out.println("4. SELECT order WHERE customer_id = ? AND id = ?");
        System.out.println("\nTotal: 4 queries for 3 orders (N+1 = 3+1)");
        System.out.println("Optimal: 1 query with JOIN or WHERE IN");

        System.out.println("\n=== NEXT SLIDE SOLUTION: JOIN FETCH ===");
    }

    private static void demonstrateSlide7() {
        System.out.println("=== LazyCollectionOption.EXTRA Demo ===");

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        System.out.println("\n1. Creating test data...");

        Article article = new Article();
        article.setTitle("Java Performance Guide");
        session.save(article);
        System.out.println("   - Article created: " + article.getTitle());

        Tag tag1 = new Tag();
        tag1.setName("Java");
        tag1.setOrder(0);
        article.addTag(tag1);
        session.save(tag1);

        Tag tag2 = new Tag();
        tag2.setName("Performance");
        tag2.setOrder(1);
        article.addTag(tag2);
        session.save(tag2);

        Tag tag3 = new Tag();
        tag3.setName("Hibernate");
        tag3.setOrder(2);
        article.addTag(tag3);
        session.save(tag3);

        System.out.println("   - 3 tags created with order");

        tx.commit();
        session.close();

        System.out.println("\n2. Demonstrating EXTRA lazy loading:");

        session = HibernateUtil.getSessionFactory().openSession();

        System.out.println("\n   Loading Article...");
        Article loadedArticle = session.get(Article.class, article.getId());

        System.out.println("   Article loaded: " + loadedArticle.getTitle());
        System.out.println("   Tags initialized? " +
                Hibernate.isInitialized(loadedArticle.getTags()));

        System.out.println("\n3. Getting collection size (EXTRA feature):");
        System.out.println("   Tags count: " + loadedArticle.getTags().size());
        System.out.println("   Tags initialized after size()? " +
                Hibernate.isInitialized(loadedArticle.getTags()));

        System.out.println("\n4. Getting specific tag (with @OrderColumn):");
        System.out.println("   Getting tag at index 1...");
        Tag secondTag = loadedArticle.getTags().get(1);
        System.out.println("   Second tag: " + secondTag.getName());
        System.out.println("   Tags initialized after get(1)? " +
                Hibernate.isInitialized(loadedArticle.getTags()));

        System.out.println("\n5. Iterating through all tags:");
        System.out.println("   All tags:");
        loadedArticle.getTags().forEach(t ->
                System.out.println("   - " + t.getName())
        );
        System.out.println("   Tags initialized after iteration? " +
                Hibernate.isInitialized(loadedArticle.getTags()));

        session.close();

        System.out.println("\n=== END ===");
    }

    private static void demonstrateSlide6() {
        System.out.println("=== @LazyCollection Demo ===");

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        // Create data
        Team team = new Team();
        team.setName("Dream Team");
        session.save(team);

        Player player = new Player();
        player.setName("John Striker");
        player.setTeam(team);
        session.save(player);

        Goal goal1 = new Goal();
        goal1.setMatch("Final Match");
        goal1.setPlayer(player);
        session.save(goal1);

        tx.commit();
        session.close();

        // Demonstrate
        session = HibernateUtil.getSessionFactory().openSession();
        Team loadedTeam = session.get(Team.class, team.getId());

        System.out.println("Team loaded: " + loadedTeam.getName());
        System.out.println("Players initialized? " +
                Hibernate.isInitialized(loadedTeam.getPlayers()));

        // Access players - LAZY load
        System.out.println("Accessing players...");
        loadedTeam.getPlayers().forEach(p -> {
            System.out.println("Player: " + p.getName());
            // Goals are EAGER loaded
            System.out.println("Goals count: " + p.getGoals().size());
        });

        session.close();

        System.out.println("=== END ===");
    }

    private static void demonstrateSlide5() {
        System.out.println("=== Демонстрация Slide 5: Значения по умолчанию ===");

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        System.out.println("\n1. Создаем тестовые данные:");

        // Создаем издателя
        Publisher publisher = new Publisher();
        publisher.setName("Tech Books Publishing");
        session.save(publisher);
        System.out.println("   - Создан Publisher: " + publisher.getName());

        // Создаем автора
        Author author = new Author();
        author.setName("Anna Ivanova");
        author.setPublisher(publisher);
        session.save(author);
        System.out.println("   - Создан Author: " + author.getName());

        // Создаем книги
        Book book1 = new Book();
        book1.setTitle("Hibernate");
        book1.setAuthor(author);
        session.save(book1);

        Book book2 = new Book();
        book2.setTitle("Hibernate 2");
        book2.setAuthor(author);
        session.save(book2);

        System.out.println("   - Создано 2 книги автора");

        // Создаем отзывы
        Review review1 = new Review();
        review1.setText("Best!");
        review1.setRating(5);
        book1.addReview(review1);
        session.save(review1);

        Review review2 = new Review();
        review2.setText("Ok");
        review2.setRating(4);
        book1.addReview(review2);
        session.save(review2);

        System.out.println("   - Создано 2 отзыва на первую книгу");

        tx.commit();
        session.close();

        System.out.println("\n2. Демонстрация значений по умолчанию:");

        session = HibernateUtil.getSessionFactory().openSession();

        System.out.println("\n   Загружаем книгу по ID...");
        Book loadedBook = session.get(Book.class, book1.getId());

        System.out.println("   Книга загружена: " + loadedBook.getTitle());

        // ManyToOne: EAGER по умолчанию - автор загружен сразу
        System.out.println("   Автор (ManyToOne - EAGER по умолчанию): " +
                loadedBook.getAuthor().getName());

        // OneToMany: LAZY по умолчанию - отзывы еще не загружены
        System.out.println("   Отзывы (OneToMany - LAZY по умолчанию):");
        System.out.println("   Инициализированы? " +
                Hibernate.isInitialized(loadedBook.getReviews()));

        // Теперь загружаем отзывы
        System.out.println("\n   Обращаемся к отзывам...");
        List<Review> reviews = loadedBook.getReviews(); // Запрос выполнится здесь
        System.out.println("   Количество отзывов: " + reviews.size());
        reviews.forEach(r ->
                System.out.println("   - " + r.getText() + " (рейтинг: " + r.getRating() + ")")
        );

        // Проверяем издателя через автора (цепочка связей)
        System.out.println("\n3. Проверка цепочки связей:");
        Author bookAuthor = loadedBook.getAuthor();
        System.out.println("   Автор книги: " + bookAuthor.getName());

        // OneToMany: LAZY по умолчанию - книги автора еще не загружены
        System.out.println("   Книги автора (OneToMany - LAZY):");
        System.out.println("   Инициализированы? " +
                Hibernate.isInitialized(bookAuthor.getBooks()));

        // ManyToOne: EAGER по умолчанию - издатель автора загружен сразу
        System.out.println("   Издатель автора (ManyToOne - EAGER): " +
                bookAuthor.getPublisher().getName());

        session.close();

        System.out.println("\n=== Итог: ===");
        System.out.println("• @ManyToOne → EAGER (загружается сразу)");
        System.out.println("• @OneToMany → LAZY (загружается при обращении)");
        System.out.println("• Это значения ПО УМОЛЧАНИЮ, их можно переопределять!");

        System.out.println("\n=== Конец демонстрации ===");
    }

    private static void demonstrateSlide4() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        // Создаем тестовые данные
        Department dept = new Department();
        dept.setName("IT");
        session.save(dept);

        User user = new User();
        user.setName("John Doe");
        user.setDepartment(dept);
        session.save(user);

        Comment comment1 = new Comment();
        comment1.setText("First comment");
        comment1.setUser(user);
        session.save(comment1);

        Comment comment2 = new Comment();
        comment2.setText("Second comment");
        comment2.setUser(user);
        session.save(comment2);

        session.getTransaction().commit();
        session.close();

        // Новая сессия для демонстрации LAZY
        session = HibernateUtil.getSessionFactory().openSession();
        User loadedUser = session.get(User.class, user.getId());

        System.out.println("User loaded: " + loadedUser.getName());
        System.out.println("Department (EAGER) loaded immediately: " +
                loadedUser.getDepartment().getName());

        // Комментарии (LAZY) загрузятся только при обращении
        System.out.println("Accessing comments (LAZY)...");
        List<Comment> comments = loadedUser.getComments(); // Здесь выполнится запрос
        comments.forEach(c -> System.out.println("Comment: " + c.getText()));

        session.close();
    }

    private static void demonstrateSlideN() {
        //
    }

}
