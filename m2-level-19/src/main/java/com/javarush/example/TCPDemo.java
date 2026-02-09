package com.javarush.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPDemo {

    // TCP-сервер
    static class SimpleTCPServer {
        private static final int SERVER_PORT = 12345;

        public static void main(String[] args) throws IOException {

            // Создаем серверный сокет на порту 12345
            try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
                System.out.println("Ожидание подключения клиента...");

                // Принять соединение
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    System.out.println("Клиент подключен: " + clientSocket.getInetAddress());

                    // Чтение сообщения от клиента
                    String message = in.readLine();
                    System.out.println("Получено от клиента " + message);

                    // Отправляем подтверждение
                    out.println("Сервер получил: " + message);
                    System.out.println("Ответ отправлен клиенту");
                }
            }
            System.out.println("Сервер завершил работу");
        }
    }

    // TCP-клиент
    static class SimpleTCPClient {
        private static final String SERVER_HOST = "localhost";
        private static final int SERVER_PORT = 12345;

        public static void main(String[] args) throws IOException {

            System.out.println("Клиент запущен");
            // Подключение к серверу localhost:12345
            try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                System.out.println("Подключение с Сервером установлено");

                // Отправляем сообщение
                String message = "Привет, Сервер!";
                out.println(message);
                System.out.println("Отправлено серверу: " + message);

                // Получаем ответ
                String response = in.readLine();
                System.out.println("Получено от сервера: " + response);
            }
            System.out.println("Клиент завершил работу");
        }
    }


}
