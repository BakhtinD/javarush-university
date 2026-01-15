package com.javarush.example;

public class MultithreadingBasics {

    public static void main(String[] args) throws InterruptedException { // Главный поток (1)
        // Создаем два потока
        Thread thread1 = new Thread(new Task("Поток 1", 5)); // (2)
        Thread thread2 = new Thread(new Task("Поток 2", 5)); // (3)

        System.out.println("Запускаем потоки...");
        thread1.start();
        thread2.start();

        // Главный поток тоже работает
        System.out.println("Главный поток продолжает работу");

        // Ждем завершения потоков
        thread1.join(); // ожидание завершения главного потока
        thread2.join(); //

        // Анализ
        System.out.println("Количество ядер процессора: " + Runtime.getRuntime().availableProcessors());

        System.out.println("Работа Главного потока завершена");

        // Вариант 1: Шаг 3) Создать экземпляр класса и вызвать метод start()
        ThreadExt threadExt = new ThreadExt();
        threadExt.start();

    }

    // Вариант 1: Шаг 1) Наследование от класса Thread
    static class ThreadExt extends Thread  {

        // Вариант 1: Шаг 2) Переопределение метода run()
        @Override
        public void run() {
            System.out.println("Какие-то действия в потоке...");
        }

    }

    // Вариант 2: 1) Имплементировать интерфейс Runnable
    static class Task implements Runnable {
        private String name;
        private int count;

        Task(String name, int count) {
            this.name = name;
            this.count = count;
        }

        // Вариант 2: Шаг 2) Переопределение метода run()
        @Override
        public void run() {
            for (int i = 1; i <= count; i++) {
                System.out.println(name + ": шаг " + i);
                try {
                    // Имитация процесса
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}

