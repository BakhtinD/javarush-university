package com.javarush.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ThreadFactoryDemo {

    public static void main(String[] args) {

        ThreadFactory customFactory = new ThreadFactory() {

            private int counter = 0;

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("Мой-Пул-Поток " + (++counter));
                thread.setPriority(Thread.MAX_PRIORITY);
                thread.setUncaughtExceptionHandler((t, e) -> {
                    System.out.println("Исключение в потоке " + t.getName() + ": " + e.getMessage());
                });
                return thread;
            }
        };

        ExecutorService executorService = Executors.newSingleThreadExecutor(customFactory);

        executorService.submit(() -> {
            System.out.println("Задача выполняется в потоке: " + Thread.currentThread().getName());
            System.out.println("Приоритет потока: " + Thread.currentThread().getPriority());
            throw new RuntimeException("Тестовое исключение!");
        });

        executorService.submit(() -> {
            System.out.println("Вторая задача в потоке: " + Thread.currentThread().getName());
        });

        executorService.shutdown();

    }

}
