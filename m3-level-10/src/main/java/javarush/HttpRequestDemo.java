package javarush;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;

public class HttpRequestDemo {

    public static void main(String[] args) {

        // Создание Get-запроса без тела
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode/posts/1"))
                .GET()
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json")
                .header("User-Agent", "Java-HttpClient-Demo")
                .build();

        System.out.println("URI: " + getRequest.uri());
        System.out.println("Метод: " + getRequest.method());
        System.out.println("Таймаут: " + getRequest.timeout().orElse(Duration.ZERO).getSeconds() + " сек");

        // Создание POST-запроса с телом (заглушка)
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode/posts"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();

        System.out.println("URI: " + getRequest.uri());
        System.out.println("Метод: " + getRequest.method());

    }

}
