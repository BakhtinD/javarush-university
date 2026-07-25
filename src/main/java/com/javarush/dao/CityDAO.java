package com.javarush.dao;

import com.javarush.domain.City;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class CityDAO {

    public List<City> getItems(int offset, int limit, Session session) {
        Query<City> query = session.createQuery("select city from City city" +
                " left join fetch city.country country", City.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.list();
    }

    public int getTotalCount(Session session) {
        Query<Long> query = session.createQuery("select count(c) from City c", Long.class);
        return query.uniqueResultOptional().map(Math::toIntExact).orElse(0);
    }

    public List<City> getAll(Session session) {
        Query<City> query = session.createQuery("select c from City c left join fetch c.country", City.class);
        return query.getResultList();
    }

    public Optional<City> getById(int id, Session session) {
        Query<City> query = session.createQuery("select c from City c left join fetch c.country where c.id = :id", City.class);
        query.setParameter("id", id);
        return query.uniqueResultOptional();
    }

}
