package com.javarush.example;

public class MathUtils {

    // Статический вложенный класс-помощник
    public static class Calculator {
        public static int add(int a, int b) {
            return a + b;
        }

        public static int multiplay(int a, int b) {
            return a * b;
        }
    }

    public static void main(String[] args) {
        int sum = MathUtils.Calculator.add(5, 3);
        int product = MathUtils.Calculator.multiplay(5, 3);
        System.out.println("Сумма: " + sum + ", Произведение: " + product);
    }
}
