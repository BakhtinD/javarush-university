package com.javarush.example;

// Класс, который мы будем лениво загружать
class DatabaseConnector {

    // Статическое поле с инициализацией
    static String driverName = loadDriver();

    // Статический блок инициализации
    static {
        System.out.println(" [Статический блок] DatabaseConnector: Инициализация подключения...");
    }

    private static String loadDriver() {
        System.out.println(" [Статическое поле] DatabaseConnector: загрузка драйвера БД...");
        return "com.mysql.jdbc.Driver";
    }

    // Обычный (не статический) метод
    public void connect() {
        System.out.println(" [Метод] Установлено подключение с исп. драйвера " + driverName);
    }

}

public class StaticDemo {

    // Статический блок главного класса
    static {
        System.out.println("Статический блок Main: программа запущена.");
    }

    public static void main(String[] args) {
        System.out.println("Запуск main метода");

        System.out.println("1. Обращаемся к статическому полю другого класса:");

        System.out.println(" DatabaseConnector.driverName = " + DatabaseConnector.driverName);

        System.out.println("Создаем объект (класс уже загружен): ");
        DatabaseConnector connector = new DatabaseConnector();
        connector.connect();

        System.out.println("Пытаемся загрузить класс еще раз:");

        DatabaseConnector connector2 = new DatabaseConnector();
        connector2.connect();
    }
}
