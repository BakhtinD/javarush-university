package com.javarush.example;

public class SyncBlockExample {

    private String name1 = "Аня";
    private String name2 = "Ваня";

    // synchronized блок в методе
    public void swap() {
        // Здесь может быть логика, которая выполняется многопоточно
        // ...

        // Выполнение одним потоком
        synchronized (this) {
            String temp = name1;
            name1 = name2;
            name2 = temp;
        }
    }

    // synchronized метод
    public synchronized void swapMethod() {
        String temp = name1;
        name1 = name2;
        name2 = temp;
    }

    public static void main(String[] args) throws InterruptedException {
        SyncBlockExample demo = new SyncBlockExample();
        System.out.println("Начало: " + demo.name1 + ", " + demo.name2);

        // Два потока меняют имена местами 500 раз
        Runnable task = () -> {
            for (int i = 0; i < 500; i++) {
                demo.swap(); // synchronized-блок
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("После 1000 обменов: " + demo.name1 + ", " + demo.name2);
        System.out.println("Ожидаем: Аня, Ваня"); // Ожидаем: Аня, Ваня

        // synchronized метод работает также
        demo.name1 = "Аня";
        demo.name2 = "Ваня";

        Thread t3 = new Thread(() -> {
            for (int i = 0; i < 500; i++) demo.swapMethod();
        });

        Thread t4 = new Thread(() -> {
            for (int i = 0; i < 500; i++) demo.swapMethod();
        });

        t3.start();
        t4.start();

        t3.join();
        t4.join();

        System.out.println("C synchronized метод: " + demo.name1 + ", " + demo.name2); // C synchronized метод: Аня, Ваня
    }

}
