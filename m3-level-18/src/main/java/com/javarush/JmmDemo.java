package com.javarush;

public class JmmDemo {

    volatile boolean flag = false;
    int data = 0;

    void writer() {
        data = 42; // (1)
        flag = true; // (2) heppense-before любого чтения flag
    }

    void reader() {
        if (flag) { // (3) если видит true, то гарантировано видит data = 42
            System.out.println(data);
        }
    }


    public static void main(String[] args) {

        // Правило 1
        int a = 1;
        int b = 2;
        int result = a + b; // 3 - не зависимо от оптимизаций

        // Правило 3: happens-before порядок


    }

    // Правило 2: мы получаем из переменной то, что в нее записали
    class SharedData {
        int x = 0; // 0, 1 - не будет мусора
    }

}
