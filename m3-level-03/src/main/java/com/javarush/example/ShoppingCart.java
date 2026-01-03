package com.javarush.example;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private List<String> items = new ArrayList<>();
    private int total = 0;

    public void addItem(String item, int price) {
        items.add(item);
        total += price;
    }

    public void clear() {
        items.clear();
        total = 0;
    }

    public List<String> getItems() {
        return new ArrayList<>(items); // возвращаем копию
    }

    public int getTotal() {
        return total;
    }
}
