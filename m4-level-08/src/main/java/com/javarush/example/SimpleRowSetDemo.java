package com.javarush.example;

import javax.sql.rowset.*;

public class SimpleRowSetDemo {
    public static void main(String[] args) throws Exception {
        System.out.println("=== ПРОСТАЯ ДЕМОНСТРАЦИЯ ROWSET ===\n");

        // 1. Создаем CachedRowSet
        CachedRowSet rowset = RowSetProvider.newFactory().createCachedRowSet();

        // 2. Настраиваем подключение
        rowset.setUrl("jdbc:mysql://localhost:3306/sakila");
        rowset.setUsername("root");
        rowset.setPassword("sakila");

        // 3. Загружаем данные
        rowset.setCommand("SELECT first_name, last_name FROM actor LIMIT 3");
        rowset.execute();

        // 4. Работаем с данными (соединение уже можно закрыть!)
        System.out.println("Актеры из кэша:");
        while (rowset.next()) {
            System.out.println("  " + rowset.getString(1) + " " + rowset.getString(2));
        }

        // 5. Прокрутка назад
        rowset.beforeFirst();
        System.out.println("\nПервая запись:");
        if (rowset.next()) {
            System.out.println("  " + rowset.getString("first_name"));
        }
    }
}