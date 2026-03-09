package com.javarush.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class NotificationServiceTest {


    @Test
    // Пример нейминга тестовых методов:
    // имяТестируемогоМетода_ДействиеВТесте_ОжидаемыйРезультат
    void notifyUser_SuccessfulSend_ReturnsTrue() {
        // Создаем

        // 1. Создаем мок-объект
        // настоящая реализация (в проде): EmailService emailService = new EmailServiceImpl();
        EmailService emailService = mock(EmailService.class);

        // 2. Настроить поведение мок-объекта
        //     boolean sendEmail(String to, String subject, String body);
        doReturn(true)
                .when(emailService)
                .sendEmail( // имя метода, который участвует в тестировании из emailService
                        eq("user@mail.ru"), // String to
                        eq("Уведомление"),  // String subject
                        anyString()              // Любое тело сообщения
                );

        // Вызываем

        // 3. Используем Мок в тестируемом коде
        NotificationService notificationService = new NotificationService(emailService);
        boolean result = notificationService.notifyUser("user@mail.ru",
                "Тело письма содержит важное сообщение.");


        // Сравниваем

        // 4. Проверяем результат
        assertTrue(result);

        // 5. Дополнительно можем проверить - метод вызвался
        verify(emailService).sendEmail(
                "user@mail.ru",
                "Уведомление",
                "Тело письма содержит важное сообщение."
        );



    }
}