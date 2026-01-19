package com.javarush.example;

public class DeadlockExample {

    //todo  Задание от ментора - исправить ощибку в этом примере, чтобы пример заработал

    private static final Object lock1 = new Object();
    private static final Object lock2 = new Object();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Создаем deadlock...");

        // Поток 1: lock1 -> lock2
        Thread t1 = new Thread(() -> {
           synchronized (lock1) {
               System.out.println("t1 взял lock1");
               try {
                   Thread.sleep(10);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }

               synchronized (lock2) {
                   System.out.println("t1 взял lock2");
               }
           }
        });

        // Поток 2: lock2 -> lock1 (Ошибка!)
        Thread t2 = new Thread(() -> {
            synchronized (lock2) {
                System.out.println("t2 взял lock2");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (lock1) {
                    System.out.println("t2 взял lock1");
                }

            }
        });

        t1.start();
        t2.start();

        // Ждем и проверяем deadlock
        Thread.sleep(2000);

        if (t1.isAlive() && t2.isAlive()) {
            System.out.println("Потоки зависли в deadlock");
        }

         // прерываение
         t1.interrupt();
         t2.interrupt();

    }

}
