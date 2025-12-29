package com.javarush.example;

// Иерархия классов
class Animal {}

class Dog extends Animal implements Barkable {
    @Override
    public void bark() {
        System.out.println("Гав!");
    }
}

class Cat extends Animal {}

interface Barkable {
    void bark();
}

public class InstanceofInheritanceExample {

    public static void main(String[] args) {

        Animal myPet1 = new Dog(); // Ссылка типа Animal на объект Dog
        Animal myPet2 = new Cat(); // Ссылка типа Animal на объект Cat

        // Массив животных для перебора
        Animal[] pets = {myPet1, myPet2};

        for (Animal pet: pets) {
            System.out.println("\nТекущий питомец: " + pet.getClass().getSimpleName());

            // 1. Проверка на точный тип
            System.out.println(" instanceof Dog? -> " + (pet instanceof Dog));

            // 2. Проверка на родительский тип (наследование)
            System.out.println(" instanceof Animal? -> " + (pet instanceof Animal));
            System.out.println(" instanceof Object? -> " + (pet instanceof Object));

            // 3. Проверка на интерфейс
            System.out.println(" instanceof Barkable? -> " + (pet instanceof Barkable));

            // Выполняем безопасное приведение и вызов метода
            if (pet instanceof Barkable) {
                System.out.println(" Этот питомец умеет лаять!");

                // Первый вариант
                Barkable barkingPet = (Barkable) pet; // безопасное приведение
                barkingPet.bark(); // вызов метода интерфейса

                // Второй вариант - через анонимность
                // ((Barkable) pet).bark();

            }
        }

        // Что вернет instanceof для null?
        Animal unknown = null;
        System.out.println("\nnull instanceof Animal? -> " + (unknown instanceof Animal));

    }

}
