package com.javarush.model;

public class Order {
    private String id;
    private double amount;
    private String cardNumber;

    public Order(String id, double amount, String cardNumber) {
        this.id = id;
        this.amount = amount;
        this.cardNumber = cardNumber;
    }

    public String getId() { return id; }
    public double getAmount() { return amount; }
    public String getCardNumber() { return cardNumber; }
}