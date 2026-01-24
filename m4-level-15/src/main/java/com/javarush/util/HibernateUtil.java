package com.javarush.util;

import com.javarush.entity.Product;
import com.javarush.entity.User;
import com.javarush.entity.discriminator.Contract;
import com.javarush.entity.discriminator.Document;
import com.javarush.entity.discriminator.Invoice;
import com.javarush.entity.discriminator.Report;
import com.javarush.entity.singletable.Admin;
import com.javarush.entity.singletable.Employee;
import com.javarush.entity.singletable.Person;
import com.javarush.entity.singletable.RegularUser;
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
                metadataSources.addAnnotatedClass(User.class);
                metadataSources.addAnnotatedClass(Product.class);

                metadataSources.addAnnotatedClass(Person.class);
                metadataSources.addAnnotatedClass(RegularUser.class);
                metadataSources.addAnnotatedClass(Employee.class);
                metadataSources.addAnnotatedClass(Admin.class);

                metadataSources.addAnnotatedClass(Contract.class);
                metadataSources.addAnnotatedClass(Document.class);
                metadataSources.addAnnotatedClass(Invoice.class);
                metadataSources.addAnnotatedClass(Report.class);

                metadataSources.addAnnotatedClass(com.javarush.entity.joinedtablestrategy.Account.class);
                metadataSources.addAnnotatedClass(com.javarush.entity.joinedtablestrategy.SavingsAccount.class);
                metadataSources.addAnnotatedClass(com.javarush.entity.joinedtablestrategy.CheckingAccount.class);
                metadataSources.addAnnotatedClass(com.javarush.entity.joinedtablestrategy.BusinessAccount.class);

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