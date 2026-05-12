package com.javarush.health;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Кастомный индикатор здоровья.
 *
 * Имитирует проверку доступности внешнего платёжного API.
 * Spring Boot автоматически включает его в /actuator/health.
 * Имя индикатора в JSON будет "externalApi" (из имени класса без суффикса HealthIndicator).
 */
@Component
public class ExternalApiHealthIndicator implements HealthIndicator {

    // В реальном проекте здесь был бы клиент для обращения к внешнему сервису.
    // Для учебного примера используем флаг, имитирующий доступность.
    private boolean serviceAvailable = true;

    @Override
    public Health health() {
        if (serviceAvailable) {
            return Health.up()
                    .withDetail("service", "ExternalPaymentAPI")
                    .withDetail("url", "https://api.example.com/health")
                    .withDetail("message", "Service is available")
                    .build();
        } else {
            return Health.down()
                    .withDetail("service", "ExternalPaymentAPI")
                    .withDetail("message", "Service responded with an error")
                    .build();
        }
    }

    // Метод для имитации отказа сервиса в тестовых целях
    public void simulateFailure() {
        this.serviceAvailable = false;
    }

    public void simulateRecovery() {
        this.serviceAvailable = true;
    }
}
