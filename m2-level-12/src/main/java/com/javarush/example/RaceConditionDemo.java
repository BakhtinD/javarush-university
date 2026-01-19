package com.javarush.example;

public class RaceConditionDemo {

    static int count = 0;

    public static void main(String[] args) throws InterruptedException {

        // (1) Что запускаем
        Runnable task = () -> {
            for (int i = 0; i < 10000; i++) {
                count++; // 1) прочитать из памяти 2) изменить 3) записать в память
            }
        };

        // (2) Где запускаем
        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);

        // (3) Запуск
        t1.start();
        t2.start();

        // (4) За-join-ить
        t1.join();
        t2.join();

        System.out.println("Ожидаем 20000");
        System.out.println("Получили " + count);
        System.out.println("Потери " + (20000 - count));

    }

}
