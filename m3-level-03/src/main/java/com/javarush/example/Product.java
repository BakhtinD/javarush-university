package com.javarush.example;

public class Product {
    private String name;
    private double price;
    private int id;

    public Product(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    // Геттеры, equals, hashCode
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getId() { return id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
