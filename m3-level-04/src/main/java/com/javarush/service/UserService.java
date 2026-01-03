package com.javarush.service;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    private List<String> userLogs = new ArrayList<>();
    private ExternalAuditService auditService;

    public UserService(ExternalAuditService auditService) {
        this.auditService = auditService;
    }

    // Метод, который мы хотим тестировать по-настоящему
    public String createUser(String username, String email) {
        // Реальная бизнес-логика
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя пользователя обязательно");
        }

        if (!email.contains("@")) {
            throw new IllegalArgumentException("Некорректный email");
        }

        String userId = "USER-" + System.currentTimeMillis();
        logAction("Создан пользователь: " + username);

        // Вызов внешнего сервиса (его замокируем)
        auditService.logUserCreation(username, userId);

        return userId;
    }

    // Метод для логирования
    private void logAction(String message) {
        userLogs.add(message);
        System.out.println("[LOG] " + message);
    }

    // Геттер для проверки логов
    public List<String> getUserLogs() {
        return new ArrayList<>(userLogs);
    }
}
