package com.javarush.example;

public class SwitchExpressionExample {

    public static void main(String[] args) {
        String[] products = {"Apple", "Peach", "Raspberry", "Banana", "Carrot", null};

        for (String product : products) {

            // 1. Switch expression
            String productType = switch (product) {
                case "Apple", "Peach" -> "Fruit"; // группировка значений
                case "Raspberry" -> "Berry";
                case "Banana" -> "Tropical Fruit";
                case null -> "null product"; // на Java 21
                default -> "other product";
            };
            System.out.println(product + " -> " + productType);
        }

        // 2. Использование в методе
        System.out.println("\nИспользование в методе: ");
        for (String product : products) {
            System.out.println(product + " -> " + getProductCategory(product));
        }

        // 3. Использование enum
        Day day = Day.WEDNESDAY;
        String dayType = switch (day) {
            case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "Будний день";
            case SATURDAY, SUNDAY -> "Выходной";
        };
        System.out.println(day + " -> " + dayType); // WEDNESDAY -> Будний день

    }

    // Метод возвращает категорию продукта через switch expression
    public static String getProductCategory(String product) {
        return switch (product) {
            case "Apple", "Peach" -> "Fruit"; // группировка значений
            case "Raspberry" -> "Berry";
            case "Banana" -> "Tropical Fruit";
            case null -> "null product"; // на Java 21
            default -> "other product";
        };
    }

    enum Day {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

}
