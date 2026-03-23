package javarush.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;
import java.util.Base64;

public class ByteArrayAndInputStreamExample {
    public static void main(String[] args) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        System.out.println("=== 1. ofByteArray() - отправка текста как байтов ===");

        // Текст как массив байт
        String text = "Привет, это тестовое сообщение!";
        byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);

        HttpRequest byteArrayRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://httpbin.org/post"))
                .header("Content-Type", "text/plain; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofByteArray(textBytes))
                .build();

        HttpResponse<String> byteArrayResponse = client.send(byteArrayRequest,
                HttpResponse.BodyHandlers.ofString());
        System.out.println("Статус: " + byteArrayResponse.statusCode());
        System.out.println("Байты отправлены: " +
                byteArrayResponse.body().contains("Привет, это тестовое"));

        System.out.println("\n=== 2. ofByteArray() - отправка сериализованных данных ===");

        // Имитация сериализации объекта
        String userData = "user:ivan;role:admin;lastLogin:2024-01-01";
        byte[] serializedData = Base64.getEncoder().encode(userData.getBytes());

        HttpRequest serializedRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://httpbin.org/post"))
                .header("Content-Type", "application/octet-stream")
                .header("X-Data-Format", "base64-serialized")
                .POST(HttpRequest.BodyPublishers.ofByteArray(serializedData))
                .build();

        HttpResponse<String> serializedResponse = client.send(serializedRequest,
                HttpResponse.BodyHandlers.ofString());
        System.out.println("Статус: " + serializedResponse.statusCode());
        System.out.println("Base64 данные отправлены");

        System.out.println("\n=== 3. ofByteArray() - отправка шифрованных данных ===");

        // Простая "шифровка" - XOR каждого байта
        String secret = "Секретное сообщение";
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedBytes = new byte[secretBytes.length];

        byte key = 0x55; // Простой XOR ключ
        for (int i = 0; i < secretBytes.length; i++) {
            encryptedBytes[i] = (byte) (secretBytes[i] ^ key);
        }

        HttpRequest encryptedRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://httpbin.org/post"))
                .header("Content-Type", "application/octet-stream")
                .header("X-Encryption", "simple-xor")
                .POST(HttpRequest.BodyPublishers.ofByteArray(encryptedBytes))
                .build();

        HttpResponse<String> encryptedResponse = client.send(encryptedRequest,
                HttpResponse.BodyHandlers.ofString());
        System.out.println("Статус: " + encryptedResponse.statusCode());
        System.out.println("Шифрованные данные отправлены");

        System.out.println("\n=== 4. ofInputStream() - отправка данных из файла ===");

        // Создаём временный файл
        File tempFile = File.createTempFile("test", ".txt");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("Строка 1 из временного файла\n");
            writer.write("Строка 2 из временного файла\n");
            writer.write("Строка 3 из временного файла\n");
        }

        HttpRequest fileStreamRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://httpbin.org/post"))
                .header("Content-Type", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofInputStream(() -> {
                    try {
                        return new FileInputStream(tempFile);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }))
                .build();

        HttpResponse<String> fileStreamResponse = client.send(fileStreamRequest,
                HttpResponse.BodyHandlers.ofString());
        System.out.println("Статус: " + fileStreamResponse.statusCode());
        System.out.println("Файл через InputStream отправлен: " +
                fileStreamResponse.body().contains("Строка 1"));

        System.out.println("\n=== 5. ofInputStream() - генерация данных на лету ===");

        // Генерируем данные в потоке (без сохранения в памяти)
        HttpRequest generatedRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://httpbin.org/post"))
                .header("Content-Type", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofInputStream(() -> {
                    // Лямбда создает InputStream с генерируемыми данными
                    return new ByteArrayInputStream(
                            ("Генерируемые данные:\n" +
                                    "Текущее время: " + System.currentTimeMillis() + "\n" +
                                    "Случайное число: " + Math.random() + "\n" +
                                    "Еще одна строка").getBytes()
                    );
                }))
                .build();

        HttpResponse<String> generatedResponse = client.send(generatedRequest,
                HttpResponse.BodyHandlers.ofString());
        System.out.println("Статус: " + generatedResponse.statusCode());
        System.out.println("Сгенерированные данные отправлены");

        System.out.println("\n=== 6. ofInputStream() - стриминг сжатых данных ===");

        // Создаем GZIP поток на лету
        HttpRequest compressedRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://httpbin.org/post"))
                .header("Content-Type", "application/octet-stream")
                .header("Content-Encoding", "gzip")
                .POST(HttpRequest.BodyPublishers.ofInputStream(() -> {
                    try {
                        PipedInputStream in = new PipedInputStream();
                        PipedOutputStream out = new PipedOutputStream(in);

                        // Запускаем отдельный поток для записи сжатых данных
                        new Thread(() -> {
                            try (GZIPOutputStream gzip = new GZIPOutputStream(out);
                                 Writer writer = new OutputStreamWriter(gzip, StandardCharsets.UTF_8)) {

                                writer.write("Сжатые данные:\n");
                                for (int i = 1; i <= 100; i++) {
                                    writer.write("Строка " + i + " для сжатия\n");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }).start();

                        return in;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }))
                .build();

        // Этот запрос может не работать с httpbin.org из-за сжатия,
        // но демонстрирует принцип
        System.out.println("Запрос со сжатием подготовлен (может не работать с тестовым сервером)");

        System.out.println("\n=== Очистка ===");
        tempFile.delete();
        System.out.println("Временный файл удален");

        System.out.println("\n=== Ключевые моменты ===");
        System.out.println("1. ofByteArray() - для данных уже в памяти");
        System.out.println("2. ofInputStream() - для потоковой передачи");
        System.out.println("3. InputStream создается лямбдой (Supplier)");
        System.out.println("4. Поток автоматически закрывается после отправки");
        System.out.println("5. Для больших данных используйте ofInputStream()");
    }
}