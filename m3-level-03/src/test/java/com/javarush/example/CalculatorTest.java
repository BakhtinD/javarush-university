package com.javarush.example;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    @BeforeEach
    void setUp() {
        System.out.println("setUp");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9})
    void add_param(int a) {
        System.out.println("a=" + a);
        // AAA: Arrange

    }

    @ParameterizedTest
    // @CsvFileSource(resources = "/test-data/add-test.csv", numLinesToSkip = 1, encoding = "UTF")
    @CsvSource({"2, 3, 5",
                "3, 3, 6"
    })
    void testAdd(int a, int b, int expected) {
        Calculator calculator = new Calculator();
        int result = calculator.add(a, b);
        assertEquals(expected, result);
    }

    @Test
    void add_TwoPositiveNumbers_ReturnsSum() {
        // AAA: Arrange
        Calculator calculator = new Calculator();
        int a = 2;
        int b = 3;

        // Act
        int result = calculator.add(a, b);

        // Assert
        assertEquals(5, result);
    }

    @Test
    void add_TwoPositiveNumbers_ReturnsSum2() {
        // AAA: Arrange
        Calculator calculator = new Calculator();
        int a = 3;
        int b = 3;

        // Act
        int result = calculator.add(a, b);

        // Assert
        assertEquals(6, result);
    }

    @AfterEach
    void tearDown() {
        System.out.println("tearDown");
    }


    @Test
    @DisplayName("Проверка на исключение")
    void divide() {
        Calculator calculator = new Calculator();
        int a = 10;
        int b = 0;
        assertThrows(ArithmeticException.class,
                () -> calculator.divide(a, b));
    }
}