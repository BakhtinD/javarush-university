package com.javarush.example;

public class CloneExample {

    static class Address {
        String city;

        Address(String city) {
            this.city = city;
        }
    }

    // Чтобы использовать .clone() пометить класс как implements Cloneable - иначе CloneNotSupportedException
    static class Person implements Cloneable {
        String name;
        Address address;

        Person(String name, Address address) {
            this.name = name;
            this.address = address;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone(); // Поверхностное копирование
        }

    }


    public static void main(String[] args) throws CloneNotSupportedException {
        Address address = new Address("Вашингтон");
        Person original = new Person("Билл", address);
        
        Person clone = (Person) original.clone();

        // До изменения
        System.out.println("Оригинал: " + original.address.city);
        System.out.println("Клон: " + clone.address.city);

        // Меняем адрес через клон
        clone.address.city = "Токио";

        // После изменения
        System.out.println("Оригинал: " + original.address.city);
        System.out.println("Клон: " + clone.address.city);

    }
    
}
