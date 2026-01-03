package com.javarush.example;

public class ManualTestingDemo {
    public static void main(String[] args) {
        PasswordValidator validator = new PasswordValidator();

        System.out.println("=== РУЧНОЕ ТЕСТИРОВАНИЕ ===");
        System.out.println("Тест 1: 'pass123' -> " + validator.isValid("pass123"));
        System.out.println("Тест 2: 'password' -> " + validator.isValid("password"));
        System.out.println("Тест 3: '12345678' -> " + validator.isValid("12345678"));
        System.out.println("Тест 4: null -> " + validator.isValid(null));

        // Проблемы: нужно запускать вручную,
        // легко пропустить тест, нет истории результатов
    }
}