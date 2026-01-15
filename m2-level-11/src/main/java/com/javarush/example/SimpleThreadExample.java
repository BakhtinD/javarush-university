package com.javarush.example;

public class SimpleThreadExample {

    // Класс реализующий Runnable
    static class Printer implements Runnable {

        @Override
        public void run() {
            System.out.println("Принтер выполняется в потоке " + Thread.currentThread().getName());
        }
    }

    public static void main(String[] args) {
        System.out.println("Главный поток: " + Thread.currentThread().getName()); // main

        // 1.
        Printer printer = new Printer();

        // 2.
        Thread childThread = new Thread(printer);

        // 3.
        childThread.start();

        System.out.println("Главный поток завершает работу"); // Thread-X

    }

}
