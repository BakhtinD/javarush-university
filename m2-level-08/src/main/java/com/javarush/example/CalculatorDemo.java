package com.javarush.example;

class Calculator {
    // Поле 1
    int startValue = 10;
    // Поле 2
    int bonus = startValue + 5;
    // Поле 3
    int total = startValue + bonus;

    // int error = mysteryValue * 2; // Cannot read value of field 'mysteryValue' before the field's definition

    int mysteryValue = 100;

    // метод класса
    void printValues() {
        System.out.println("startValue=" + startValue);
        System.out.println("bonus=" + bonus);
        System.out.println("total=" + total);
        System.out.println("mysteryValue=" + mysteryValue);
    }

}

public class CalculatorDemo {

    public static void main(String[] args) {
        System.out.println("Создаем объект Calculator:");
        Calculator calculator = new Calculator();
        calculator.printValues();

        System.out.println("Статическое поле staticX" + StaticClass.staticX);
        System.out.println("Статическое поле staticY" + StaticClass.staticY);

    }
}

class StaticClass {
    static int staticX = 100;
    static int staticY = staticX * 2;
    static int staticW = 50;
}