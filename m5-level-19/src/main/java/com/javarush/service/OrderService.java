package com.javarush.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Демонстрация кастомных метрик с Micrometer.
 *
 * Counter  — счётчик: считает созданные заказы (только растёт).
 * Gauge    — датчик: показывает текущий размер очереди заказов.
 */
@Slf4j
@Service
public class OrderService {

    private final Counter ordersCreatedCounter;
    private final AtomicInteger orderQueueSize;

    public OrderService(MeterRegistry meterRegistry) {
        // Counter: метрика "orders.created.total" с тегом region=europe
        this.ordersCreatedCounter = meterRegistry.counter("orders.created.total", "region", "europe");

        // Gauge: отслеживает текущее значение AtomicInteger в реальном времени
        this.orderQueueSize = meterRegistry.gauge("orders.queue.size", new AtomicInteger(0));
    }

    public String createOrder(String productName) {
        log.info("Creating order for product: {}", productName);

        ordersCreatedCounter.increment();

        // Имитируем попадание заказа в очередь
        orderQueueSize.incrementAndGet();
        log.debug("Order added to queue. Queue size: {}", orderQueueSize.get());

        // Имитируем обработку
        orderQueueSize.decrementAndGet();
        log.debug("Order processed. Queue size: {}", orderQueueSize.get());

        return "Order created for: " + productName;
    }
}
