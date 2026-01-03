package com.javarush;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== Hello World from Maven! ===");
        System.out.println("Project: " + System.getProperty("project.name", "Unknown"));
        System.out.println("Version: " + System.getProperty("project.version", "Unknown"));
        System.out.println("Java: " + System.getProperty("java.version"));
        System.out.println("Encoding: " + System.getProperty("file.encoding"));

        // Демонстрация работы с кириллицей (проверка кодировки)
        System.out.println("\nПроверка кириллицы: Привет, мир!");

        // Аргументы командной строки
        if (args.length > 0) {
            System.out.println("\nАргументы командной строки:");
            for (int i = 0; i < args.length; i++) {
                System.out.println("  arg[" + i + "]: " + args[i]);
            }
        }
    }
}
