package javarush.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class HttpClientBodyHandlersDemo {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("=== HttpClient BodyHandlers Демонстрация ===\n");

        // Создаём HttpClient с настройками
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)  // Исправлено: HTTP_1_1, а не HTP_1.1
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        String apiUrl = "https://jsonplaceholder.typicode.com";

        // ========== 1. Синхронный запрос с разными BodyHandlers ==========

        System.out.println("1. СИНХРОННЫЕ ЗАПРОСЫ:");

        // 1.1 ofString() - самый частый случай
        System.out.println("\na) BodyHandlers.ofString():");
        HttpRequest stringRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/posts/1"))
                .GET()
                .build();

        HttpResponse<String> stringResponse = client.send(stringRequest,
                HttpResponse.BodyHandlers.ofString());
        System.out.println("Статус: " + stringResponse.statusCode());
        System.out.println("Тело (первые 100 символов): " +
                stringResponse.body().substring(0, Math.min(100, stringResponse.body().length())) + "...");

        // 1.2 ofByteArray() - для бинарных данных
        System.out.println("\nb) BodyHandlers.ofByteArray():");
        HttpRequest bytesRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/posts/1"))
                .GET()
                .build();

        HttpResponse<byte[]> bytesResponse = client.send(bytesRequest,
                HttpResponse.BodyHandlers.ofByteArray());
        System.out.println("Статус: " + bytesResponse.statusCode());
        System.out.println("Размер тела: " + bytesResponse.body().length + " байт");

        // 1.3 ofFile() - сохранение в файл
        System.out.println("\nc) BodyHandlers.ofFile():");
        Path tempFile = Paths.get("response_data.json");

        HttpRequest fileRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/posts/1"))
                .GET()
                .build();

        HttpResponse<Path> fileResponse = client.send(fileRequest,
                HttpResponse.BodyHandlers.ofFile(tempFile));
        System.out.println("Статус: " + fileResponse.statusCode());
        System.out.println("Файл сохранен: " + fileResponse.body().toAbsolutePath());
        System.out.println("Размер файла: " + Files.size(tempFile) + " байт");

        // 1.4 discarding() - игнорируем тело
        System.out.println("\nd) BodyHandlers.discarding():");
        HttpRequest discardRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/posts/1"))
                .GET()
                .build();

        HttpResponse<Void> discardResponse = client.send(discardRequest,
                HttpResponse.BodyHandlers.discarding());
        System.out.println("Статус: " + discardResponse.statusCode());
        System.out.println("Тело игнорировано, тип ответа: " + discardResponse.body());

        // 1.5 ofLines() - обработка как потока строк
        System.out.println("\ne) BodyHandlers.ofLines():");
        HttpRequest linesRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://httpbin.org/stream/3")) // Поток JSON строк
                .GET()
                .build();

        HttpResponse<Stream<String>> linesResponse = client.send(linesRequest,
                HttpResponse.BodyHandlers.ofLines());
        System.out.println("Статус: " + linesResponse.statusCode());
        System.out.println("Первые 2 строки из потока:");
        linesResponse.body().limit(2).forEach(System.out::println);

        // ========== 2. Асинхронный запрос ==========

        System.out.println("\n\n2. АСИНХРОННЫЙ ЗАПРОС (sendAsync):");

        HttpRequest asyncRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/posts/2"))
                .GET()
                .build();

        CompletableFuture<HttpResponse<String>> future = client.sendAsync(
                asyncRequest,
                HttpResponse.BodyHandlers.ofString());

        System.out.println("Запрос отправлен асинхронно...");

        // Обработка результата
        future.thenAccept(response -> {
            System.out.println("Асинхронный ответ получен:");
            System.out.println("Статус: " + response.statusCode());
            System.out.println("Заголовки: " + response.headers().map());
        });

        // Ждём завершения асинхронного запроса
        future.join();

        // ========== 3. Обработка ошибок ==========

        System.out.println("\n\n3. ОБРАБОТКА ОШИБОК:");

        // 3.1 Запрос к несуществующему ресурсу
        HttpRequest errorRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/nonexistent"))
                .GET()
                .build();

        try {
            HttpResponse<String> errorResponse = client.send(errorRequest,
                    HttpResponse.BodyHandlers.ofString());
            System.out.println("Статус 404: " + errorResponse.statusCode());
        } catch (Exception e) {
            System.out.println("Ошибка при запросе: " + e.getClass().getSimpleName());
        }

        // 3.2 Запрос с таймаутом
        HttpRequest timeoutRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://httpbin.org/delay/10")) // Задержка 10 сек
                .timeout(java.time.Duration.ofSeconds(2))
                .GET()
                .build();

        try {
            HttpResponse<String> timeoutResponse = client.send(timeoutRequest,
                    HttpResponse.BodyHandlers.ofString());
        } catch (java.net.http.HttpTimeoutException e) {
            System.out.println("Таймаут сработал как ожидалось: " + e.getMessage());
        }

        // ========== 4. Очистка ==========

        System.out.println("\n\n4. ОЧИСТКА:");
        Files.deleteIfExists(tempFile);
        System.out.println("Временный файл удален");

        System.out.println("\n=== Ключевые моменты ===");
        System.out.println("1. BodyHandlers.ofString() - для текстовых ответов");
        System.out.println("2. BodyHandlers.ofFile() - для сохранения в файл");
        System.out.println("3. BodyHandlers.discarding() - когда тело не нужно");
        System.out.println("4. send() - синхронный, блокирующий");
        System.out.println("5. sendAsync() - асинхронный, возвращает CompletableFuture");
        System.out.println("6. Всегда проверяйте statusCode()");
    }
}