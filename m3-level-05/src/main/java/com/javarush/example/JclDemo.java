package com.javarush.example;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JclDemo {

    // 1. Получение логгера через фабрику JCL
    private static final Log log = LogFactory.getLog(JclDemo.class);

    public static void main(String[] args) {

        // 2. Используем API JCL
        log.trace("Уровень TRACE");
        log.debug("Уровень DEBUG");
        log.info("Уровень INFO");
        log.warn("Уровень WARN");
        log.error("Уровень ERROR");
        log.fatal("Уровень FATAl");

        // 3. Логирование с исключением
        try {
            String nullString = null;
            nullString.length();
        } catch (NullPointerException e) {
            log.error("Получен NPE", e);
        }




    }

}
