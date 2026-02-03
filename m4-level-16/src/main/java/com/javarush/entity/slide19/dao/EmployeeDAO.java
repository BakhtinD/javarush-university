package com.javarush.entity.slide19.dao;

import com.javarush.entity.slide19.EmployeeDaoExample;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class EmployeeDAO {
    private final Session session;

    public EmployeeDAO(Session session) {
        this.session = session;
    }

    // CRUD операции
    public void saveEmployee(EmployeeDaoExample employee) {
        session.beginTransaction();
        try {
            session.save(employee);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
    }

    public EmployeeDaoExample getEmployeeById(Long id) {
        return session.get(EmployeeDaoExample.class, id);
    }

    public void updateEmployee(EmployeeDaoExample employee) {
        session.beginTransaction();
        try {
            session.update(employee);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
    }

    public void deleteEmployee(EmployeeDaoExample employee) {
        session.beginTransaction();
        try {
            session.delete(employee);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
    }

    // Методы для поиска и фильтрации
    public List<EmployeeDaoExample> getAllEmployees() {
        return session.createQuery("FROM EmployeeDaoExample", EmployeeDaoExample.class).list();
    }

    public int getEmployeeCount() {
        Query<Long> query = session.createQuery("SELECT COUNT(e) FROM EmployeeDaoExample e", Long.class);
        return query.uniqueResult().intValue();
    }

    public List<EmployeeDaoExample> getEmployeeList(int offset, int limit) {
        Query<EmployeeDaoExample> query = session.createQuery("FROM EmployeeDaoExample ORDER BY id", EmployeeDaoExample.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.list();
    }

    public EmployeeDaoExample getEmployeeByUniqueName(String fullName) {
        Query<EmployeeDaoExample> query = session.createQuery(
                "FROM EmployeeDaoExample WHERE fullName = :name", EmployeeDaoExample.class);
        query.setParameter("name", fullName);
        return query.uniqueResult();
    }

    public List<EmployeeDaoExample> getEmployeesByPosition(String position) {
        Query<EmployeeDaoExample> query = session.createQuery(
                "FROM EmployeeDaoExample WHERE position = :position", EmployeeDaoExample.class);
        query.setParameter("position", position);
        return query.list();
    }

    public List<EmployeeDaoExample> getEmployeesBySalaryRange(double minSalary, double maxSalary) {
        Query<EmployeeDaoExample> query = session.createQuery(
                "FROM EmployeeDaoExample WHERE salary BETWEEN :min AND :max", EmployeeDaoExample.class);
        query.setParameter("min", minSalary);
        query.setParameter("max", maxSalary);
        return query.list();
    }

    public List<EmployeeDaoExample> getEmployeesByHireDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        Query<EmployeeDaoExample> query = session.createQuery(
                "FROM EmployeeDaoExample WHERE hireDate BETWEEN :start AND :end", EmployeeDaoExample.class);
        query.setParameter("start", startDate);
        query.setParameter("end", endDate);
        return query.list();
    }

    // Методы для агрегации и статистики
    public double getAverageSalary() {
        Query<Double> query = session.createQuery("SELECT AVG(e.salary) FROM EmployeeDaoExample e", Double.class);
        return query.uniqueResult();
    }

    public double getMaxSalary() {
        Query<Double> query = session.createQuery("SELECT MAX(e.salary) FROM EmployeeDaoExample e", Double.class);
        return query.uniqueResult();
    }

    public double getMinSalary() {
        Query<Double> query = session.createQuery("SELECT MIN(e.salary) FROM EmployeeDaoExample e", Double.class);
        return query.uniqueResult();
    }

    public List<Object[]> getEmployeesGroupByPosition() {
        Query<Object[]> query = session.createQuery(
                "SELECT e.position, COUNT(e), AVG(e.salary) FROM EmployeeDaoExample e GROUP BY e.position", Object[].class);
        return query.list();
    }

    // Методы для пагинации и сортировки
    public List<EmployeeDaoExample> getEmployeesSortedBySalaryDesc(int limit) {
        Query<EmployeeDaoExample> query = session.createQuery(
                "FROM EmployeeDaoExample ORDER BY salary DESC", EmployeeDaoExample.class);
        query.setMaxResults(limit);
        return query.list();
    }

    public List<EmployeeDaoExample> getEmployeesSortedByHireDateAsc() {
        return session.createQuery(
                "FROM EmployeeDaoExample ORDER BY hireDate ASC", EmployeeDaoExample.class).list();
    }

    // Методы для проверки существования
    public boolean employeeExists(String fullName) {
        Query<Long> query = session.createQuery(
                "SELECT COUNT(e) FROM EmployeeDaoExample e WHERE fullName = :name", Long.class);
        query.setParameter("name", fullName);
        return query.uniqueResult() > 0;
    }

    public boolean employeeWithEmailExists(String email) {
        Query<Long> query = session.createQuery(
                "SELECT COUNT(e) FROM EmployeeDaoExample e WHERE email = :email", Long.class);
        query.setParameter("email", email);
        return query.uniqueResult() > 0;
    }

    // Утилитарные методы
    public void close() {
        if (session != null && session.isOpen()) {
            session.close();
        }
    }

    // Метод для массовых операций (batch processing)
    public void bulkUpdateSalary(String position, double percentageIncrease) {
        session.beginTransaction();
        try {
            Query<?> query = session.createQuery(
                    "UPDATE EmployeeDaoExample e SET e.salary = e.salary * :increase WHERE e.position = :position");
            query.setParameter("increase", 1 + (percentageIncrease / 100));
            query.setParameter("position", position);
            query.executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
    }

    public void bulkDeleteByPosition(String position) {
        session.beginTransaction();
        try {
            Query<?> query = session.createQuery(
                    "DELETE FROM EmployeeDaoExample e WHERE e.position = :position");
            query.setParameter("position", position);
            query.executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
    }

    // Методы для поиска с использованием Criteria API
    public List<EmployeeDaoExample> searchEmployees(String namePattern, String position, Double minSalary, Double maxSalary) {
        var builder = session.getCriteriaBuilder();
        var criteria = builder.createQuery(EmployeeDaoExample.class);
        var root = criteria.from(EmployeeDaoExample.class);

        var predicates = new java.util.ArrayList<javax.persistence.criteria.Predicate>();

        if (namePattern != null && !namePattern.isEmpty()) {
            predicates.add(builder.like(root.get("fullName"), "%" + namePattern + "%"));
        }

        if (position != null && !position.isEmpty()) {
            predicates.add(builder.equal(root.get("position"), position));
        }

        if (minSalary != null) {
            predicates.add(builder.ge(root.get("salary"), minSalary));
        }

        if (maxSalary != null) {
            predicates.add(builder.le(root.get("salary"), maxSalary));
        }

        if (!predicates.isEmpty()) {
            criteria.where(builder.and(predicates.toArray(new javax.persistence.criteria.Predicate[0])));
        }

        criteria.orderBy(builder.asc(root.get("fullName")));

        return session.createQuery(criteria).getResultList();
    }
}