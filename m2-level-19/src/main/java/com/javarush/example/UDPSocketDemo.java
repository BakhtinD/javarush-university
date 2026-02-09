package com.javarush.example;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDPSocketDemo {

    // UDP Сервер (получатель)
    static class UDPServer {
        private static final int PORT = 8888;
        private static final int BUFFER_SIZE = 1024;

        public static void main(String[] args) throws IOException {

            // Создаем DatagremSocket на порту 8888
            try (DatagramSocket socket = new DatagramSocket(PORT)) {
                System.out.println("Сокет создан на порту: " + socket.getLocalPort());
                System.out.println("Ожидание данных...\n");

                // Буфер для приема данных
                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                // Получаем пакет (блокируется до получения)
                socket.receive(packet);
                System.out.println("Получен пакет от: " + packet.getAddress() + ":" + packet.getPort());
                System.out.println("Размер пакета данных: " + packet.getLength() + " байт");

                // Преобразование данных в строку
                String message = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                System.out.println("Сообщение: " + message);

                // Отправляем ответ
                String response = "Сервер получил: " + message;
                byte[] responseData = response.getBytes(StandardCharsets.UTF_8);
                DatagramPacket responsePacket = new DatagramPacket(
                        responseData, responseData.length,
                        packet.getAddress(), packet.getPort()
                );
                socket.send(responsePacket);
                System.out.println("\nОтвет отправлен клиенту");
            }
        }
    }

    // UDP Клиент (отправитель)
    static class UDPClient {

        private static final int PORT = 8888;
        private static final int BUFFER_SIZE = 1024;
        private static final int TIMEOUT = 5000;

        public static void main(String[] args) throws IOException {

            // Создаем DatagramSocket - система выберет свободный порт
            try (DatagramSocket socket = new DatagramSocket()) {
                System.out.println("Клиентский сокет на порту: " + socket.getLocalPort());

                // Подготовка данных
                String message = "Привет, UDP-сервер!";
                byte[] data = message.getBytes(StandardCharsets.UTF_8);
                InetAddress serverAddress = InetAddress.getByName("localhost");

                // Создаем пакет
                DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, PORT);

                System.out.println("Отправка пакета серверу...");

                // Отправляем пакет (без установки соединения!)
                socket.send(packet);
                System.out.println("Пакет отправлен: " + message);

                // Ждем ответ (не обязательно для UDP)
                System.out.println("\nОжидание ответа...");
                byte[] buffer= new byte[BUFFER_SIZE];
                DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);

                // Устанавливаем таймаут (в UDP нет гарантии получения)
                socket.setSoTimeout(TIMEOUT);

                socket.receive(responsePacket);
                String response = new String(responsePacket.getData(), 0, responsePacket.getLength(), StandardCharsets.UTF_8);
                System.out.println("Ответ от сервера: " + response + "\n");

            }

        }

    }

}
