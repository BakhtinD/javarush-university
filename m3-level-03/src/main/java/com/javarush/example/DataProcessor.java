package com.javarush.example;

public class DataProcessor {

    // Медленная операция (имитация обработки данных)
    public String processData(String input) {
        try {
            Thread.sleep(100); // Имитация долгой обработки
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Входные данные не могут быть пустыми");
        }

        return "Обработано: " + input.toUpperCase();
    }

    // Валидация пользователя
    public Person validatePerson(String username, String email, int age) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя пользователя обязательно");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Некорректный email");
        }
        if (age < 18) {
            throw new IllegalArgumentException("Возраст должен быть 18+");
        }

        return new Person(username, email, age);
    }
}

record Person(String username, String email, int age) {}

