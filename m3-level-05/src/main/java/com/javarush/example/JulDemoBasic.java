package com.javarush.example;

import java.util.logging.Logger; // JUL
import java.util.logging.Level;

public class JulDemoBasic {

    // 1. Получаем логгер JUL
    private static final Logger log = Logger.getLogger(JulDemoBasic.class.getName());

    public static void main(String[] args) {
        // 2. Логирование сообщений JUL
        log.severe("SEVERE (аналог ERROR/FATAL)");
        log.warning("WARNING (аналог WARN)");
        log.info("Это INFO");

        // Уровни для отладки отличаются
        log.config("CONFIG используется для вывода параметров конфигурации");
        log.finer("FINER более детальная отладка");
        log.finest("FINEST - максимальная детализация");

        // JUL по умолчанию имеет уровень INFO
        // CONFIG FINE FINER FINEST - будут пропущены

        try {
            int[] arr = new int[2];
            arr[5] = 10;
        } catch (ArrayIndexOutOfBoundsException e) {
            log.log(Level.SEVERE, "Выход за границы массива!", e);
        }

    }


}
