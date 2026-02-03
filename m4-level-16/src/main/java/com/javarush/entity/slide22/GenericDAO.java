package com.javarush.entity.slide22;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.query.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public abstract class GenericDAO<T, ID extends Serializable> {
    private final Class<T> entityClass;
    protected final Session session; // Наследники имеют доступ к сессии

    // Получить сущность по ID
    public Optional<T> getById(ID id) {
        return Optional.ofNullable(session.get(entityClass, id));
    }

    // Получить все сущности
    public List<T> getAll() {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(entityClass);
        criteria.from(entityClass);
        return session.createQuery(criteria).getResultList();
    }

    // Получить список сущностей с пагинацией
    public List<T> getItems(int offset, int limit) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(entityClass);
        criteria.from(entityClass);
        Query<T> query = session.createQuery(criteria);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    // Получить общее количество сущностей
    public Long getCount() {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<T> root = criteria.from(entityClass);
        criteria.select(builder.count(root));
        return session.createQuery(criteria).getSingleResult();
    }

    // Сохранить или обновить сущность
    public T save(T entity) {
        session.saveOrUpdate(entity); // Универсальный метод Hibernate
        return entity;
    }

    // Удалить сущность
    public void delete(T entity) {
        session.delete(entity);
    }

    // Удалить сущность по ID
    public void deleteById(ID id) {
        getById(id).ifPresent(this::delete);
    }
}