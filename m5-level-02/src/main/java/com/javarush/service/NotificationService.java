package com.javarush.service;

import com.javarush.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Пример внедрения зависимостей через сеттеры
 */
@Service
public class NotificationService {
    private EmailService emailService;
    private SmsService smsService;

    // сеттер для поля EmailService
    @Autowired // Обязательная зависимость
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Autowired(required = false) // если бина с типом SmsService нет в контексте
    // то исключения у нас не будет - сеттер не будет вызван
    public void setSmsService(SmsService smsService) {
        this.smsService = smsService;
    }

    public void notifyUser(User user, String message) {
        if (emailService != null) {
            emailService.sendEmail(user, message);
        }
        if (smsService != null) {
            smsService.sendSms(user, message);
        }
    }

}
