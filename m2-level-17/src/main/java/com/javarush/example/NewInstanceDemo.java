package com.javarush.example;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

class Employee {
    private String name;

    public Employee() {
        this.name = "Безымянный";
        System.out.println("Создание через конструктор по умолчанию");
    }

    public Employee(String name) {
        this.name = name;
        System.out.println("Создан " + name);
    }

    public String getName() {
        return name;
    }

}

public class NewInstanceDemo {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // Исп конструктора без параметров
        Constructor<Employee> constructor = Employee.class.getConstructor();
        Employee employee = constructor.newInstance();
        System.out.println(employee.getName());

        // Исп конструктор с параметрами
        Constructor<Employee> constructor2 = Employee.class.getConstructor(String.class);
        Employee employee2 = constructor2.newInstance("Анна");
        System.out.println(employee2.getName());

    }

}
