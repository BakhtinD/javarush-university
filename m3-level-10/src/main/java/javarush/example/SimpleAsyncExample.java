package javarush.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class SimpleAsyncExample {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Простые асинхронные запросы ===\n");

        HttpClient client = HttpClient.newHttpClient();

        // 1. Простой асинхронный запрос
        System.out.println("1. Один асинхронный запрос:");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/posts/1"))
                .GET()
                .build();

        // Отправляем асинхронно - не блокирует основной поток
        CompletableFuture<HttpResponse<String>> future = client.sendAsync(
                request,
                HttpResponse.BodyHandlers.ofString());

        System.out.println("   Запрос отправлен, продолжаем работу...");

        // Делаем другую работу пока идёт запрос
        for (int i = 1; i <= 3; i++) {
            System.out.println("   Делаем другую работу " + i + "...");
            Thread.sleep(300);
        }

        // Получаем результат когда он готов
        future.thenAccept(response -> {
            System.out.println("   Ответ получен!");
            System.out.println("   Статус: " + response.statusCode());
            System.out.println("   Тело (начало): " +
                    response.body().substring(0, Math.min(50, response.body().length())) + "...");
        });

        // Ждём завершения
        future.join();

        // 2. Несколько параллельных запросов
        System.out.println("\n2. Параллельные запросы:");

        String[] urls = {
                "https://jsonplaceholder.typicode.com/posts/1",
                "https://jsonplaceholder.typicode.com/posts/2",
                "https://jsonplaceholder.typicode.com/posts/3"
        };

        CompletableFuture<Void>[] futures = new CompletableFuture[urls.length];

        for (int i = 0; i < urls.length; i++) {
            int id = i + 1;
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(urls[i]))
                    .GET()
                    .build();

            futures[i] = client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(resp -> {
                        System.out.println("   Пост " + id + ": статус " + resp.statusCode());
                    })
                    .exceptionally(ex -> {
                        System.out.println("   Пост " + id + ": ошибка - " + ex.getMessage());
                        return null;
                    });
        }

        // Ждём завершения всех запросов
        CompletableFuture.allOf(futures).join();
        System.out.println("   Все запросы завершены!");

        // 3. Цепочка обработки
        System.out.println("\n3. Цепочка обработки:");

        HttpRequest chainRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/posts/1"))
                .GET()
                .build();

        client.sendAsync(chainRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)       // Получаем тело ответа
                .thenApply(String::length)           // Считаем длину
                .thenAccept(length -> {              // Выводим результат
                    System.out.println("   Длина ответа: " + length + " символов");
                })
                .join();

        System.out.println("\n=== Готово! ===");
    }
}