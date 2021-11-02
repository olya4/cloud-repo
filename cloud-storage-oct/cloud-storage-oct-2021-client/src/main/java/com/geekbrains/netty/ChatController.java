package com.geekbrains.netty;


import com.geekbrains.model.FileMessage;
import com.geekbrains.model.FileRequest;
import com.geekbrains.model.ListMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import com.geekbrains.model.AbstractMessage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static javafx.application.Platform.runLater;

public class ChatController implements Initializable {
    // папка root (находится в основном модуле cloud-storage-oct-2021 )
    // из папки root будут выбирается файлы для отправки от клиента серверу
    private Path root;

    // буфер
    private byte[] buffer;

    private Path currentDir;

    // поле для связки с дизайном
    // в chat.fxml есть переменная с таким же названием

    // в listView помещаются сообщения, которые приходят от сервера (входящие)
    public ListView<String> listView;
    // в input помещаются сообщения, которые идут от клиента (исходящие)
    public TextField input;

    // для входящего потока
    private ObjectDecoderInputStream dis;
    // для исходящего потока
    private ObjectEncoderOutputStream dos;

    // для установки сетевого соединения
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            currentDir = Paths.get("client");
            // клиент создает сокет на своем конце связи и пытается подключить этот сокет к серверу
            Socket socket = new Socket("localhost", 8189);
            // вначале исходящий поток, затем входящий
            // для исходящего потока
            dos = new ObjectEncoderOutputStream(socket.getOutputStream());
            // для входящего потока
            dis = new ObjectDecoderInputStream(socket.getInputStream());
            System.out.println("Client is connected");
            // отдельный поток для чтения сообщений
            Thread readThread = new Thread(() -> {
                // try/catch вне цикла
                try {
                    // бесконечный цикл
                    while (true) {
                        // астрактное сообщение message = прочитаному сообщение от сервера
                        AbstractMessage message = (AbstractMessage) dis.readObject();
                        processMessage(message);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            // readThread - демон-поток
            // setDaemon(true) - поток продолжает работать даже после завершения работы потока main
            readThread.setDaemon(true);
            // запустить поток
            readThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processMessage(AbstractMessage message) throws IOException {
        switch (message.getType()) {
            case FILE_MESSAGE:
                FileMessage msg = (FileMessage) message;
                Path file = currentDir.resolve(msg.getName());

                // если первый файл
                if (msg.isFirstButch()) {
                    // удалить предыдущий файл
                    Files.deleteIfExists(file);
                }

                // записать файл
                try (FileOutputStream os = new FileOutputStream(file.toFile(), true)) {
                    os.write(msg.getBytes(), 0, msg.getEndByteNum());
                }

                break;
            case LIST_MESSAGE:
                ListMessage list = (ListMessage) message;
                runLater(() -> {
                    listView.getItems().clear();
                    listView.getItems().addAll(list.getFiles());
                });
                break;
        }
    }

    // получение файлов от сервера
    private List<String> getFilesInCurrentDir() throws IOException {
        return Files.list(currentDir).map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
    }

    // добавить файлы в окошко чата
    private void fillFilesInView() throws IOException {
        // очистить поле чата от старых файлов
        listView.getItems().clear();
        // из root достать все файлы
        // Files.list - возвращает стрим пассов
        List<String> list = Files.list(root)
                // преобразовать пасс к строке
                .map(p -> p.getFileName().toString())
                // собрать в список
                .collect(Collectors.toList());
        // добавить все элементы нового списка в поле чата
        listView.getItems().addAll(list);
    }

    // отправка сообщений от пользователя (отправить файл серверу)
    public void sendMessage(ActionEvent actionEvent) throws IOException {
        // в переменную str положить файл из компонента TextField input
        String fileName = input.getText();
        // очистить поле после отправки
        input.clear();
        // записать и отправить сообщение
        dos.writeObject(new FileRequest(fileName));
    }
}
