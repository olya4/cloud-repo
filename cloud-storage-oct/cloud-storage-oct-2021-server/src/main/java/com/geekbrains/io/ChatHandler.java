package com.geekbrains.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatHandler implements Runnable {
    // папка root (находится в основном модуле cloud-storage-oct-2021 )
    private final Path root;
    // в папку clientDir будут складываться файлы, полученные от клиента
    private Path clientDir;

    // буфер
    private byte[] buffer;
    // размер буфера
    private static final int BUFFER_SIZE = 1024;

    // логин
    private static int counter = 0;
    private final String userName;

    // для форматирования даты
    private final SimpleDateFormat format;

    // подключение клиента
//    private final Socket socket;

    // информация о всех подключенных клиентах
    private final Server server;

    // для входящего потока
    private final DataInputStream dis;
    // для исходящего потока
    private final DataOutputStream dos;

    // когда соединение установлено, сервер создает объект сокета на своем конце связи
    public ChatHandler(Socket socket, Server server) throws Exception {
        // создание буфера
        buffer = new byte[BUFFER_SIZE];

        // название папки
        root = Paths.get("server_root");
        // если такой папки нет
        if (!Files.exists(root)) {
            // создать папку
            Files.createDirectory(root);
        }

//        this.socket = socket;
        this.server = server;
        // при каждом создании подключения(экземпляра класса) увеличивать счетчик
        counter++;
        userName = "User_" + counter;
        // название папки
        clientDir = root.resolve(userName);

        // если такой папки нет
        if (!Files.exists(clientDir)) {
            // создать папку
            Files.createDirectory(clientDir);
        }

        // выводит дату и время
        format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        // try/catch на весь цикл, если соединение прервалось - выйти из цикла
        try {
            // бесконечный цикл
            while (true) {
//                // прочитать сообщение
//                String msg = dis.readUTF();
//                // сервер отправит прочитанное сообщение клиентам
//                // getMessage(msg), чтоб отображалось имя клиента, который написал сообщение
//                server.broadCastMessage(getMessage(msg));

                // прием файла
                // имя файла
                String fileName = dis.readUTF();
                // размер файла
                long size = dis.readLong();
                // записать файл
                Path path = clientDir.resolve(fileName);

//                // InputStream = dis
//                // Path = path
//                // CopyOption = StandardCopyOption.REPLACE_EXISTING
//                Files.copy(dis, path, StandardCopyOption.REPLACE_EXISTING);

                try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
                    // получить байты из файла
                    for (int i = 0; i < (size + BUFFER_SIZE - 1) / BUFFER_SIZE; i++) {
                        // из входящего потока прочитать буфер
                        int read = dis.read(buffer);
                        // из буфера байты достаются не подному, а по несколько за раз
                        // прочитаное из буфера записать в файл от 0 до количества прочитанного
                       fos.write(buffer, 0, read);
                    }
                }
                // сообщение клиенту, что файл получен
                responseOk();
            }
        } catch (Exception e) {
            System.err.println("Connection was broken");
            e.printStackTrace();
        }
    }

    // прикрепить время к сообщению
    public String getMessage(String msg) {
        // текущее время + [имя пользователя] + его сообщение
        return getTime() + "[" + userName + "]: " + msg;
    }

    // временная метка
    public String getTime() {
        // форматирование даты
        return format.format(new Date());
    }

    // сообщение клиенту, что файл получен
    private void responseOk() throws Exception {
        // отправить сообщение
        dos.writeUTF("File received");
        // очистить внутренние буферы и принудительно записать (т.е. очистить) все ожидающие данные в назначение потока
        dos.flush();
    }

    // отправить сообщение клиенту
    public void sendMessage(String msg) throws Exception {
        // отправить сообщение
        dos.writeUTF(msg);
        // очистить внутренние буферы и принудительно записать (т.е. очистить) все ожидающие данные в назначение потока
        dos.flush();
    }
}
