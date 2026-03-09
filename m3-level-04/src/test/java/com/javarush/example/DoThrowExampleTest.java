package com.javarush.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOError;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class DoThrowExampleTest {

    // демо для doThrow() (метод назван не по неймингу)
    @Test
    @DisplayName("Тестируем doThrow()")
    void allThreeMethodsInOneTest() {

        List<String> mockList = mock(List.class);

        // 1. doTrow(класс) для void-метода
        doThrow(RuntimeException.class).when(mockList).clear();
        assertThrows(RuntimeException.class, mockList::clear);

        // 2. when().thenThrow(класс) для не-void метода
        when(mockList.get(0)).thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> mockList.get(0));

        when(mockList.get(1)).thenThrow(Error.class);
        assertThrows(Error.class, () -> mockList.get(1));

        // 3. doThrow(объект) с конкретным исключением
        Exception specificException = new RuntimeException("Конкретная ошибка");
        doThrow(specificException).when(mockList).size();

        Exception thrown = assertThrows(RuntimeException.class, () -> mockList.size());
        assertEquals("Конкретная ошибка", thrown.getMessage());


    }

}
