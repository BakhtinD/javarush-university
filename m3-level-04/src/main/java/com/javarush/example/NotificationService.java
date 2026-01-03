package com.javarush.example;

public class NotificationService {
    private EmailService emailService;

    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    public boolean notifyUser(String email, String message) {
        // Сложная бизнес-логика...
        return emailService.sendEmail(email, "Уведомление", message);
    }
}