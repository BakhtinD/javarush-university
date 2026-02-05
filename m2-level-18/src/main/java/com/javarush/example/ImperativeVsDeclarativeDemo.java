package com.javarush.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImperativeVsDeclarativeDemo {

    public static void main(String[] args) {

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        // 1. Императивный подход (как)
        int sumImperative = 0;
        for (int num : numbers) {
            if (num % 2 ==0) {
                sumImperative += num;
            }
        }
        System.out.println("Сумма четных чисел: " + sumImperative);

        // 2. Декларативный подход (что)
        int sumDeclarative = numbers.stream()
                .filter(n -> n % 2 == 0) // ЧТО: фильтруем четные
                .mapToInt(n -> n) // ЧТО: преобразование
                .sum(); // ЧТО: суммируем
        System.out.println("Сумма четных чисел: " + sumDeclarative);

    }

}
