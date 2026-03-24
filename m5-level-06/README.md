# Модуль 5. Уровень _: 

Для подключения к консоли:
http://localhost:8080/h2-console/

### CURL команды для тестирования HelloController

```bash
curl http://localhost:8080/
```

Получение всех пользователей (GET)
```bash
curl -s http://localhost:8080/users | jq .
```

Получение пользователя по ID (GET)
```bash
curl -s http://localhost:8080/users/1 | jq .
```

Получение пользователя по email (GET)
```bash
curl -s "http://localhost:8080/users/email?email=bob@example.com" | jq .
```

Создание пользователя (POST)
```bash
curl -s -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"name": "Karl", "email": "karl@example.com"}' | jq .
```

Удаление пользователя (DELETE)
```bash
curl -X DELETE http://localhost:8080/users/1
```

Обновление пользователя полностью (PUT)
```bash
curl -s -X PUT http://localhost:8080/users/2 \
  -H "Content-Type: application/json" \
  -d '{"name": "Alice Updated", "email": "alice.new@example.com"}' | jq .
```

### References 
1. Ветка с заготовкой spring-data-jpa & CRUD https://github.com/sproshchaev/javarush-university/blob/m5-level-06-spring-data-jpa/m5-level-06/README.md  