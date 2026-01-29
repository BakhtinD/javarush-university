package com.javarush;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractList;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        // 1
        AbstractList list = new ArrayList(); // внутренний класс Itr в AbstractList

        // 2
        Integer i = 100; // внутренний класс IntegerCache

        // 3 статический метод nullInputStream() в InputStream
        InputStream inputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };

    }

}
