package com.javarush.example;

import java.util.ArrayList;
import java.util.List;

public class InstanceofExample {

    public static void main(String[] args) {

        // Создадим массив объектов разного типа
        Object[] objects = {
                "Я строка",
                42,   // auto-boxed to Integer
                3.14, // auto-boxed to Double
                new ArrayList<>(),
                null
        };

        // Пройдем по массиву и проверим тип каждого объекта
        for (Object object : objects) {

            System.out.println("\nObject is: " + object);

            // Проверка - является ли объект строкой?
            boolean isString = object instanceof String;
            System.out.println(" instanceof String? -> " + isString);

            // Проверка - является ли объект числом?
            boolean isInteger = object instanceof Integer;
            System.out.println(" instanceof Integer? -> " + isInteger);

            // Проверяем - является ли объект списком?
            boolean isList = object instanceof List;
            System.out.println(" instanceof List? -> " + isList);

            // Проверяем - является ли объект Double?
            boolean isDouble = object instanceof Double;
            System.out.println(" instanceof Double? -> " + isDouble);

            // instanceof c null всегда возвращает false
            if (object == null) {
                System.out.println(" Это null, instanceof всегда false");
            }

        }


    }

}
