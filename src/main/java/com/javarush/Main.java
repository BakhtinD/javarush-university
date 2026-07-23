package com.javarush;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javarush.dao.CityDAO;
import com.javarush.dao.CountryDAO;
import com.javarush.domain.City;
import com.javarush.domain.Country;
import com.javarush.domain.CountryLanguage;
import com.javarush.redis.CityCountry;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.util.*;

/**
 * JavaRush-University
 */
public class Main {

    private final SessionFactory sessionFactory;
    private final CountryDAO countryDAO;
    private final CityDAO cityDAO;
    private final RedisClient redisClient;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public Main() {
        sessionFactory = prepareSessionFactory();
        cityDAO = new CityDAO(sessionFactory);
        countryDAO = new CountryDAO(sessionFactory);
        redisClient = createRedisClient();
    }

    public static void main(String[] args) {
        Main main = new Main();
        List<City> cities = main.fetchData();
        System.out.println("total cities: " + cities.size());
        List<CityCountry> details = main.transformData(cities);
        main.pushToRedis(details);

        org.hibernate.stat.Statistics stats = main.sessionFactory.getStatistics();
        System.out.println("PrepareStatement count: " + stats.getPrepareStatementCount());
        System.out.println("Query execution count: " + stats.getQueryExecutionCount());
        System.out.println("Entity fetch count: " + stats.getEntityFetchCount());
    }

    private RedisClient createRedisClient() {

        RedisClient client = RedisClient.create(RedisURI.create("localhost", 6379));
        try (StatefulRedisConnection<String, String> connection = client.connect()) {
            System.out.println("Connected to Redis");
        }
        return client;

    }

    private List<CityCountry> transformData(List<City> countries) {
        return countries.stream().map(city -> {
            CityCountry detail = new CityCountry();
            Country country = city.getCountry();
            detail.setId(country.getId());
            detail.setCode(country.getCode());
            detail.setAlternativeCode(country.getAlternativeCode());
            detail.setName(country.getName());
            detail.setContinent(country.getContinent());
            detail.setRegion(country.getRegion());
            detail.setSurfaceArea(country.getSurfaceArea());
            detail.setPopulation(country.getPopulation());
            detail.setLocalName(country.getLocalName());
            detail.setGovernmentForm(country.getGovernmentForm());

            Set<CountryLanguage> languages = new HashSet<>(country.getLanguages());
            detail.setLanguages(languages);
            detail.setIndependenceYear(country.getIndependenceYear());
            detail.setLifeExpectancy(country.getLifeExpectancy());
            detail.setGnp(country.getGnp());
            detail.setHeadOfState(country.getHeadOfState());
            detail.setCapital(country.getCapital().getName());
            return detail;
        }).toList();
    }

    private void pushToRedis(List<CityCountry> cityCountries) {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisStringCommands<String, String> stringCommands = connection.sync();
            for (CityCountry detail : cityCountries) {
                String key = "country: " + detail.getCode();
                String value = objectMapper.writeValueAsString(detail);
                stringCommands.set(key, value);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private List<City> fetchData() {
        try (Session session = this.sessionFactory.getCurrentSession()) {
            List<City> allCities = new ArrayList<>();
            session.beginTransaction();

            int totalCount = this.cityDAO.getTotalCount();
            //int step = 500; заменил на batch
            //for (int i = 0; i < totalCount; i += step) {
            allCities.addAll(this.cityDAO.getItems(0, totalCount));
            //}
            session.getTransaction().commit();
            return allCities;
        }
    }

    private SessionFactory prepareSessionFactory() {
        Properties properties = new Properties();
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver"); //properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        properties.put(Environment.URL, "jdbc:mysql://localhost:3306/world"); //properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/world");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "sakila");
        properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        properties.put(Environment.HBM2DDL_AUTO, "validate");
        properties.put(Environment.STATEMENT_BATCH_SIZE, "100");
        properties.put("hibernate.generate_statistics", "true");

        SessionFactory sessionFactory = new Configuration().addAnnotatedClass(City.class).addAnnotatedClass(Country.class).addAnnotatedClass(CountryLanguage.class).addProperties(properties).buildSessionFactory();
        return sessionFactory;
    }

    //todo недоделано
//    private void testRedisData(List<Integer> ids) {
//        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
//            RedisStringCommands<String, String> stringCommands = connection.sync();
//            for (Integer id : ids) {
//                // Получить из Redis
//                String json = stringCommands.get("film:" + id);
//                objectMapper.readValue(json, FilmDetail.class);
//            }
//        } catch (JsonProcessingException e ) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    //todo недоделано
//    private void testMySqlData(List<Integer> ids) {
//        try (Session session = sessionFactory.getCurrentSession()) {
//            session.beginTransaction();
//            for (Integer id : ids) {
//                Optional<Film> film = filmDao.getById(id);
//                film.get().getActors().size();  // lazy load
//                film.get().getCategories().size();
//            }
//            session.getTransaction().commit();
//        }
//    }


    private void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }


}
