package com.javarush.example;

class Box {

    int area = calculateArea();

    // Хотим, чтобы размер был 10
    int size = 10;

    int calculateArea() {
        //  на момент вызова size еще равен 0 (дефолтное значение)
        System.out.println(" calculateArea() вызван. size = " + size);
        return size * size;
    }

    void print() {
        System.out.println("Box: size = " + size + ", area = " + area );
    }
}

class CorrectBox {
    int size;
    int area;

    // Инициализация проводится в конструкторе
    CorrectBox(int initialSize) {
        this.size = initialSize; // сначала задаем size
        this.area = calculateArea(); // вычисляем area
    }

    int calculateArea() {
        System.out.println(" calculateArea() вызван. size = " + size);
        return size * size;
    }

    void print() {
        System.out.println("CorrectBox: size = " + size + ", area = " + area);
    }

}

public class BoxDemo {

    public static void main(String[] args) {
        // некорректная инициализация
        Box box = new Box();
        box.print();

        // правильная инициализация
        CorrectBox correctBox = new CorrectBox(10);
        correctBox.print();
    }

}
