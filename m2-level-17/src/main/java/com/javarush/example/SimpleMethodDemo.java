package com.javarush.example;


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.SortedMap;

class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
    private void log() {
        System.out.println("Выполнено");
    }
}

public class SimpleMethodDemo {
    public static void main(String[] args) {
        Class<?> clazz = Calculator.class;

        for (Method method : clazz.getDeclaredMethods()) {
            System.out.println("Метод: " + method.getName());
            System.out.println(" Публичный? "
                    + Modifier.isPublic(method.getModifiers()));
            System.out.println(" Параметры: " + method.getParameterCount());
            System.out.println(" Возвращает: " + method.getReturnType().getSimpleName());

        }
    }
}
