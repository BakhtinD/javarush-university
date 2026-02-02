package com.javarush.example;

import com.javarush.Main;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class SomeClass {
    private String secret = "секрет";
    public void show() {
        System.out.println("secret: " + secret);
    }
}

public class QuickDemo2 {

    public static void main(String[] args) throws ClassNotFoundException, NoSuchFieldException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        Object object = new SomeClass();
        Class<?> clazz = object.getClass();

        // Первый метод: устаревший @Deprecated(since="9")
        Object newObj = Class.forName("com.javarush.example.SomeClass").newInstance();

        // Второй метод: рекомендуемый - через конструктор
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        SomeClass someClass = (SomeClass) constructor.newInstance();
        someClass.show(); // secret: секрет

        Field field = clazz.getDeclaredField("secret");
        field.setAccessible(true);
        field.set(newObj, "новый секрет");

        Method method = clazz.getMethod("show");
        method.invoke(newObj);


    }

}
