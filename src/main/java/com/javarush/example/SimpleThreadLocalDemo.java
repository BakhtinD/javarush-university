package com.javarush.example;

public class SimpleThreadLocalDemo {

    private static final ThreadLocal<String> user = new ThreadLocal<>();

    public static void main(String[] args) {

        new Thread(() -> {
            user.set("Алиса");
            System.out.println("Поток 1: " + user.get());
            user.remove();
        }).start();

        new Thread(() -> {
            user.set("Боб");
            System.out.println("Поток 2: " + user.get());
            user.remove();
        }).start();

        System.out.println("Главный: " + user.get()); // null


    }


}
