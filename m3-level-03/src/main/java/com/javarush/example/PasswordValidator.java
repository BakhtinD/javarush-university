package com.javarush.example;



public class PasswordValidator {
    public boolean isValid(String password) {
        // Пароль должен быть: 8+ символов, хотя бы одна цифра
        if (password == null || password.length() < 8) {
            return false;
        }
        return password.matches(".*\\d.*"); // содержит цифру
    }
}