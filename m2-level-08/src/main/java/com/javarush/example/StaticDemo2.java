package com.javarush.example;

class Settings {
    // Статическую переменную
    static String secretCode;

    // Статический блок инициализации
    static {
        System.out.println("Статический блок выполняется");
        // 1. Генерируем секретный код
        // 2. Можем считать файл. Сделать вычисления Хэш-ей и тп
        secretCode = "CODE-" + System.currentTimeMillis() % 1000;
        System.out.println("Сгенерирован код: " + secretCode);
    }

    // Конструктор
    public Settings() {
        System.out.println("Создан новый объект Setings");
    }

}

public class StaticDemo2 {

    public static void main(String[] args) {
        System.out.println("Первое обращение к классу Settings:");

        Settings s1 = new Settings();

        System.out.println("Второе обращение к классу Settings:");

        Settings s2 = new Settings();

        System.out.println("Исп. статическую переменную: ");
        System.out.println("Секретный код " + Settings.secretCode);
    }

}
