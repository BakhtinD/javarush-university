package com.javarush.entity.slide22;

import com.javarush.entity.slide22.GenericDAO;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;

public class TaskDAO extends GenericDAO<Task, Long> {

    // Конструктор передаёт класс сущности и сессию в родительский класс
    public TaskDAO(Session session) {
        super(Task.class, session);
    }

    // Специфичный метод, не входящий в стандартный набор
    public List<Task> findCompletedTasks() {
        String hql = "FROM Task t WHERE t.completed = true ORDER BY t.id";
        Query<Task> query = session.createQuery(hql, Task.class);
        return query.getResultList();
    }

    // Ещё один специфичный метод
    public List<Task> findTasksByTitleKeyword(String keyword) {
        String hql = "FROM Task t WHERE LOWER(t.title) LIKE LOWER(:keyword)";
        Query<Task> query = session.createQuery(hql, Task.class);
        query.setParameter("keyword", "%" + keyword + "%");
        return query.getResultList();
    }
}