package com.javarush.example;

import java.lang.annotation.*;

// Мета-анотации для нашей кастомной аннотации
@Retention(RetentionPolicy.RUNTIME) // сохраняется во время выполнения
@Target({ElementType.TYPE, ElementType.METHOD}) // класс и метод
@Inherited // наследование в подклассах
@Documented
@interface MyAnnotation {
    // Параметры  - как методы
    String name() default "без имени"; // параметр со значением по умолчанию
    int value(); // обязательный параметр
    String description() default ""; // необязательный параметр

}

@MyAnnotation(value = 100, name = "Важный класс", description = "Основной класс приложения")
class ImportantClass {

    @MyAnnotation(value = 50)
    public void processData() {
        System.out.println("Обработка данных...");
    }

    @MyAnnotation(value = 25, description = "Тестовый метод")
    public void testMethod() {
        System.out.println("Тестирование...");
    }

}

// унаследованы аннотации - так как было указано @Inherited // наследование в подклассах
class SubClass extends ImportantClass {
    // Здесь можно исп @MyAnnotation
}

public class AnnotationParametersDemo {

    public static void main(String[] args) {
        Class<ImportantClass> clazz= ImportantClass.class;

        if (clazz.isAnnotationPresent(MyAnnotation.class)) {
            MyAnnotation annotation = clazz.getAnnotation(MyAnnotation.class);
            System.out.println("Аннотация на классе");
            System.out.println(" name: " + annotation.name());
            System.out.println(" value: " + annotation.value());
            System.out.println(" description: " + annotation.description());
        }

        //todo Дописать проверку аннотаций на методах
    }

}
