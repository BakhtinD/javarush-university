package com.javarush.example;

// 1. Создадим базовый родительский класс
class Animal {
    // Поле с инициализацией при объявлении (Шаг 3)
    private String type = setTypeField();

    // Конструктор родительского (Шаг 4)
    public Animal() {
        System.out.println(" Вызван конструктор Animal. Поле type = " + type);
    }

    // Метод для демонстрации порядка
    private String setTypeField() {
        System.out.println(" (Animal) Присваивание значения полю type");
        return "Животное";
    }
}

// 2. Создадим производный класс
class Cat extends Animal {
    // Поле с инициализацией при объявлении (Шаг 3)
    private String name = setNameField();
    private int age;

    private int weight = 5;

    // Конструктор дочернего класса (Шаг 4)
    public Cat(String catName, int catAge) {
        // super() вызывается неявно, если его нет
        this.age = catAge; // доп действие в конструкторе
        System.out.println(" Вызван конструктор Cat. Поля: name = " + name + ", age=" + age);
    }

    // Метод для демонстрации порядка
    private String setNameField() {
        System.out.println(" (Cat) Присваивание значения полю name");
        return "Безымянный";
    }
}

public class Example {

    public static void main(String[] args) {
        System.out.println("Начало создания объекта Cat:");
        Cat myCat = new Cat("Барсик", 3);
        System.out.println("Объект Cat создан.");
    }

}
