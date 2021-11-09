package com.geekbrains.io;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Server {

    // коллекция для хранения клиентов
    private final ConcurrentLinkedDeque<ChatHandler> clients;

    // конструктор
    public Server() {
        // при создании экземпляра сервера будет создаваться список клиентов
        clients = new ConcurrentLinkedDeque<>();

        // ServerSocket предоставляет механизм серверной программы для прослушивания клиентов и установления соединений с ними
        // Сервер создает экземпляр объекта ServerSocket, определяющий, по какому номеру порта должна происходить связь
        // чтобы сервер при любых обстоятельствах корректно завершил работу
        try (ServerSocket server = new ServerSocket(8189)) {
            System.out.println("Server started");

            while (true) {
                // Сервер вызывает метод accept() класса ServerSocket.
                // Этот метод ожидает, пока клиент не подключится к серверу по указанному порту
                // На стороне сервера метод accept() возвращает ссылку к новому сокету на сервере, который подключен к клиентскому сокету
                Socket socket = server.accept();
                System.out.println("Client accepted");
                // создать экземпляр ChatHandler и передать ему socket и server
                ChatHandler handler = new ChatHandler(socket, this);
                // добавить клиента в список
                clients.add(handler);
                // отдельный поток для работы handler
                new Thread(handler).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // рассылка сообщений
    public void broadCastMessage(String message) throws Exception {
        // перебрать список клиентов
        for (ChatHandler client : clients) {
            // отправить сообщение каждому клиенту
            client.sendMessage(message);
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
