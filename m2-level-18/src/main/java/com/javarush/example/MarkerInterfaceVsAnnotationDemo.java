package com.javarush.example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 1. Маркерный интерфейс (старый способ
interface Validatable {

}

// 2. Аннотация (новый способ)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface Validated {
    String message() default "Требуется валидация";
}

// 3. Класс с маркерным интерфейсом
class Order implements Validatable {
    private String product;

    public Order(String product) {
        this.product = product;
    }

    public void process() {
        System.out.println("Обработка заказа: " + product);
    }
}

// 4. Класс с аннотацией
@Validated(message = "Пользователь должен быть проверен")
class Person {
    private String name;

    public Person(String name) {
        this.name = name;
    }

    public void display() {
        System.out.println("Пользователь: " + name);
    }
}

public class MarkerInterfaceVsAnnotationDemo {

    public static void main(String[] args) {
        Order order = new Order("Ноутбук");
        Person user = new Person("Елена");

        // Проверяем маркерный интерфейс (компиляция) - instanceof
        if (order instanceof Validatable) {
            System.out.println(" Заказ можно валидировать");
            order.process();
        }

        // Проверяем аннотацию (runtime) - isAnnotationPresent()
        Class<?> userClass = user.getClass();

        if (userClass.isAnnotationPresent(Validated.class)) {
            Validated annotation = userClass.getAnnotation(Validated.class);
            System.out.println(" Аннотация найдена " + annotation.message());
            user.display();
        }
    }

}
