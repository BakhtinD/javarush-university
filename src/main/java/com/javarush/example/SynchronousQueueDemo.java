package com.javarush.example;

import java.util.concurrent.SynchronousQueue;

public class SynchronousQueueDemo {

    public static void main(String[] args) throws InterruptedException {

        SynchronousQueue<String> queue = new SynchronousQueue<>();

        // Продюсер
        Thread producer = new Thread(() -> {
           try {
               String[] items = {"Задача 1", "Задача 2", "Задача 3"};
               for (String item : items) {
                   System.out.println("Продюсер: пытаюсь положить " + item);

                   queue.put(item);
                   System.out.println("Продюсер: успешно передал " + item);
                   Thread.sleep(500);
               }
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
        });

        // Консюмер
        Thread consumer = new Thread(() -> {
           try {
               for (int i = 0; i < 3; i++) {
                   Thread.sleep(1000);
                   System.out.println("Консюмер: пытаюсь взять элемент...");
                   String item = queue.take();
                   System.out.println("Консюмер: получил " + item);
               }
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
        });

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();

        System.out.println("Передача завершена!");

    }

}
