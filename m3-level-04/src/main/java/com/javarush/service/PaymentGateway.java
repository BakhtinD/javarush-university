package com.javarush.service;

public interface PaymentGateway {
    boolean processPayment(double amount, String cardNumber);
}
