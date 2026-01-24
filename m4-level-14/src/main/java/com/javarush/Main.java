package com.javarush;

import com.javarush.entity.Comment;
import com.javarush.entity.Department;
import com.javarush.entity.User;
import com.javarush.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        // demonstrateSlideN();

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
