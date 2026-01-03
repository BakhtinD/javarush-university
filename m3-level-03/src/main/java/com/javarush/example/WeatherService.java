package com.javarush.example;

public class WeatherService {
    private static boolean apiConnected = false;

    // "Дорогая" операция - подключение к внешнему API
    public void connectToApi() {
        System.out.println("Подключение к погодному API...");
        // Имитация долгого подключения
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        apiConnected = true;
        System.out.println("Подключено!");
    }

    public void disconnect() {
        apiConnected = false;
        System.out.println("Отключено от API");
    }

    public String getWeather(String city) {
        if (!apiConnected) {
            throw new IllegalStateException("API не подключено");
        }
        // Имитация запроса к API
        return "Солнечно, 25°C в " + city;
    }
}
