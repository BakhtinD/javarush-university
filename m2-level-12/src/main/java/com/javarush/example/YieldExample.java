package com.javarush.example;

public class YieldExample {

    public static void main(String[] args) throws InterruptedException {

        // Без Yield
        System.out.println("--- без yield ---");

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 300; i++) {
                System.out.println("Поток 1");
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 300; i++) {
                System.out.println("Поток 2");
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        // c yield
        System.out.println("--- c yield ---");
        Thread t3 = new Thread(() -> {
            for (int i = 0; i < 300; i++) {
                System.out.println("Поток 3");
                Thread.yield(); // уступаем CPU
            }
        });

        Thread t4 = new Thread(() -> {
            for (int i = 0; i < 300; i++) {
                System.out.println("Поток 4");
                Thread.yield(); // уступаем CPU
            }
        });

        t3.start();
        t4.start();
        t3.join();
        t4.join();

    }

}
