package javarush.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;

public class RedirectExample {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("=== Демонстрация followRedirects() ===\n");

        // Тестовые URL с редиректами
        String redirectUrl = "http://httpbin.org/redirect-to?url=https://httpbin.org/get&status_code=302";
        String secureRedirect = "https://httpbin.org/redirect-to?url=http://httpbin.org/get&status_code=301";
        String doubleRedirect = "http://httpbin.org/redirect/2"; // Двойной редирект

        // ========== 1. NORMAL (по умолчанию, рекомендуемый) ==========
        System.out.println("1. HttpClient.Redirect.NORMAL (рекомендуемый):");
        System.out.println("   - Следует за редиректами");
        System.out.println("   - НЕ следует из HTTPS в HTTP\n");

        HttpClient normalClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        // 1.1 Обычный редирект (HTTP → HTTP или HTTP → HTTPS)
        HttpRequest normalRequest = HttpRequest.newBuilder()
                .uri(URI.create(redirectUrl))
                .GET()
                .build();

        HttpResponse<String> normalResponse = normalClient.send(normalRequest,
                HttpResponse.BodyHandlers.ofString());
        System.out.println("   HTTP → HTTPS редирект:");
        System.out.println("   Финальный статус: " + normalResponse.statusCode());
        System.out.println("   Финальный URL: " + normalResponse.uri());
        System.out.println();

        // 1.2 Безопасный редирект (HTTPS → HTTP) - НЕ БУДЕТ СЛЕДОВАТЬ
        HttpRequest secureRequest = HttpRequest.newBuilder()
                .uri(URI.create(secureRedirect))
                .GET()
                .build();

        try {
            HttpResponse<String> secureResponse = normalClient.send(secureRequest,
                    HttpResponse.BodyHandlers.ofString());
            System.out.println("   HTTPS → HTTP редирект (должен остановиться):");
            System.out.println("   Статус: " + secureResponse.statusCode() + " (должен быть 301)");
            System.out.println("   Заголовок Location: " +
                    secureResponse.headers().firstValue("Location").orElse("нет"));
        } catch (Exception e) {
            System.out.println("   Ошибка: " + e.getClass().getSimpleName());
            System.out.println("   Сообщение: " + e.getMessage());
        }

        // ========== 2. ALWAYS (всегда следовать) ==========
        System.out.println("\n\n2. HttpClient.Redirect.ALWAYS:");
        System.out.println("   - Следует за ВСЕМИ редиректами");
        System.out.println("   - Опасно: может следовать из HTTPS в HTTP\n");

        HttpClient alwaysClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        // 2.1 HTTPS → HTTP редирект - БУДЕТ СЛЕДОВАТЬ (опасно!)
        HttpRequest alwaysRequest = HttpRequest.newBuilder()
                .uri(URI.create(secureRedirect))
                .GET()
                .build();

        HttpResponse<String> alwaysResponse = alwaysClient.send(alwaysRequest,
                HttpResponse.BodyHandlers.ofString());
        System.out.println("   HTTPS → HTTP редирект:");
        System.out.println("   Финальный статус: " + alwaysResponse.statusCode());
        System.out.println("   Финальный URL: " + alwaysResponse.uri());
        System.out.println("   ВНИМАНИЕ: Произошёл переход с HTTPS на HTTP!");

        // ========== 3. NEVER (никогда не следовать) ==========
        System.out.println("\n\n3. HttpClient.Redirect.NEVER:");
        System.out.println("   - НИКОГДА не следует за редиректами");
        System.out.println("   - Полезно для отладки\n");

        HttpClient neverClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();

        HttpRequest neverRequest = HttpRequest.newBuilder()
                .uri(URI.create(redirectUrl))
                .GET()
                .build();

        HttpResponse<String> neverResponse = neverClient.send(neverRequest,
                HttpResponse.BodyHandlers.ofString());
        System.out.println("   Обычный редирект (остановлен):");
        System.out.println("   Статус: " + neverResponse.statusCode() + " (должен быть 302)");
        System.out.println("   Заголовок Location: " +
                neverResponse.headers().firstValue("Location").orElse("нет"));

        // Проверяем длину тела перед substring
        String body = neverResponse.body();
        if (body != null && !body.isEmpty()) {
            int endIndex = Math.min(100, body.length());
            System.out.println("   Тело ответа: " + body.substring(0, endIndex) + "...");
        } else {
            System.out.println("   Тело ответа: (пустое)");
        }

        // ========== 4. Ручная обработка редиректов ==========
        System.out.println("\n\n4. РУЧНАЯ ОБРАБОТКА РЕДИРЕКТОВ:");
        System.out.println("   (когда NEVER недостаточно)\n");

        HttpClient manualClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();

