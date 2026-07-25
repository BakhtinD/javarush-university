package com.javarush;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PerformanceTest {

    private static final List<Integer> testIds = List.of(1, 20, 45, 100, 250, 300, 400, 500, 600,
            700);
    private static SessionFactory factory;
    private static CityDAO cityDAO;
    private static RedisClient redisClient;
    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setup() {
        factory = preparedRelationDb();
        cityDAO = new CityDAO();
        redisClient = RedisClient.create(RedisURI.create("localhost", 6379));
        try (StatefulRedisConnection<String, String> ignored = redisClient.connect()) {
            System.out.println("Connected to Redis");
        }

        objectMapper = new ObjectMapper();

        List<City> cities;
        try (Session session = factory.openSession()) {
            Transaction transaction = session.getTransaction();
            transaction.begin();
            cities = cityDAO.getAll(session);
            transaction.commit();
        }

        List<CityDetail> details = transformData(cities);
        pushToRedis(details);

    }

    @AfterAll
    static void tearDown() {
        if (factory != null && !factory.isClosed()) {
            factory.close();
        }
        if (redisClient != null) {
            redisClient.shutdown();
        }

    }

    private static void pushToRedis(List<CityDetail> data) {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisStringCommands<String, String> stringCommands = connection.sync();
            for (CityDetail detail : data) {
                String key = "city: " + detail.getId();
                String value = objectMapper.writeValueAsString(detail);
                stringCommands.set(key, value);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<CityDetail> transformData(List<City> cities) {
        return cities.stream().map(city -> {
            CityDetail detail = new CityDetail();
            detail.setId(city.getId());
            detail.setName(city.getName());
            detail.setCountry(city.getCountry());
            detail.setPopulation(city.getPopulation());
            detail.setDistrict(city.getDistrict());
            return detail;
        }).toList();
    }

    private static SessionFactory preparedRelationDb() {
        Properties properties = new Properties();
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        //properties.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        //properties.put(Environment.URL, "jdbc:mysql://localhost:3306/world");
        properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/world");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "sakila");
        properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        properties.put(Environment.HBM2DDL_AUTO, "none");
        properties.put(Environment.STATEMENT_BATCH_SIZE, "100");
        return new Configuration().addAnnotatedClass(City.class).addAnnotatedClass(Country.class)
                .addAnnotatedClass(CountryLanguage.class).addProperties(properties)
                .buildSessionFactory();
    }

    @Test
    void testRedisPerformance() {
        long start = System.currentTimeMillis();

        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisStringCommands<String, String> stringCommands = connection.sync();
            for (Integer id : testIds) {
                String json = stringCommands.get("city: " + id);
                assertNotNull(json, "Данные для поиска города " + id + " не найдены в Redis");
                objectMapper.readValue(json, CityDetail.class);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        long duration = System.currentTimeMillis() - start;
        System.out.println("Redis test time: " + duration);
    }

    @Test
    void testMySqlPerformance() {
        long start = System.currentTimeMillis();

        try (Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            for (Integer id : testIds) {
                Optional<City> city = cityDAO.getById(id, session);
                assertNotNull(city, "Данные для поиска города " + id + " не найдены в MySql");

            }
        }

        long duration = System.currentTimeMillis() - start;
        System.out.println("MySql test time: " + duration);
    }

}