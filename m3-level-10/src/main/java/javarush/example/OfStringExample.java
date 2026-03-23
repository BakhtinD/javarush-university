package javarush.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

public class OfStringExample {

    public static void main(String[] args) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();

        System.out.println("=== 1. Простой текст (text/plain) ===");

        // Текстовое тело с русскими символами
        String textBody = "Привет, это тестовое сообщение с русскими буквами!";

        HttpRequest textRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://httpbin.org/post"))
                .header("Content-Type", "text/plain;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(textBody))
                .build();

        HttpResponse<String> textResponse = client.send(textRequest,
                HttpResponse.BodyHandlers.ofString());
        System.out.println("Статус: " + textResponse.statusCode());
        // Проверим, что сервер получил наш текст
        System.out.println("Текст в ответе: " +
                textResponse.body().contains("\"Привет, это тестовое\""));
        System.out.println();

        System.out.println("=== 2. JSON данные (application/json) ===");

        // JSON с русским текстом
        String jsonBody = """  
            {                "title": "Заголовок на русском",                "body": "Содержимое поста с русскими символами: привет, мир!",                "userId": 123,                "tags": ["тест", "демо"]            }            """;

        HttpRequest jsonRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/posts"))
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> jsonResponse = client.send(jsonRequest,
                HttpResponse.BodyHandlers.ofString());
        System.out.println("Статус: " + jsonResponse.statusCode());
        System.out.println("Ответ: " + jsonResponse.body());
        System.out.println();

        System.out.println("=== 3. Форма (application/x-www-form-urlencoded) ===");

        // Данные формы (логин/пароль например)
        String formData = "username=vasya&password=secret123&remember=true";

        HttpRequest formRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://httpbin.org/post"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();

        HttpResponse<String> formResponse = client.send(formRequest,
                HttpResponse.BodyHandlers.ofString());
        System.out.println("Статус: " + formResponse.statusCode());
        System.out.println("Форма отправлена успешно: " +
                formResponse.body().contains("\"username\": \"vasya\""));
        System.out.println();

        System.out.println("=== 4. Явное указание кодировки ===");

        // Демонстрация явного указания кодировки
        String latinText = "Hello world with special chars: áéíóú";

        HttpRequest latinRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://httpbin.org/post"))
                .header("Content-Type", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofString(latinText, StandardCharsets.ISO_8859_1))
                .build();

        HttpResponse<String> latinResponse = client.send(latinRequest,
                HttpResponse.BodyHandlers.ofString());
        System.out.println("Статус: " + latinResponse.statusCode());
        System.out.println("Латиница отправлена: " +
                latinResponse.body().contains("Hello world"));

        System.out.println("\n=== Ключевые моменты ===");
        System.out.println("1. ofString() по умолчанию использует UTF-8");
        System.out.println("2. Content-Type должен соответствовать типу данных");
        System.out.println("3. Русский текст работает без проблем с UTF-8");
        System.out.println("4. Можно явно указать кодировку вторым параметром");
    }
}