package javarush.example;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.io.IOException;

public class UriExample {
    public static void main(String[] args) throws IOException, InterruptedException {
        // Способ 1: URI.create() — предпочтительный
        URI uri1 = URI.create("https://jsonplaceholder.typicode.com/posts/1");

        // Способ 2: new URI() — требует обработки исключения
        URI uri2 = null;
        try {
            uri2 = new URI("https://jsonplaceholder.typicode.com/posts/2");
        } catch (Exception e) {
            System.out.println("Ошибка в URI: " + e.getMessage());
        }

        // Создаём HTTP-запрос с помощью uri()
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri1)
                .GET()
                .build();

        // Отправляем запрос и выводим ответ
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Статус код: " + response.statusCode());
        System.out.println("Тело ответа: " + response.body());
    }
}