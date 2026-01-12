package com.javarush.example;

public class MutableImmutableExample {

    // Мутабельный класс - тот который изменяется
    static class MutablePerson {
        String name;
        MutablePerson(String name) {
            this.name = name;
        }
        void setName(String newName) {
            this.name = newName;
        }
    }

    // Иммутабельный класс - тот который НЕ изменяется!
    static final class ImmutablePerson { // final на классе! (1)
        private final String name; // private final на полях! (2)
        ImmutablePerson(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

        // public void setName(String newName) {
        //    this.name = newName; // java: cannot assign a value to final variable name
        // }
    }

    public static void main(String[] args) {
        // Мутабельный объект
        MutablePerson mutablePerson = new MutablePerson("Иван"); // До: Иван
        System.out.println("До: " + mutablePerson.name);
        mutablePerson.setName("Семен");
        System.out.println("После: " + mutablePerson.name); // После: Семен

        // Иммутабельный объект
        String str = "hello";
        String upper = str.toUpperCase();
        System.out.println("До: " + str);
        System.out.println("После: " + upper);

        ImmutablePerson immutablePerson = new ImmutablePerson("Петр");
        // immutablePerson.setName("Семен"); нет сеттера
        System.out.println("immutablePerson: " + immutablePerson.name);

    }

}
