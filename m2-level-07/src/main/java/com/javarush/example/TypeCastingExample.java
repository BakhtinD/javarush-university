package com.javarush.example;


class Vehicle {
    void start() {
        System.out.println("Транспортное средство запускается!");
    }
}

class Car extends Vehicle {
    void start() {
        System.out.println("Автомобиль заводится с ключа");
    }

    void drive() {
        System.out.println("Автомобиль едет по дороге");
    }
}

class Bicycle extends Vehicle {
    void start() {
        System.out.println("Велосипед начинает движение с педалей");
    }
    void ringBell() {
        System.out.println("Дзинь-дзинь!");
    }
}

public class TypeCastingExample {

    public static void main(String[] args) {

        // 1. Upcasting (автоматически и безопасно)
        Car car = new Car();
        Vehicle vehicle1 = car; // Автоматический upcasting: Car -> Vehicle
        vehicle1.start();       // Вызывается переопределенный метод Car.start()

        // Vehicle car = new Car(); <- сокращенный вариант вызова
        // vehicle1.drive();  // метод drive() не доступен через Vehicle

        // 2. Downcasting
        Vehicle vehicle2 = new Car(); // Upcasting при создании

        // Безопасный downcasting с проверкой instanceof
        if (vehicle2 instanceof Car) {
            Car car2 = (Car) vehicle2; // Явное приведение типа
            car2.start();
            car2.drive(); // Теперь метод доступен!
        }

        // 3. Опасный downcasting без проверки
        Vehicle vehicle3 = new Bicycle();

        // Неправильно приведет к ошибке в рантайме
        // Car car3 = (Car) vehicle3; // ClassCastException

        // Правильно - с проверкой
        if (vehicle3 instanceof Car) {
            Car car3 = (Car) vehicle3;
            car3.drive();
        } else {
            System.out.println("vehicle3 не является Car, нельзя привести!");
        }

        // 4. Downcastring к правильному типу
        if (vehicle3 instanceof Bicycle) {
            Bicycle bicycle = (Bicycle) vehicle3;
            bicycle.start();
            bicycle.ringBell(); // Специфичный метод для Bicycle
        }

    }

}
