package com.javarush.example;

public class Car {
    private String model;
    private Engine engine;

    public Car(String model) {
        this.model = model;
        this.engine = this.new Engine(200);
    }


    // Внутренний класс
    class Engine {
        private int horsepower;

        public Engine(int horsepower) {
            this.horsepower = horsepower;
        }

        public void start() {
            System.out.println("Запуск двигателя " + horsepower + " л.с. для машины " + model);
        }

        public void displayInfo() {
            System.out.println("Машина " + model + ", двигатель " + horsepower + " л.с.");
        }

    }

    // Метод внешнего класса, использующий внутренний
    public void drive() {
        engine.start();;
        System.out.println("Машина " + model + " поехала!");
    }

    // Метод для создания нового экз внутреннего класса снаружи
    public Engine createNewEngine(int hp) {
        return this.new Engine(hp);
    }

    public static void main(String[] args) {
        Car myCar = new Car("Toyota Camry");
        myCar.drive();

        Car.Engine extraEngine = myCar.createNewEngine(250);
        extraEngine.displayInfo();

        Car anotherCar = new Car("BMW X5");
        Car.Engine bmwEngine = anotherCar.new Engine(300);
        bmwEngine.start();

        // Engine engine2 = new Engine(100); 'com.javarush.example.Car.this' cannot be referenced from a static context

        bmwEngine.displayInfo();
    }

}

