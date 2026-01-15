package com.javarush.example;

public class SimpleThreadStop {

    static class Worker implements Runnable {

        private volatile boolean running = true; // стандартный паттерн с volatile

        public void stop() {
            running = false;
        }

        @Override
        public void run() {
            while (running) {
                System.out.println("Работаю...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("Прервано!");
                    return;
                }
            }
            System.out.println("Завершил работу.");
        }
    }

    public static void main(String[] args) throws InterruptedException { // поток main
        Worker worker = new Worker();
        Thread thread = new Thread(worker);

        thread.start();

        // 3 сек
        Thread.sleep(3000);


        // Thread.stop(); - остановку через Thread.stop() делать нельзя!

        // Останавливаем через флаг
        worker.stop();
        thread.join();

        System.out.println("Поток остановлен корректно");
    }

}
