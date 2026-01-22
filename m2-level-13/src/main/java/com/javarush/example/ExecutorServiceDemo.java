package com.javarush.example;

import java.util.concurrent.*;

public class ExecutorServiceDemo {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        // 1. Создаем пул из 2 потоков
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // 2. Запускаем Runnable
        System.out.println("1. Runnable задачи:");
        for (int i = 0; i < 3; i++) {
            int taskId = i;
            executorService.execute(() -> {
                System.out.println(" Задача " + taskId + " в потоке " + Thread.currentThread().getName());
            });
        }

        Thread.sleep(100);

        // 3. Запустим Callable задачи с результатов
        System.out.println("2. Запускаем Callable");
        Future<Integer> future1 = executorService.submit(() -> {
            Thread.sleep(1000);
            return 10;
        });

        Future<Integer> future2 = executorService.submit(() -> {
           Thread.sleep(500);
           return 20;
        });

        // Получаем результаты
        System.out.println(" Результат 1: " + future1.get());
        System.out.println(" Результат 2: " + future2.get());

        // Фиксированный
        ExecutorService fixedPool = Executors.newFixedThreadPool(3);

        // Кэширующий (создает при необходимости)
        ExecutorService cachedPool = Executors.newCachedThreadPool();

        // Одиночный
        ExecutorService singleThred = Executors.newSingleThreadExecutor();

        // Корректное завершение
        executorService.shutdown(); // плавное завершение
        // executorService.shutdownNow(); // немедленное завершение

        boolean terminated = executorService.awaitTermination(2, TimeUnit.SECONDS);
        System.out.println(" Завершен? " + terminated);

        fixedPool.shutdown();
        cachedPool.shutdown();
        singleThred.shutdown();

    }

}
