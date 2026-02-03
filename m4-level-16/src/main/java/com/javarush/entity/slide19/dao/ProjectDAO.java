package com.javarush.entity.slide19.dao;

import com.javarush.entity.slide19.NewProject;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class ProjectDAO {
    private final Session session; // Сессия передается извне (например, из сервиса)

    public List<NewProject> getProjectList(int offset, int limit) {
        String hql = "FROM NewProject"; // Используем имя Entity-класса
        Query<NewProject> query = session.createQuery(hql, NewProject.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public Long getTotalProjectCount() {
        String hql = "SELECT COUNT(p) FROM NewProject p";
        Query<Long> query = session.createQuery(hql, Long.class);
        return query.getSingleResult();
    }

    public Optional<NewProject> getProjectByName(String name) {
        String hql = "FROM NewProject p WHERE p.name = :projectName";
        Query<NewProject> query = session.createQuery(hql, NewProject.class);
        query.setParameter("projectName", name);
        return query.uniqueResultOptional(); // Используем Optional для безопасного возврата
    }

    public void saveProject(NewProject project) {
        session.save(project);
    }
}