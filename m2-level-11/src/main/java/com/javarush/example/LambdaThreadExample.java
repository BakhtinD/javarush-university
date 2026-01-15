package com.javarush.example;

public class LambdaThreadExample {

    public static void main(String[] args) {
        // Все в одном месте
        Thread thread = new Thread(() -> {
            // Здесь пишется логика выполнения в потоке
            System.out.println("Выполняюсь в отдельном потоке");
        });

        thread.start();

    }

}
