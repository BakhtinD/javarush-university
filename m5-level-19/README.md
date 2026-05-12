# Модуль 5. Уровень 19: Spring Boot Actuator и мониторинг приложений

Учебный проект для демонстрации всех ключевых возможностей **Spring Boot Actuator**:
подключение, базовые эндпоинты, конфигурация, безопасность, кастомные индикаторы здоровья,
кастомные метрики с Micrometer, экспорт в Prometheus, управление логированием на лету.

Основа проекта взята из модуля **m5-level-06** (Spring Data JPA + H2 + транзакции).

---

## Структура проекта

```
com.javarush
├── Main.java                                  — точка входа Spring Boot
├── config/
│   └── SecurityConfig.java                    — защита эндпоинтов Actuator
├── controller/
│   ├── HelloController.java                   — CRUD-эндпоинты (из m5-level-06)
│   └── OrderController.java                   — демо кастомных метрик и health
├── entity/
│   └── User.java                              — JPA-сущность
├── exception/
│   └── BusinessException.java                 — checked-исключение
├── health/
│   └── ExternalApiHealthIndicator.java        — кастомный HealthIndicator
├── repository/
│   └── UserRepository.java                    — Spring Data JPA
└── service/
    ├── OrderService.java                      — Counter + Gauge (Micrometer)
    ├── RegistrationService.java               — TransactionTemplate (из m5-level-06)
    └── UserService.java                       — @Transactional (из m5-level-06)
```

---

## Запуск

```bash
./mvnw spring-boot:run
```

или через IntelliJ IDEA — запустить `Main.java`.

**H2 консоль:** http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- User: `sa`, Password: _(пусто)_

---

## Безопасность Actuator

| Эндпоинт                       | Доступ            |
|-------------------------------|-------------------|
| `/actuator/health`            | Все               |
| `/actuator/info`              | Все               |
| `/actuator/**` (остальные)    | Только `ADMIN`    |
| Бизнес-эндпоинты (`/`, `/users`, ...) | Все      |

**Учётные данные для admin:** `admin` / `admin`

---

## CURL команды

### 1. Подключение Actuator — базовые эндпоинты

# Статус приложения (открыт для всех)
```bash
curl http://localhost:8080/actuator/health
```

# Информация о приложении (открыт для всех)
```bash
curl http://localhost:8080/actuator/info
```

### 2. Эндпоинт `/health` — детальная информация


# Детали здоровья (требует Basic Auth: admin/admin)
```bash
curl -u admin:admin http://localhost:8080/actuator/health
```

# Ответ содержит статусы всех HealthIndicator-ов:
# - db          (H2 database)
# - diskSpace   (свободное место на диске)
# - externalApi (наш кастомный индикатор)


### 3. Кастомный индикатор здоровья — симуляция сбоя

# Симулируем отказ внешнего API
```bash
curl -X POST http://localhost:8080/orders/health/down
```

# Смотрим: статус externalApi стал DOWN, общий статус — DOWN
```bash
curl http://localhost:8080/actuator/health
```

# Восстанавливаем
```bash
curl -X POST http://localhost:8080/orders/health/up
```

# Снова UP
```bash
curl http://localhost:8080/actuator/health
```

### 4. Эндпоинт `/metrics`


# Список всех метрик
```bash
curl -u admin:admin http://localhost:8080/actuator/metrics
```

# Конкретная метрика — использование памяти JVM
```bash
curl -u admin:admin http://localhost:8080/actuator/metrics/jvm.memory.used
```

# Загрузка CPU
```bash
curl -u admin:admin http://localhost:8080/actuator/metrics/system.cpu.usage
```

# Статистика HTTP-запросов
```bash
curl -u admin:admin http://localhost:8080/actuator/metrics/http.server.requests
```

### 5. Кастомные метрики с Micrometer

# Создаём заказы (счётчик orders.created.total растёт)
```bash
curl -X POST "http://localhost:8080/orders?product=Laptop"
curl -X POST "http://localhost:8080/orders?product=Phone"
curl -X POST "http://localhost:8080/orders?product=Tablet"
```
# Смотрим значение счётчика
```bash
curl -u admin:admin http://localhost:8080/actuator/metrics/orders.created.total
```

# Смотрим текущий размер очереди (Gauge)
```bash
curl -u admin:admin http://localhost:8080/actuator/metrics/orders.queue.size
```

### 6. Настройка экспорта в Prometheus

# Все метрики в формате Prometheus (текстовый формат для скрапинга)
```bash
curl -u admin:admin http://localhost:8080/actuator/prometheus
```

# Ищем наш кастомный счётчик
```bash
curl -u admin:admin http://localhost:8080/actuator/prometheus | grep orders
```

### 7. Логирование через Actuator

# Список всех логгеров и их уровней
```bash
curl -u admin:admin http://localhost:8080/actuator/loggers
```

# Уровень конкретного логгера
```bash
curl -u admin:admin http://localhost:8080/actuator/loggers/com.javarush.service.OrderService
```

# Включаем DEBUG для OrderService БЕЗ ПЕРЕЗАПУСКА приложения
```bash
curl -X POST -u admin:admin \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": "DEBUG"}' \
  http://localhost:8080/actuator/loggers/com.javarush.service.OrderService
```

# Теперь в логах появятся DEBUG-сообщения:
```bash
curl -X POST "http://localhost:8080/orders?product=Laptop"
```

# Возвращаем INFO
```bash
curl -X POST -u admin:admin \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": "INFO"}' \
  http://localhost:8080/actuator/loggers/com.javarush.service.OrderService
```

### 8. Эндпоинт `/env` — переменные окружения

# Все переменные окружения (чувствительные замаскированы)
```bash
curl -u admin:admin http://localhost:8080/actuator/env
```

---

## Эндпоинты из m5-level-06 (бизнес-логика)

# Главная страница
```bash
curl http://localhost:8080/
```

# Все пользователи
```bash
curl -s http://localhost:8080/users | jq .
```

# Обновление email двух пользователей в одной транзакции
```bash
curl -X POST "http://localhost:8080/users/update-emails?id1=1&id2=2&email1=alice.new@example.com&email2=bob.new@example.com"
```

# Регистрация пользователя через TransactionTemplate
```bash
curl -X POST "http://localhost:8080/register?name=John&email=john@example.com"
```

---

## Интеграция с Prometheus и Grafana

Для локального запуска стека мониторинга создайте `docker-compose.yml`:

```yaml
services:
  prometheus:
    image: prom/prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml

  grafana:
    image: grafana/grafana
    ports:
      - "3000:3000"
```

Конфигурация `prometheus.yml` (Prometheus будет скрапить наше приложение):

```yaml
scrape_configs:
  - job_name: 'm5-level-19'
    scrape_interval: 15s
    metrics_path: '/actuator/prometheus'
    basic_auth:
      username: admin
      password: admin
    static_configs:
      - targets: ['host.docker.internal:8080']
```

После запуска:
- Prometheus UI: http://localhost:9090
- Grafana: http://localhost:3000 (admin/admin)
- В Grafana добавить Prometheus как DataSource: `http://prometheus:9090`

---

## References

1. [Spring Boot Actuator docs](https://docs.spring.io/spring-boot/reference/actuator/)
2. [Micrometer docs](https://micrometer.io/docs)
3. [Prometheus docs](https://prometheus.io/docs/introduction/overview/)
4. Ветка с основой проекта: m5-level-06
