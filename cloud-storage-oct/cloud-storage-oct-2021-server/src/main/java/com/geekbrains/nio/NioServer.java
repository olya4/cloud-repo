package com.geekbrains.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.Set;

public class NioServer {

    private ServerSocketChannel server;
    private Selector selector;
    private ByteBuffer buffer;

    private Path path;
    private Path file;

    public NioServer() throws Exception {
        path = Paths.get("root");
        file = Paths.get("root", "1.txt");
        // размер буфера
        buffer = buffer.allocate(256);
        // сервер сокет канал
        server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(8189));
        // селектор - позволяет одному потоку обрабатывать несколько каналов
        // собирает события со всех каналов в очередь
        // и последовательно обходить эту очередь
        selector = Selector.open();
        // сервер работает не в блокирующем режиме
        server.configureBlocking(false);
        // сервер делает только accept()
        // ожидает, пока клиент не подключится к серверу по указанному порту
        server.register(selector, SelectionKey.OP_ACCEPT);

        while (server.isOpen()) {
            selector.select();
            // собралась коллекция событий
            // SelectionKey = событие
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            // итератор
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            // сообщения будут обрабатываться поочередно, а не паралельно

            // обойти коллекцию
            while (iterator.hasNext()) {
                // после прохождения ключа
                SelectionKey key = iterator.next();
                // если это ключ подключения
                if (key.isAcceptable()) {
                    handAccept(key);
                }
                // если это ключ на чтение
                if (key.isReadable()) {
                    handRead(key);
                }
                // он будет удален из коллекции
                iterator.remove();
            }

        }

    }

    private void handRead(SelectionKey key) throws Exception {
        // результат key.channel() = канал
        SocketChannel channel = (SocketChannel) key.channel();

        StringBuilder sb = new StringBuilder();

        while (true) {
            // сколько было прочитано из буфера
            int read = channel.read(buffer);
            // channel.read(buffer) вернет -1, если оборвалось соединение
            if (read == -1) {
                // закрыть канал
                channel.close();
                // выйти из цикла
                return;
            }
            // нет байтов для чтения
            if (read == 0) {
                break;
            }
            // переключает буфер из режима записи в режим чтения
            buffer.flip();
            // hasRemaining() - дойти до лимита буфера (до конца)
            while (buffer.hasRemaining()) {
                // к строке добавить каждый прочитанный элемент из буфера
                // буфер работает с байтами, поэтому надо явное преобразование
                sb.append((char) buffer.get());
            }
            // сбрасывает буфер (можно записывать сначала)
            buffer.clear();
        }
        // собрать строку
        // trim() - убирает пробелы и enter (все разделители)
        String result = sb.toString().trim();

        if (result.equals("ls")) {
            try {
                // обходит дерево каталогов
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws
                            IOException {
                        // добавить имя файла в список
                        // "\n\r" - переход на новую строку
                        String list = (file.getFileName().toString() + "\n\r");
                        // записать строку в канал
                        channel.write(ByteBuffer.wrap(list.getBytes(StandardCharsets.UTF_8)));
                        printCurrentName(channel);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (result.equals("cat_file")) {
            try {
                channel.write(ByteBuffer.wrap(("read from file " + file.toAbsolutePath().getFileName().toString() + ":\n\r").getBytes(StandardCharsets.UTF_8)));
                // прочитать все строки из файла
                String list = Files.readAllLines(file, StandardCharsets.UTF_8).toString();
                // записать строку в канал
                channel.write(ByteBuffer.wrap(list.getBytes(StandardCharsets.UTF_8)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (result.equals("touch_file")) {
            // новый файл (где находится, как назвать)
            Path newFilePath = Path.of("root", "a.txt");
            // если такого файла не существует
            if (!Files.exists(newFilePath)) {
                try {
                    // создать
                    Files.createFile(newFilePath);
                    String str = "The file was created successfully";
                    // записать строку в канал
                    channel.write(ByteBuffer.wrap(str.getBytes(StandardCharsets.UTF_8)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

//        // если вводимая команда не подходит не к одному из if
//        channel.write(ByteBuffer.wrap("Unknown command\n\r".getBytes(StandardCharsets.UTF_8)));
//        printCurrentName(channel);

//        // записать строку в канал
//        channel.write(ByteBuffer.wrap(result.getBytes(StandardCharsets.UTF_8)));
    }

    // в какой папке находимся
    private void printCurrentName(SocketChannel channel) throws IOException {
        channel.write(ByteBuffer.wrap((path.toAbsolutePath().getFileName().toString() + " ").getBytes(StandardCharsets.UTF_8)));
    }

    private void handAccept(SelectionKey key) throws Exception {
        // результат accept() = канал
        SocketChannel channel = server.accept();
        // канал работает не в блокирующем режиме
        channel.configureBlocking(false);
        // ожидает ключ на чтение
        channel.register(selector, SelectionKey.OP_READ);
        // приветствие в терминале
        channel.write(ByteBuffer.wrap("Welcome\n\r".getBytes(StandardCharsets.UTF_8)));
    }

    public static void main(String[] args) throws Exception {
        new NioServer();
    }
}
