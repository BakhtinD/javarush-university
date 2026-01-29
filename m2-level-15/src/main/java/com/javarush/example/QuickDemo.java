package com.javarush.example;

public class QuickDemo {

    public static void main(String[] args) {

        Thread thread = new Thread() {
            public void run() {
                System.out.println("Анонимный поток");
            }
        };
        thread.start();

        new Thread() {
            public void run() {
                System.out.println("Второй анонимный поток");
            }
        }.start();

    }

}
