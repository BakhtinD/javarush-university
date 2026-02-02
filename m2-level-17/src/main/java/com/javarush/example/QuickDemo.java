package com.javarush.example;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class Simple {
    private String secret = "секрет";
    public void show() {
        System.out.println("secret: " + secret);
    }
}

public class QuickDemo {

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException,
            NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        // 1) Узнать класс
        Object object = new Simple();
        Class<?> clazz = object.getClass();

        // 2) Структура
        System.out.println("Поля: " +clazz.getDeclaredFields().length);

        // 3) Интерфейсы
        System.out.println("Интерфейсы: " + clazz.getAnnotatedInterfaces().length);

        // 4) Динамическое создание
        Object newObj = Class.forName("com.javarush.example.Simple").newInstance();

        // 5) Работа с полем
        Field field = clazz.getDeclaredField("secret");
        field.setAccessible(true); // public String secret = "секрет";
        field.set(newObj, "новый секрет");

        // 6) Вызов метода
        Method method = clazz.getMethod("show");
        method.invoke(newObj);

        // Вернуть назад
        field.setAccessible(false); // private String secret = "секрет";
    }

}
