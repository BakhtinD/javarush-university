package com.javarush.example;

public class PriorityDemo {

    public static void main(String[] args) {

        Thread low = new Thread(() -> work("'Низкий'", 1));
        Thread norm = new Thread(() -> work("'Средний'", 5));
        Thread high = new Thread(() -> work("'Высокий'", 10));

        // Устанавливаем приоритет
        low.setPriority(Thread.MIN_PRIORITY);
        norm.setPriority(Thread.NORM_PRIORITY);
        high.setPriority(Thread.MAX_PRIORITY);

        // Запускаем
        low.start();
        norm.start();
        high.start();

        System.out.println("Главный поток приоритет:" + Thread.currentThread().getPriority());

    }

    private static void work(String name, int expPriority) {
        System.out.println(name + " поток запущен, приоритет " + Thread.currentThread().getPriority());

        long count = 0;
        for (int i = 0; i < 10_000_000; i++) {
            count++;
        }
        System.out.println(name + "  поток завершил " + count + " операций");
    }

}
