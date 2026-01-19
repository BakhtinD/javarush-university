package com.javarush.example;

public class SynchronizedDemo {

    private int counter = 0;

    private Object obj = new Object();

    // Без синхронизации
    public void increment() {
        //synchronized (obj) {
            counter++;
        //}
    }

    // С Синхронизацией - на уровне метода - может заходить только один поток. Блокировка на this
    public synchronized void incrementSync() {
        counter++;
    }

    public static void main(String[] args) throws InterruptedException {
        SynchronizedDemo demo = new SynchronizedDemo();

        // Создаем задачу, которая увел. счетчик на 1000 раз
        Runnable task = () -> {
            for (int i = 0; i < 10000; i++) {
                demo.increment();
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Без синхронизации " + demo.counter);

        // Теперь с синхронизацией
        demo.counter = 0;

        Runnable safeTask = () -> {
            for (int i = 0; i < 1000; i++) {
                demo.incrementSync();
            }
        };

        Thread t3 = new Thread(safeTask);
        Thread t4 = new Thread(safeTask);

        t3.start();
        t4.start();

        t3.join();;
        t4.join();

        System.out.println("C синхронизацией " + demo.counter);
    }

}
