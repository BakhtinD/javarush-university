package com.javarush.example;

public class YieldExample {

    public static void main(String[] args) {

        String[] products = {"Apple", "Peach", "Raspberry", "Banana", "Carrot"};

        for (String product : products) {
            String description = switch (product) {
                case "Apple", "Peach" -> {
                    System.out.println(" Определяем категорию фрукта...");
                    yield "Фрукт сладкий";
                }
                case "Raspberry" -> {
                    System.out.println(" Анализируем ягоду...");
                    String taste = "кисло-сладкая";
                    yield "Ягода " + taste; // вычисленное значение
                }
                default -> {
                    System.out.println(" Продукт не распознан");
                    yield "Что-то еще...";
                }
            };
            System.out.println(product + " -> " + description);
        }
    }

}
