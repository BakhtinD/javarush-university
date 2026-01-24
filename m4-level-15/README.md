# Модуль 4. Уровень 15 Сохранение иерархии классов

Программная конфигурация (Configuration) Hibernate

### Установка базы данных Sakila

```bash
docker run --name mysql-sakila -e MYSQL_ROOT_PASSWORD=sakila -d -p 3306:3306 restsql/mysql-sakila
```