package com.javarush.example;

public class VolatileExample {

    private static boolean flag = false;

    // ЗАПОМНИТЬ: volatile не делает операции атомарными!
    private static volatile boolean volatileFlag = false;

    public static void main(String[] args) throws InterruptedException {
        // Без volatile
        Thread t1 = new Thread(() -> {
            while (!flag && !Thread.currentThread().isInterrupted()) {}
            System.out.println("t1: " + (flag ? "увидел true" : "прерван"));
        });

        t1.start();
        Thread.sleep(1000);

        flag = true;
        t1.join(500);

        if (t1.isAlive()) {
            System.out.println("Без volatile изменения не видимы!");
            t1.interrupt();
            t1.join();
        }

        // c volatile
        Thread  t2 = new Thread(() -> {
            while (!volatileFlag) {}
            System.out.println("t2: увидел true");
        });

        t2.start();
        Thread.sleep(100);
        volatileFlag = true;
        t2.join();
        System.out.println("volatile гарантирует видимость!");

    }

}
