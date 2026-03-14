package com.javarush.example;

import org.apache.log4j.Logger;

// @Slf4j
public class Log4jDemo {

    // 1. Получаем логгер по имени класса
    private static final Logger log = Logger.getLogger(Log4jDemo.class);

    public static void main(String[] args) {
        System.out.println("Log4j Demo");

        // 2. Логирование сообщений с разным уровнем
        // Информативность <----> Краткость
        // TRACE < DEBUG < INFO < WARN < ERROR < FATAL
        log.trace("Это сообщение уровня TRACE. Его может быть не видно. Фильтр rootLogger=DEBUG");
        log.debug("Сообщение для отладки (DEBUG)");
        log.info("Информационное сообщение (INFO)"); // Пример: Метод main выполняется...
        log.warn("Предупреждение (WARN)"); // Пример: На диске заканчивается свободное место...
        log.error("Сообщение об ошибке (ERROR)"); // Пример: На диске закончилось место!
        log.fatal("Критическая ошибка (FATAL)"); // Пример: приложение не может далее выполнять действия

        // 3. Демо логирования
        try {
            int a = 10;
            int b = 0;
            // log.debug("Первое число {}", a);
            log.debug("Первое число " + a + ", Второе число " + b);
            int result = a / b;
        } catch (ArithmeticException e) {
            log.error("Произошло деление на ноль!", e);
        }



    }

}
