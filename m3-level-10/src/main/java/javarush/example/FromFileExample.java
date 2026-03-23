package javarush.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Files;

public class FromFileExample {
    public static void main(String[] args) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        // Для демонстрации создадим тестовые файлы разных типов

        System.out.println("=== 1. Подготовка тестовых файлов ===");

        // 1.1 Создаём текстовый файл
        Path textFile = Paths.get("test_document.txt");
        Files.writeString(textFile, "Это содержимое текстового файла.\nСтрока 2.\nСтрока 3.");
        System.out.println("Создан текстовый файл: " + textFile.toAbsolutePath());
        System.out.println("Размер: " + Files.size(textFile) + " байт");

        // 1.2 Создаём простой JSON файл
        Path jsonFile = Paths.get("data.json");
        Files.writeString(jsonFile, """  
            {                "name": "Тестовый пользователь",                "email": "test@example.com",                "age": 25            }            """);
        System.out.println("Создан JSON файл: " + jsonFile.toAbsolutePath());

        System.out.println("\n=== 2. Отправка текстового файла ===");

        HttpRequest textRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://httpbin.org/post"))
                .header("Content-Type", "text/plain; charset=UTF-8")
                .header("X-Filename", textFile.getFileName().toString())
                .POST(HttpRequest.BodyPublishers.ofFile(textFile))
                .build();

        HttpResponse<String> textResponse = client.send(textRequest,
                HttpResponse.BodyHandlers.ofString());
        System.out.println("Статус: " + textResponse.statusCode());
        System.out.println("Текстовый файл отправлен успешно: " +
                textResponse.body().contains("Это содержимое текстового файла"));

        System.out.println("\n=== 3. Отправка JSON файла ===");

        HttpRequest jsonRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://httpbin.org/post"))
                .header("Content-Type", "application/json")
                .header("X-Filename", jsonFile.getFileName().toString())
                .POST(HttpRequest.BodyPublishers.ofFile(jsonFile))
                .build();

        HttpResponse<String> jsonResponse = client.send(jsonRequest,
                HttpResponse.BodyHandlers.ofString());
        System.out.println("Статус: " + jsonResponse.statusCode());
        System.out.println("JSON файл отправлен успешно: " +
                jsonResponse.body().contains("\"Тестовый пользователь\""));

        System.out.println("\n=== 4. Отправка бинарного файла (имитация) ===");

        // 4.1 Создаём бинарный файл (просто массив байт)
        Path binaryFile = Paths.get("test.bin");
        byte[] binaryData = new byte[100];
        for (int i = 0; i < binaryData.length; i++) {
            binaryData[i] = (byte) i;
        }
        Files.write(binaryFile, binaryData);
        System.out.println("Создан бинарный файл: " + binaryFile.toAbsolutePath());
        System.out.println("Размер: " + Files.size(binaryFile) + " байт");

        HttpRequest binaryRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://httpbin.org/post"))
                .header("Content-Type", "application/octet-stream") // Универсальный тип для бинарных данных
                .header("X-Filename", binaryFile.getFileName().toString())
                .POST(HttpRequest.BodyPublishers.ofFile(binaryFile))
                .build();

        HttpResponse<String> binaryResponse = client.send(binaryRequest,
                HttpResponse.BodyHandlers.ofString());
        System.out.println("Статус: " + binaryResponse.statusCode());
        System.out.println("Бинарный файл отправлен: " +
                binaryResponse.body().contains("\"data\":"));

        System.out.println("\n=== 5. Проверка обработки ошибок ===");

        // Пытаемся отправить несуществующий файл
        Path nonExistentFile = Paths.get("non_existent_file.txt");

        try {
            HttpRequest errorRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://httpbin.org/post"))
                    .POST(HttpRequest.BodyPublishers.ofFile(nonExistentFile))
                    .build();

            // Этот код не выполнится, так как fromFile() проверит существование файла
            System.out.println("Файл найден (это сообщение не должно появиться)");
        } catch (Exception e) {
            System.out.println("Ожидаемая ошибка: " + e.getClass().getSimpleName());
            System.out.println("Сообщение: " + e.getMessage());
        }

        System.out.println("\n=== 6. Очистка тестовых файлов ===");
        Files.deleteIfExists(textFile);
        Files.deleteIfExists(jsonFile);
        Files.deleteIfExists(binaryFile);
        System.out.println("Тестовые файлы удалены");

        System.out.println("\n=== Ключевые моменты ===");
        System.out.println("1. fromFile() автоматически определяет размер файла");
        System.out.println("2. Content-Type должен соответствовать типу файла");
        System.out.println("3. Для бинарных файлов используем application/octet-stream");
        System.out.println("4. Файл должен существовать и быть доступен для чтения");
    }
}