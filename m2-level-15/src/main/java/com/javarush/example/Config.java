package com.javarush.example;

public class Config {

    private static String apiKey = "secret-key-123";

    public static class Database {
        public static void connect() {
            System.out.println("Подключение с apiKey " + apiKey);
        }
    }

    public static void main(String[] args) {
        Database.connect();
    }

}

class App {
    void start() {
        Config.Database.connect();
    }
}