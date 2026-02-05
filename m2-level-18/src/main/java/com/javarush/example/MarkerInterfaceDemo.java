package com.javarush.example;

import java.io.Serializable;

// 1) Маркерный интерфейс #1
interface Loggable {
    // нет методов - только маркер
}

// 2) Маркерный интерфейс #2
interface Cacheable {

}

// 3) Класс отмеченный маркерными интерфейсами
class User implements Serializable, Cloneable, Loggable {
    private String name;
    private int age;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

}

public class MarkerInterfaceDemo {
    public static void main(String[] args) {
        User user = new User("Иван", 30);

        // Проверка маркерных интерфейсов
        if (user instanceof Serializable) {
            System.out.println(" Класс реализует Serializable");
            // Выполнение кода, который будет предназначен для всех классов, реализующих интерфейс Serializable
        }

        if (user instanceof Cloneable) {
            System.out.println(" Класс реализует Cloneable");
        }

        if (user instanceof Loggable) {
            System.out.println(" Класс реализует Loggable");
        }
    }

}
