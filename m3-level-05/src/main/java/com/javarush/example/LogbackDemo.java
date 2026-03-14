package com.javarush.example;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

/**
 * Используется конфигурация из logback.xml
 */
public class LogbackDemo {

    // 1. Получение логгера через фабрику SLF4J
    private static final Logger log = LogManager.getLogger(LogbackDemo.class);

    public static void main(String[] args) {

        // 2. Logback
        log.trace("Уровень TRACE");
        log.debug("Уровень DEBUG");
        log.info("Уровень INFO");
        log.warn("Уровень WARN");
        log.error("Уровень ERROR");
        log.fatal("Уровень FATAl");

        // 3. Логирование с исключением
        try {
            int x = 10 / 0;
        } catch (ArithmeticException e) {
            log.error("Деление на ноль!", e);
        }

    }

}
