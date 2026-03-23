package javarush.example;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.io.IOException;

public class HttpMethodsExample {
    public static void main(String[] args) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String baseUrl = "https://jsonplaceholder.typicode.com/posts";

        // 1. GET-запрос — получить данные
        System.out.println("=== GET-запрос ===");
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/1"))
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println("GET ответ: " + getResponse.body());

        // 2. POST-запрос — создать новый ресурс
        System.out.println("\n=== POST-запрос ===");
        String postBody = "{\"title\": \"foo\", \"body\": \"bar\", \"userId\": 1}";
        // "{
        //   "title":  "foo",
        //   "body":   "bar",
        //   "userId": 1
        //   }"
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(postBody))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println("POST ответ: " + postResponse.body());

        // 3. PUT-запрос — обновить ресурс
        System.out.println("\n=== PUT-запрос ===");
        String putBody = "{\"id\": 1, \"title\": \"updated\", \"body\": \"updated body\", \"userId\": 1}";
        HttpRequest putRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/1"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(putBody))
                .build();
        HttpResponse<String> putResponse = client.send(putRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println("PUT ответ: " + putResponse.body());

        // 4. DELETE-запрос — удалить ресурс
        System.out.println("\n=== DELETE-запрос ===");
        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/1"))
                .DELETE()
                .build();
        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println("DELETE статус: " + deleteResponse.statusCode());
        System.out.println("DELETE ответ: " + deleteResponse.body());
    }
}