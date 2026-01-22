package com.javarush.example;

import java.util.concurrent.*;

public class CallableDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Callable<String> callableTask = () -> {
            System.out.println("Callable работает");
            Thread.sleep(1000);
            return "Результат из потока!";
        };

        // Запускаем через ExecutorService
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> future = executorService.submit(callableTask);

        System.out.println("Ждем результат...");
        String result = future.get(); // Блокируется пока задача не завершится
        System.out.println("Результат: " + result);

        executorService.shutdown();


    }

}
