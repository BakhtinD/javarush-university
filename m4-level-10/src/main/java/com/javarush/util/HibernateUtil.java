package com.javarush.util;

import com.javarush.entity.Employee;
import com.javarush.entity.EmployeeTask;
import com.javarush.entity.Product;
import com.javarush.entity.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();

                // Загружаем properties из файла
                Properties settings = loadPropertiesFromFile();

                // Если файл не найден
                if (settings.isEmpty()) {
                    System.out.println("Файл hibernate.properties не найден!");
                    throw new ExceptionInInitializerError();
                }

                configuration.setProperties(settings);
                configuration.addAnnotatedClass(User.class);
                configuration.addAnnotatedClass(Employee.class);
                configuration.addAnnotatedClass(EmployeeTask.class);
                configuration.addAnnotatedClass(Product.class);

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties())
                        .build();

                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
                System.out.println("Hibernate сконфигурирован через файл properties!");

            } catch (Exception e) {
                e.printStackTrace();
                throw new ExceptionInInitializerError(e);
            }
        }
        return sessionFactory;
    }

    private static Properties loadPropertiesFromFile() {
        Properties properties = new Properties();
        try (InputStream input = HibernateUtil.class
                .getClassLoader()
                .getResourceAsStream("hibernate.properties")) {

            if (input != null) {
                properties.load(input);
                System.out.println("Загружены настройки из hibernate.properties");
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении hibernate.properties: " + e.getMessage());
        }
        return properties;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

}