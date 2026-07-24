package com.javarush;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javarush.dao.CityDAO;
import com.javarush.domain.City;
import com.javarush.domain.Country;
import com.javarush.domain.CountryLanguage;
import com.javarush.redis.CityDetail;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

public class Main {

    private final SessionFactory sessionFactory;
    private final CityDAO cityDAO;
    private final RedisClient redisClient;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public Main() {
        sessionFactory = prepareSessionFactory();
        cityDAO = new CityDAO();
        redisClient = createRedisClient();
    }

    public static void main(String[] args) {
        Main app = new Main();
        List<City> cities = app.fetchData();
        System.out.println("total cities: " + cities.size());
        List<CityDetail> details = app.transformData(cities);
        app.pushToRedis(details);

        org.hibernate.stat.Statistics stats = app.sessionFactory.getStatistics();
        System.out.println("PrepareStatement count: " + stats.getPrepareStatementCount());
        System.out.println("Query execution count: " + stats.getQueryExecutionCount());
        System.out.println("Total CityDetails count: " + details.size());
        System.out.println("Num of records in Redis: " + app.getCountFromRedis());



        app.shutdown();
    }

    private RedisClient createRedisClient() {

        RedisClient client = RedisClient.create(RedisURI.create("localhost", 6379));
        try (StatefulRedisConnection<String, String> connection = client.connect()) {
            connection.sync().flushdb();
            System.out.println("Connected to Redis");
        }
        return client;

    }

    private List<CityDetail> transformData(List<City> countries) {
        return countries.stream().map(city -> {
            CityDetail detail = new CityDetail();
            detail.setId(city.getId());
            detail.setName(city.getName());
            detail.setPopulation(city.getPopulation());
            detail.setCountry(city.getCountry());
            detail.setDistrict(city.getDistrict());
            return detail;
        }).toList();
    }

    private void pushToRedis(List<CityDetail> cityCountries) {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisStringCommands<String, String> stringCommands = connection.sync();
            for (CityDetail detail : cityCountries) {
                String key = "city: " + detail.getId();
                String value = objectMapper.writeValueAsString(detail);
                stringCommands.set(key, value);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private List<City> fetchData() {
        try (Session session = this.sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            int totalCount = this.cityDAO.getTotalCount(session);
            // заменил цикл на batch
            List<City> allCities = new ArrayList<>(this.cityDAO.getItems(0, totalCount, session));
            session.getTransaction().commit();
            return allCities;
        }
    }

    // Получить размер данных из Redis
    private Long getCountFromRedis() {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            return connection.sync().dbsize();
        }
    }

    private SessionFactory prepareSessionFactory() {
        Properties properties = new Properties();
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        //properties.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        //properties.put(Environment.URL, "jdbc:mysql://localhost:3306/world");
        properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/world");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "sakila");
        properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        properties.put(Environment.HBM2DDL_AUTO, "validate");
        properties.put(Environment.STATEMENT_BATCH_SIZE, "100");
        properties.put("hibernate.generate_statistics", "true");

        return new Configuration().addAnnotatedClass(City.class).addAnnotatedClass(Country.class)
                .addAnnotatedClass(CountryLanguage.class).addProperties(properties)
                .buildSessionFactory();
    }

    //todo недоделано
    private void testRedisData(List<Integer> keys) {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisStringCommands<String, String> stringCommands = connection.sync();
            for (Integer id : keys) {
                // Получить из Redis
                String JSON = stringCommands.get("city: " + id);
                objectMapper.readValue(JSON, CityDetail.class);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    //todo недоделано
    private void testMySqlData(List<Integer> ids) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            for (Integer id : ids) {
                cityDAO.getById(id, session);
                // lazy load is implemented on query level
            }
            session.getTransaction().commit();
        }
    }


    private void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }


}
