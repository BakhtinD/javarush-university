package com.javarush.example;

import java.util.ArrayList;
import java.util.List;

// Функциональный интерфейс
@FunctionalInterface
interface Calculator {
    int calculate(int a, int b);
}

class MathOperations {
    @Deprecated
    public int oldAdd(int a, int b) {
        return a + b;
    }

    public int newAdd(int a, int b) {
        return a + b;
    }

    // Метод с varargs
    public final <T> List<T> createList(T... elements) {
        List<T> list = new ArrayList<>();
        for (T element : elements) {
            list.add(element);
        }
        return list;
    }

}

class AdvanceMath extends MathOperations {
    @Override
    public int newAdd(int a, int b) {
        System.out.println("Расширенное сложение");
        return super.newAdd(a, b);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public List processData() {
        List list = new ArrayList();
        list.add("Элемент");
        return list;
    }

}

public class StandardAnnotationsDemo {

    public static void main(String[] args) {
        AdvanceMath math = new AdvanceMath();
        List<String> strings = math.createList("A", "B", "C");
        System.out.println(strings);

        Calculator adder = (a, b) -> a + b;
        System.out.println(adder.calculate(10, 20));

        List data = math.processData();
        System.out.println(data);
    }

}
