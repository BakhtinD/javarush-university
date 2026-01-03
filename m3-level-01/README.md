

```angular2html
# 1. Компиляция проекта
mvn compile

# 2. Запуск тестов (если будут)
mvn test

# 3. Создание обычного JAR
mvn package
# Результат: target/m3-level-01-1.0-SNAPSHOT.jar

# 4. Создание FAT JAR (со всеми зависимостями)
# Уже настроено в assembly plugin, создастся при mvn package
# Результат: target/m3-level-01-1.0-SNAPSHOT-jar-with-dependencies.jar

# 5. Запуск приложения через exec plugin
mvn exec:java
mvn exec:java -Dexec.args="параметр1 параметр2"

# 6. Запуск FAT JAR
java -jar target/m3-level-01-1.0-SNAPSHOT-jar-with-dependencies.jar

# 7. Просмотр эффективного POM (со всеми унаследованными настройками)
mvn help:effective-pom

# 8. Очистка и полная пересборка
mvn clean package

# 9. Работа с профилями
mvn clean package -P prod  # Использовать prod-профиль
mvn clean package          # Использовать dev-профиль (по умолчанию)

# 10. Генерация сайта с документацией
mvn site
```