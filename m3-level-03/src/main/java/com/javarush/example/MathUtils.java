package com.javarush.example;

public class MathUtils {
    public int factorial(int n) {
        if (n < 0) throw new IllegalArgumentException();
        if (n <= 1) return 1;

        // Медленная реализация для демонстрации @Timeout
        try { Thread.sleep(n * 100); } catch (InterruptedException e) {}

        int result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    public boolean isPrime(int n) {
        if (n <= 1) return false;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }
}