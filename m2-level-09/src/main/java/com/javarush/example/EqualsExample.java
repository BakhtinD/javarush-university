package com.javarush.example;

import java.util.Objects;

public class EqualsExample {

    static class Person {
        String name;
        int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public boolean equals(Object obj) {
            // Формальные признаки
            if (this == obj) return true;
            if (obj == null) return false;
            if (this.getClass() != obj.getClass()) return false;
            // Сравнение объектов по состоянию
            Person person = (Person) obj; // Преобразованный экземпляр из конейнера Object
            // return age == person.age && Objects.equals(name, person.name); // Вариант 1
            return age == person.age && this.name.equals(person.name); // Вариант 2
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, age);
        }

    }

    public static void main(String[] args) {
        Person p1 = new Person("Иван", 30);
        Person p2 = new Person("Иван", 30);
        Person p3 = new Person("Мария", 25);

        System.out.println("p1 == p2: " + (p1 == p2));         // false
        System.out.println("p1.equals(p2): " + p1.equals(p2)); // true
        System.out.println("p1.equals(p3): " + p1.equals(p3)); // false

        System.out.println("p1.hashCode(): " + p1.hashCode()); // 1000910360
        System.out.println("p2.hashCode(): " + p2.hashCode()); // 1000910360
        System.out.println("p3.hashCode(): " + p3.hashCode()); // 1076595959

    }

}
