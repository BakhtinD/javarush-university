# Модуль 5. Уровень 10: Создание REST API с Spring

Для подключения к консоли:
http://localhost:8080/h2-console/

### CURL команды для тестирования HelloController

```bash
curl http://localhost:8080/
```

Запрос для обновления двух пользователей
```bash
curl -X POST "http://localhost:8080/users/update-emails?id1=1&id2=2&email1=alice.new@example.com&email2=bob.new@example.com" \
  -H "Content-Type: application/json"
```

Метод, который выбрасывает checked исключение после сохранения
```bash
curl -X POST "http://localhost:8080/users/1/email?email=alice.default@example.com"
```

Получение всех пользователей (GET)
```bash
curl -s http://localhost:8080/users | jq .
```

Сервис RegistrationService с использованием TransactionTemplate
```bash
curl -X POST "http://localhost:8080/register?name=John&email=john@example.com"
```

### References 
1. Ветка с заготовкой spring-data-jpa & CRUD https://github.com/sproshchaev/javarush-university/blob/m5-level-06-spring-data-jpa/m5-level-06/README.md  