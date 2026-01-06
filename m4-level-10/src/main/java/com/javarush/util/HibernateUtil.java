package com.javarush.util;

import com.javarush.entity.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Создаем объект Configuration
                Configuration configuration = new Configuration();

                // Создаем Properties и настраиваем их
                Properties settings = new Properties();

                // 1. Настройки подключения к БД
                settings.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
                settings.put(Environment.URL, "jdbc:mysql://localhost:3306/sakila");
                settings.put(Environment.USER, "root");
                settings.put(Environment.PASS, "sakila");

                // 2. Диалект
                settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");

                // 3. Настройки пула соединений
                settings.put(Environment.C3P0_MIN_SIZE, "5");
                settings.put(Environment.C3P0_MAX_SIZE, "20");
                settings.put(Environment.C3P0_TIMEOUT, "300");
                settings.put(Environment.C3P0_MAX_STATEMENTS, "50");
                settings.put(Environment.C3P0_IDLE_TEST_PERIOD, "3000");

                // 4. Логирование SQL
                settings.put(Environment.SHOW_SQL, "true");
                settings.put(Environment.FORMAT_SQL, "true");

                // 5. Автоматическое управление схемой
                settings.put(Environment.HBM2DDL_AUTO, "create-drop");

                // 6. Производительность
                settings.put(Environment.USE_SECOND_LEVEL_CACHE, "false");
                settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");

                // Применяем настройки
                configuration.setProperties(settings);

                // Регистрируем Entity-классы (Слайд 13, способ 2)
                configuration.addAnnotatedClass(User.class);

                // Создаем ServiceRegistry
                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties())
                        .build();

                // Создаем SessionFactory
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);

                System.out.println("Hibernate сконфигурирован через Properties!");

            } catch (Exception e) {
                System.err.println("Ошибка при создании SessionFactory:");
                e.printStackTrace();
                throw new ExceptionInInitializerError(e);
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            System.out.println("SessionFactory закрыт.");
        }
    }
}