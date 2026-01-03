package com.javarush.example;

import java.time.DayOfWeek;

public class StringValidator {

    // Проверяет, что строка содержит хотя бы одну цифру
    public boolean containsDigit(String text) {
        if (text == null) return false;
        return text.matches(".*\\d.*");
    }

    // Проверяет длину строки
    public boolean isValidLength(String text, int min, int max) {
        if (text == null) return false;
        return text.length() >= min && text.length() <= max;
    }

    // Конвертирует день недели в русское название
    public String dayToRussian(DayOfWeek day) {
        return switch (day) {
            case MONDAY -> "Понедельник";
            case TUESDAY -> "Вторник";
            case WEDNESDAY -> "Среда";
            case THURSDAY -> "Четверг";
            case FRIDAY -> "Пятница";
            case SATURDAY -> "Суббота";
            case SUNDAY -> "Воскресенье";
        };
    }
}
