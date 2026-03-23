package javarush.example;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

public class CookieExample {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Работа с куками в HttpClient ===\n");

        // ========== 1. Создаём CookieManager ==========
        System.out.println("1. СОЗДАНИЕ COOKIE MANAGER:");

        CookieManager cookieManager = new CookieManager();

        // Настраиваем политику (принимать все куки)
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        System.out.println("   CookieManager создан с политикой ACCEPT_ALL");

        // ========== 2. Создаём HttpClient с CookieHandler ==========
        System.out.println("\n2. СОЗДАНИЕ HTTPCLIENT С COOKIE HANDLER:");

        HttpClient client = HttpClient.newBuilder()
                .cookieHandler(cookieManager)  // Подключаем обработчик куков
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        System.out.println("   HttpClient создан с CookieHandler");

        // ========== 3. Запрос, который устанавливает куки ==========
        System.out.println("\n3. ЗАПРОС, КОТОРЫЙ УСТАНАВЛИВАЕТ КУКИ:");

        // httpbin.org - тестовый сервер, который может устанавливать куки
        String setCookieUrl = "https://httpbin.org/cookies/set?name=John&session=abc123";

        HttpRequest setRequest = HttpRequest.newBuilder()
                .uri(URI.create(setCookieUrl))
                .GET()
                .build();

        HttpResponse<String> setResponse = client.send(setRequest, BodyHandlers.ofString());
        System.out.println("   Статус: " + setResponse.statusCode());

        // ========== 4. Проверяем сохранённые куки ==========
        System.out.println("\n4. ПРОВЕРЯЕМ СОХРАНЁННЫЕ КУКИ:");

        CookieStore cookieStore = cookieManager.getCookieStore();
        List<HttpCookie> cookies = cookieStore.getCookies();

        if (cookies.isEmpty()) {
            System.out.println("   Куки не найдены");
        } else {
            System.out.println("   Найдено куков: " + cookies.size());
            for (HttpCookie cookie : cookies) {
                System.out.println("   - " + cookie.getName() + " = " + cookie.getValue());
                System.out.println("     Домен: " + cookie.getDomain());
                System.out.println("     Путь: " + cookie.getPath());
                System.out.println("     Срок жизни: " +
                        (cookie.getMaxAge() == -1 ? "сессия" : cookie.getMaxAge() + "сек"));
            }
        }

        // ========== 5. Запрос, который отправляет куки обратно ==========
        System.out.println("\n5. ЗАПРОС, КОТОРЫЙ ОТПРАВЛЯЕТ КУКИ:");

        String getCookieUrl = "https://httpbin.org/cookies";
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(getCookieUrl))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, BodyHandlers.ofString());
        System.out.println("   Статус: " + getResponse.statusCode());
        System.out.println("   Ответ сервера:");
        System.out.println("   " + getResponse.body());

        // ========== 6. Ручное добавление куков ==========
        System.out.println("\n6. РУЧНОЕ ДОБАВЛЕНИЕ КУКОВ:");

        // Создаём новую куку
        HttpCookie manualCookie = new HttpCookie("manual", "testValue");
        manualCookie.setDomain(".httpbin.org");
        manualCookie.setPath("/");
        manualCookie.setMaxAge(3600); // 1 час

        // Добавляем в хранилище
        cookieStore.add(URI.create("https://httpbin.org"), manualCookie);
        System.out.println("   Добавлена кука: " + manualCookie.getName() + " = " + manualCookie.getValue());

        // Проверяем все куки
        System.out.println("   Теперь всего куков: " + cookieStore.getCookies().size());

        // ========== 7. Запрос с ручными куками ==========
        System.out.println("\n7. ЗАПРОС С РУЧНЫМИ КУКАМИ:");

        HttpRequest manualRequest = HttpRequest.newBuilder()
                .uri(URI.create(getCookieUrl))
                .GET()
                .build();

        HttpResponse<String> manualResponse = client.send(manualRequest, BodyHandlers.ofString());
        System.out.println("   Ответ сервера (должны быть все 3 куки):");
        System.out.println("   " + manualResponse.body());

        // ========== 8. Удаление куков ==========
        System.out.println("\n8. УДАЛЕНИЕ КУКОВ:");

        // Удаляем конкретную куку
        boolean removed = cookieStore.remove(URI.create("https://httpbin.org"), manualCookie);
        System.out.println("   Кука 'manual' удалена: " + removed);

        // Удаляем все куки
        cookieStore.removeAll();
        System.out.println("   Все куки удалены");
        System.out.println("   Осталось куков: " + cookieStore.getCookies().size());

        // ========== 9. Разные политики куков ==========
        System.out.println("\n9. ПОЛИТИКИ COOKIE MANAGER:");

        System.out.println("   a) ACCEPT_ALL - принимать все куки");
        System.out.println("   b) ACCEPT_NONE - не принимать куки");
        System.out.println("   c) ACCEPT_ORIGINAL_SERVER - только с исходного сервера");

        // Пример с другой политикой
        CookieManager strictManager = new CookieManager();
        strictManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        System.out.println("\n   Создан CookieManager с ACCEPT_ORIGINAL_SERVER");

        // ========== 10. Создание сессии ==========
        System.out.println("\n10. СОЗДАНИЕ СЕССИИ С КУКАМИ:");

        // Имитация логина с получением session cookie
        String loginUrl = "https://httpbin.org/cookies/set/session_id/xyz789";

        HttpClient sessionClient = HttpClient.newBuilder()
                .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
                .build();

        HttpRequest loginRequest = HttpRequest.newBuilder()
                .uri(URI.create(loginUrl))
                .GET()
                .build();

        // Первый запрос - получаем сессионную куку
        sessionClient.send(loginRequest, BodyHandlers.discarding());
        System.out.println("   Сессия создана (кука установлена)");

        // Второй запрос - используем сессию
        HttpRequest sessionRequest = HttpRequest.newBuilder()
                .uri(URI.create(getCookieUrl))
                .GET()
                .build();

        HttpResponse<String> sessionResponse = sessionClient.send(sessionRequest, BodyHandlers.ofString());
        System.out.println("   Ответ с сессионной кукой:");
        System.out.println("   " + sessionResponse.body());

        System.out.println("\n=== Ключевые моменты ===");
        System.out.println("1. CookieManager автоматически управляет куками");
        System.out.println("2. HttpClient.Builder.cookieHandler() - настройка");
        System.out.println("3. Куки сохраняются между запросами");
        System.out.println("4. Можно добавлять куки вручную через CookieStore");
        System.out.println("5. Политики: ACCEPT_ALL, ACCEPT_NONE, ACCEPT_ORIGINAL_SERVER");
        System.out.println("6. Идеально для аутентификации и сессий");
    }
}