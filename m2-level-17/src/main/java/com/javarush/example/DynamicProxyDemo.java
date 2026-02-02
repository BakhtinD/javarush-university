package com.javarush.example;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

// Интерфейс - запроксируем
interface MessageSender {
    void send(String message);
}

// Реальная реализация
class EmaiSender implements MessageSender {

    @Override
    public void send(String message) {
        System.out.println("Отправляем email: " + message);
    }
}

// Обработчик вызовов. InvocationHandler - интерфейс для реализации динамических прокси
class LoggingHandler implements InvocationHandler {

    private Object target;

    public LoggingHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println(" Вызов метода: " + method.getName());
        System.out.println(" Аргументы " + (args != null ? args[0] : "нет"));

        // Вызов оригинального метода
        Object result = method.invoke(target, args);

        System.out.println(" Метод выполнен!");
        return result;
    }
}

public class DynamicProxyDemo {

    public static void main(String[] args) {

        MessageSender realSender = new EmaiSender();

        MessageSender proxy = (MessageSender) Proxy.newProxyInstance(
                MessageSender.class.getClassLoader(),
                new Class[] { MessageSender.class },
                new LoggingHandler(realSender)
        );

        System.out.println("Без прокси: ");
        realSender.send("Привет!");

        // Используем прокси
        System.out.println("Через прокси: ");
        proxy.send("Привет через прокси!");

    }

}
