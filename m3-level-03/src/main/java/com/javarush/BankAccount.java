package com.javarush;

public class BankAccount {
    private double balance;

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
        // Ошибка: что если amount <= 0?
    }

    public void withdraw(double amount) {
        balance -= amount;
        // Ошибка: нет проверки на отрицательный баланс
    }

    public double getBalance() {
        return balance;
    }
}