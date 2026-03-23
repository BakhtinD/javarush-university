package javarush.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.io.IOException;

public class AdvancedHttpExample {
    public static void main(String[] args) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String apiUrl = "https://jsonplaceholder.typicode.com/posts/1";

        // 1. Создаём запрос с явным указанием версии, таймаута и заголовков
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .version(HttpClient.Version.HTTP_2)  // Явно указываем HTTP/2
                .timeout(Duration.ofSeconds(5))      // Таймаут 5 секунд
                .header("Content-Type", "application/json")
                .header("X-Custom-Header", "MyValue")
                .header("User-Agent", "JavaHttpClientDemo")
                .GET()
                .build();

        System.out.println("Отправляем запрос с параметрами:");
        System.out.println("- Версия HTTP: " + request.version().orElse(null));
        System.out.println("- Таймаут: " + request.timeout().orElse(null));
        System.out.println("- Заголовки: " + request.headers().map());

        // 2. Отправляем запрос
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("\nОтвет получен:");
            System.out.println("Статус: " + response.statusCode());
            System.out.println("Тело: " + response.body().substring(0, 100) + "...");
        } catch (java.net.http.HttpTimeoutException e) {
            System.out.println("\nОшибка: Превышен таймаут запроса!");
        }

        // 3. Демонстрация timeout в действии (запрос к несуществующему медленному ресурсу)
        System.out.println("\n=== Демонстрация timeout ===");
        HttpRequest slowRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://httpbin.org/delay/10")) // Задержка 10 секунд
                .timeout(Duration.ofSeconds(3))                  // Таймаут 3 секунды
                .GET()
                .build();

        try {
            HttpResponse<String> slowResponse = client.send(slowRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println("Этот код не выполнится, если таймаут сработает");
        } catch (java.net.http.HttpTimeoutException e) {
            System.out.println("Таймаут сработал как ожидалось!");
        }
    }
}