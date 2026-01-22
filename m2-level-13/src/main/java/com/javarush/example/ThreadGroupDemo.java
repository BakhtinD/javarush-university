package com.javarush.example;

import static java.lang.Thread.sleep;

public class ThreadGroupDemo {

    public static void main(String[] args) throws InterruptedException {

        // 1. Создаем группу
        ThreadGroup group = new ThreadGroup("Рабочие потоки");

        // 2. Создаем 3 потока в группе
        for (int i = 1; i <= 3; i++) {
            new Thread(group, () -> {
                // Работа внутри потока
                System.out.println(Thread.currentThread().getName() +
                        " в группе: " + Thread.currentThread().getThreadGroup().getName());
                System.out.println("Итого потоков " + group.activeCount());
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + "  прерван");
                }
            }, "Поток-" + i).start();
        }

        // 3. Ждем и показываем информацию
        Thread.sleep(5000);
        System.out.println("\nВ группе " + group.getName() + " потоков " + group.activeCount());

        // 4. Прерываем все потоки группы одной командой
        System.out.println("Прерываем: ");
        group.interrupt();

        // 5. Покажем иерархию
        System.out.println("Текущая группа " + group.getName());
        System.out.println("Родительская группа " + group.getParent().getName());

    }

}
