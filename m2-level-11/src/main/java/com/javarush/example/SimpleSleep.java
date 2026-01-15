package com.javarush.example;

public class SimpleSleep {

    public static void main(String[] args) {

        System.out.println("Приложение запустилось");

        try {
            Thread.sleep(2000); // 2 сек - не для точного тайминга
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Приложение завершилось");
    }

}
