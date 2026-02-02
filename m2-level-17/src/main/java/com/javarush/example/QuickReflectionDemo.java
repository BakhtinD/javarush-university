package com.javarush.example;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class QuickReflectionDemo {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Class<?> stringClass = String.class;

        // Что можно узнать о классе через рефлексию?
        System.out.println("Поля: " + stringClass.getDeclaredFields().length);
        System.out.println("Методы: " + stringClass.getDeclaredMethods().length);
        System.out.println("Конструкторы: " + stringClass.getConstructors().length);

        // Создание String через рефлексию
        Constructor<?> constructor = stringClass.getConstructor(String.class);

        // new String("Hello, Java!");
        String str = (String) constructor.newInstance("Hello, Java!");

        System.out.println("Создана срока: " + str);

    }

}
