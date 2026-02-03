package com.javarush.dao.slide19;

import com.javarush.entity.slide19.Employee;
import org.hibernate.Session;
import org.hibernate.query.Query;
import com.javarush.util.HibernateUtil;

import java.util.List;

public class EmployeeDAO {

    private Session getCurrentSession() {
        return HibernateUtil.getSessionFactory().getCurrentSession();
    }

    // 1. Получение списка сотрудников с пагинацией
    public List<Employee> getEmployeeList(int from, int count) {
        Session session = getCurrentSession();
        String hqlQuery = "from Employee";
        Query<Employee> query = session.createQuery(hqlQuery, Employee.class);
        query.setFirstResult(from);
        query.setMaxResults(count);
        return query.getResultList();
    }

    // 2. Получение общего количества сотрудников
    public int getEmployeeCount() {
        Session session = getCurrentSession();
        String hqlQuery = "select count(*) from Employee";
        Query<Long> query = session.createQuery(hqlQuery, Long.class);
        return query.getSingleResult().intValue();
    }

    // 3. Поиск сотрудника по уникальному имени
    public Employee getEmployeeByUniqueName(String name) {
        Session session = getCurrentSession();
        String hqlQuery = "from Employee where fullName = :name";
        Query<Employee> query = session.createQuery(hqlQuery, Employee.class);
        query.setParameter("name", name);
        return query.getSingleResult();
    }

    // 4. Дополнительный метод: сохранение сотрудника
    public void saveEmployee(Employee employee) {
        Session session = getCurrentSession();
        session.save(employee);
    }

    // 5. Дополнительный метод: получение сотрудников по должности
    public List<Employee> getEmployeesByPosition(String position) {
        Session session = getCurrentSession();
        String hqlQuery = "from Employee where position = :position";
        Query<Employee> query = session.createQuery(hqlQuery, Employee.class);
        query.setParameter("position", position);
        return query.getResultList();
    }
}