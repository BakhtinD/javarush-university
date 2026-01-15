package com.javarush.example;

public class SimpleInterrupt {

    static class Clock implements Runnable {

        @Override
        public void run() {
            Thread current = Thread.currentThread();
            while (!current.isInterrupted()) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("Прервано!");
                    current.interrupt(); // Восстанавливаем статус - isInterrupted
                }
                System.out.println("Тик");
            }
            System.out.println("Часы остановлены");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Clock clock = new Clock();
        Thread clockThread = new Thread(clock);

        clockThread.start();
        Thread.sleep(5000);

        clockThread.interrupt(); // просим остановиться (без гарантии)
        clockThread.join();
    }

}