        String currentUrl = "http://httpbin.org/redirect/3"; // Тройной редирект
        int maxRedirects = 5;
        int redirectCount = 0;

        while (redirectCount < maxRedirects) {
            HttpRequest manualRequest = HttpRequest.newBuilder()
                    .uri(URI.create(currentUrl))
                    .GET()
                    .build();

            HttpResponse<String> manualResponse = manualClient.send(manualRequest,
                    HttpResponse.BodyHandlers.ofString());

            int status = manualResponse.statusCode();
            System.out.println("   Шаг " + (redirectCount + 1) + ": " + currentUrl);
            System.out.println("   Статус: " + status);

            if (status == 301 || status == 302) {
                // Получаем новый URL из заголовка Location
                String newLocation = manualResponse.headers()
                        .firstValue("Location")
                        .orElse(null);

                if (newLocation != null) {
                    // Обрабатываем относительные URL
                    if (newLocation.startsWith("/")) {
                        // Относительный путь - конструируем абсолютный URL
                        URI currentUri = URI.create(currentUrl);
                        String base = currentUri.getScheme() + "://" + currentUri.getHost();
                        if (currentUri.getPort() != -1) {
                            base += ":" + currentUri.getPort();
                        }
                        currentUrl = base + newLocation;
                    } else if (!newLocation.startsWith("http://") && !newLocation.startsWith("https://")) {
                        // Другие относительные формы (например, "relative-redirect/2")
                        URI currentUri = URI.create(currentUrl);
                        String path = currentUri.getPath();
                        if (path.contains("/")) {
                            path = path.substring(0, path.lastIndexOf("/") + 1);
                        } else {
                            path = "/";
                        }
                        currentUrl = currentUri.getScheme() + "://" + currentUri.getHost() + path + newLocation;
                    } else {
                        // Абсолютный URL
                        currentUrl = newLocation;
                    }

                    redirectCount++;
                    System.out.println("   → Редирект на: " + newLocation);
                    System.out.println("   Обработанный URL: " + currentUrl + "\n");
                    continue;
                }
            }

            // Если не редирект или Location отсутствует
            System.out.println("   ✓ Финальный ответ получен");
            System.out.println("   Финальный статус: " + status);

            // Проверяем длину тела
            String responseBody = manualResponse.body();
            if (responseBody != null && !responseBody.isEmpty()) {
                int endIndex = Math.min(50, responseBody.length());
                System.out.println("   Тело: " + responseBody.substring(0, endIndex) + "...");
            } else {
                System.out.println("   Тело: (пустое)");
            }
            break;
        }

        if (redirectCount >= maxRedirects) {
            System.out.println("   ⚠️  Превышен лимит редиректов: " + maxRedirects);
        }

        // ========== 5. Бесконечный редирект (опасность) ==========
        System.out.println("\n\n5. ОПАСНОСТЬ: БЕСКОНЕЧНЫЙ РЕДИРЕКТ");
        System.out.println("   (HttpClient защищает от этого)\n");

        // httpbin.org не поддерживает циклические редиректы,
        // но в реальности они возможны
        System.out.println("   HttpClient имеет встроенную защиту:");
        System.out.println("   - Максимум 5 редиректов по умолчанию");
        System.out.println("   - Обнаружение циклов");
        System.out.println("   - Прерывание при превышении лимита");

        // ========== 6. Дополнительная демонстрация с doubleRedirect ==========
        System.out.println("\n\n6. ДВОЙНОЙ РЕДИРЕКТ:");

        HttpClient defaultClient = HttpClient.newHttpClient(); // По умолчанию NORMAL

        HttpRequest doubleRequest = HttpRequest.newBuilder()
                .uri(URI.create(doubleRedirect))
                .GET()
                .build();

        HttpResponse<String> doubleResponse = defaultClient.send(doubleRequest,
                HttpResponse.BodyHandlers.ofString());
        System.out.println("   Исходный URL: " + doubleRedirect);
        System.out.println("   Финальный статус: " + doubleResponse.statusCode());
        System.out.println("   Финальный URL: " + doubleResponse.uri());
        System.out.println("   Количество редиректов: 2 (автоматически обработаны)");

        System.out.println("\n=== Ключевые моменты ===");
        System.out.println("1. NORMAL - безопасный вариант по умолчанию");
        System.out.println("2. NEVER - для отладки и ручной обработки");
        System.out.println("3. ALWAYS - осторожно, может следовать HTTPS→HTTP");
        System.out.println("4. Редиректы настраиваются при создании HttpClient");
        System.out.println("5. HttpClient защищает от бесконечных редиректов");
        System.out.println("6. При статусе 301/302 тело ответа может быть пустым");
        System.out.println("7. В заголовке Location могут быть относительные URL");
    }
}