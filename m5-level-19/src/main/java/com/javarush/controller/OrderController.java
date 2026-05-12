package com.javarush.controller;

import com.javarush.health.ExternalApiHealthIndicator;
import com.javarush.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для демонстрации кастомных метрик Micrometer и индикатора здоровья.
 *
 * Каждый POST /orders увеличивает счётчик orders.created.total.
 * После нескольких вызовов проверьте:
 * GET /actuator/metrics/orders.created.total
 * GET /actuator/prometheus  (поиск по "orders_created_total")
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final ExternalApiHealthIndicator externalApiHealthIndicator;

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestParam(defaultValue = "Laptop") String product) {
        String result = orderService.createOrder(product);
        return ResponseEntity.ok(result);
    }

    // Эндпоинты для демонстрации изменения состояния кастомного HealthIndicator
    @PostMapping("/health/down")
    public ResponseEntity<String> simulateExternalApiDown() {
        externalApiHealthIndicator.simulateFailure();
        return ResponseEntity.ok("ExternalAPI marked as DOWN. Check: GET /actuator/health");
    }

    @PostMapping("/health/up")
    public ResponseEntity<String> simulateExternalApiUp() {
        externalApiHealthIndicator.simulateRecovery();
        return ResponseEntity.ok("ExternalAPI marked as UP. Check: GET /actuator/health");
    }
}
