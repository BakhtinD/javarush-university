package javarush.example;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RequestBodyExample {
    public static void main(String[] args) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String baseUrl = "https://jsonplaceholder.typicode.com/posts";

        System.out.println("=== 1. POST с телом из строки (JSON) ===");

        // JSON-строка, которую будем отправлять
        String jsonBody = """
            {
                "title": "Пример поста",
                "body": "Это тело поста для демонстрации",
                "userId": 1
            }
            """;

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody)) // Тело из строки
                .build();

        HttpResponse<String> postResponse = client.send(postRequest,
                HttpResponse.BodyHandlers.ofString());
        System.out.println("Статус: " + postResponse.statusCode());
        System.out.println("Ответ: " + postResponse.body());
        System.out.println();

        System.out.println("=== 2. PUT с обновлением данных ===");

        String updatedJson = """
            {
                "id": 1,
                "title": "Обновленный заголовок",
                "body": "Обновленное содержимое",
                "userId": 1
            }
            """;

        HttpRequest putRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/1"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(updatedJson)) // Для PUT тоже нужен BodyPublisher
                .build();

        HttpResponse<String> putResponse = client.send(putRequest,
                HttpResponse.BodyHandlers.ofString());
        System.out.println("Статус: " + putResponse.statusCode());
        System.out.println("Ответ: " + putResponse.body());
        System.out.println();

        System.out.println("=== 3. POST без тела (noBody) ===");

        // Иногда endpoint требует POST, но не принимает данные
        HttpRequest noBodyRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://httpbin.org/post"))
                .POST(HttpRequest.BodyPublishers.noBody()) // Пустое тело
                .build();

        HttpResponse<String> noBodyResponse = client.send(noBodyRequest,
                HttpResponse.BodyHandlers.ofString());
        System.out.println("Статус: " + noBodyResponse.statusCode());
        System.out.println("Ответ содержит информацию о пустом теле: " +
                noBodyResponse.body().contains("\"data\": \"\""));
        System.out.println();

        // Демонстрация других способов (можно показать в комментариях или отдельно)
        System.out.println("=== Другие способы (для демонстрации) ===");

        // Из файла (нужно создать файл предварительно)
        // Path filePath = Paths.get("data.json");
        // HttpRequest fileRequest = HttpRequest.newBuilder()
        //         .uri(URI.create(baseUrl))
        //         .POST(HttpRequest.BodyPublishers.ofFile(filePath))
        //         .build();

        // Из массива байт
        byte[] byteData = "{\"test\": \"byte array\"}".getBytes();
        HttpRequest byteRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://httpbin.org/post"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray(byteData))
                .build();

        HttpResponse<String> byteResponse = client.send(byteRequest,
                HttpResponse.BodyHandlers.ofString());
        System.out.println("Байтовый запрос статус: " + byteResponse.statusCode());
    }
}
