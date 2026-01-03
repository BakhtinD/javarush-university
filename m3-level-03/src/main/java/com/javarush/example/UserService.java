package com.javarush.example;

public class UserService {
    public User register(String username, String email, int age) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым");
        }
        if (age < 18) {
            throw new IllegalArgumentException("Возраст должен быть 18+");
        }

        return new User(username, email, age);
    }

    public boolean isAdult(int age) {
        return age >= 18;
    }

    public String createGreeting(String name) {
        if (name == null) return "Привет, гость!";
        return "Привет, " + name + "!";
    }
}

record User(String username, String email, int age) {}
