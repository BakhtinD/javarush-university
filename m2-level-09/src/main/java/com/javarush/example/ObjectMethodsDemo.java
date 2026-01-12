package com.javarush.example;

import java.text.Annotation;

public class ObjectMethodsDemo {

    public static void main(String[] args) {

        // Создадим простой объект
        Object object1 = new Object();
        Object object2 = new Object();

        // Вызовем несколько методов классов Object
        System.out.println("1. toString(): " + object1.toString());
        System.out.println("2. object1.hashCode(): " + object1.hashCode());
        System.out.println("3. object2.hashCode(): " + object2.hashCode());
        System.out.println("4. getClass(): " + object1.getClass());
        System.out.println("5. equals(): " + object1.equals(object2));

        // Сравним ссылки
        System.out.println("5. Сравнение ссылок (==): " + (object1 == object2));
    }

}
