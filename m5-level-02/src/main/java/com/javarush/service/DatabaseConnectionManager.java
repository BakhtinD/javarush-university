package com.javarush.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConnectionManager {
    private String connection;

    public DatabaseConnectionManager() {
        System.out.println("1) Конструктор: объект создан, но connection null");
    }

    @PostConstruct
    public void init() {
        System.out.println("2) @PostConstruct: открываем соединение с БД");
        this.connection = "jdbc:mysql://localhost:3306/";
    }

    @PreDestroy
    public void destroy() {
        System.out.println("3) @PreDestroy: закрываем соединение");
        this.connection = null;
    }

}
