# Модуль 4. Уровень 10. HQL, Запросы к базе данных

Конфигурация через Properties-файл (hibernate.properties) Hibernate

### Установка базы данных Sakila

```bash
docker run --name mysql-sakila -e MYSQL_ROOT_PASSWORD=sakila -d -p 3306:3306 restsql/mysql-sakila
```