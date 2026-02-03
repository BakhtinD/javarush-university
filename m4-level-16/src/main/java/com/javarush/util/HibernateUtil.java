package com.javarush.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.HashMap;
import java.util.Map;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Создаем карту настроек
                Map<String, String> settings = new HashMap<>();

                // Database settings
                settings.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
                settings.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
                settings.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/sakila");
                settings.put("hibernate.connection.username", "root");
                settings.put("hibernate.connection.password", "sakila");

                // Connection pool settings
                settings.put("hibernate.connection.pool_size", "10");
                settings.put("hibernate.connection.characterEncoding", "utf8");
                settings.put("hibernate.connection.useUnicode", "true");

                // DDL settings
                settings.put("hibernate.hbm2ddl.auto", "create-drop");

                // Performance settings
                settings.put("hibernate.jdbc.batch_size", "20");
                settings.put("hibernate.order_inserts", "true");
                settings.put("hibernate.order_updates", "true");
                settings.put("hibernate.jdbc.fetch_size", "50");

                // Logging settings
                settings.put("hibernate.show_sql", "true");
                settings.put("hibernate.format_sql", "true");
                settings.put("hibernate.use_sql_comments", "true");

                // Transaction settings
                settings.put("hibernate.connection.autocommit", "false");
                settings.put("hibernate.transaction.coordinator_class", "jdbc");

                StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(settings)
                        .build();

                MetadataSources metadataSources = new MetadataSources(serviceRegistry);

                // Регистрируем сущности
                metadataSources.addAnnotatedClass(com.javarush.entity.slide3.Customer.class);
                metadataSources.addAnnotatedClass(com.javarush.entity.slide4.Product.class);
                metadataSources.addAnnotatedClass(com.javarush.entity.slide5.Employee.class);
                metadataSources.addAnnotatedClass(com.javarush.entity.slide6.Developer.class);

                Metadata metadata = metadataSources.buildMetadata();
                sessionFactory = metadata.buildSessionFactory();

                System.out.println("Hibernate сконфигурирован через Map настроек!");

            } catch (Exception e) {
                e.printStackTrace();
                throw new ExceptionInInitializerError(e);
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            System.out.println("SessionFactory закрыт");
        }
    }
}