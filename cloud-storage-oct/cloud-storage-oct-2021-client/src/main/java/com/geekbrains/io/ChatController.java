package com.geekbrains.io;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ChatController implements Initializable {
    // папка root (находится в основном модуле cloud-storage-oct-2021 )
    // из папки root будут выбирается файлы для отправки от клиента серверу
    private Path root;

    // буфер
    private byte[] buffer;

    // поле для связки с дизайном
    // в chat.fxml есть переменная с таким же названием

    // в listView помещаются сообщения, которые приходят от сервера (входящие)
    public ListView<String> listView;
    // в input помещаются сообщения, которые идут от клиента (исходящие)
    public TextField input;

    // для входящего потока
    private DataInputStream dis;
    // для исходящего потока
    private DataOutputStream dos;

    // для установки сетевого соединения
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // создание буфера
        buffer = new byte[1024];
        // название папки
        root = Paths.get("root");
        // если такой папки нет
        if (!Files.exists(root)) {
            try {
                // создать папку
                Files.createDirectory(root);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        try {
            // добавить файлы в окошко чата
            fillFilesInView();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // на поле чата вешается обработчик событий
        // реагирует на клик мышки
        listView.setOnMouseClicked(e -> {
            // если количество кликов = 2
            if (e.getClickCount() == 2) {
                // имя файла, на котором сфокусированы клики мышки
                String fileName = listView.getSelectionModel().getSelectedItem();
                // если файл не директория (просто файл)
                // resolve добавляет к roo/имя файла
                if (Files.exists(root.resolve(fileName))) {
                    // добавить выбранный файл в нижнее окошко чата
                    input.setText(fileName);
                } else {
                    // иначе выведет текст
                    input.setText("Select file. Not directory");
                }
            }
        });

        try {
            // клиент создает сокет на своем конце связи и пытается подключить этот сокет к серверу
            Socket socket = new Socket("localhost", 8189);
            // для входящего потока
            dis = new DataInputStream(socket.getInputStream());
            // для исходящего потока
            dos = new DataOutputStream(socket.getOutputStream());
            // отдельный поток для чтения сообщений
            Thread readThread = new Thread(() -> {
                // try/catch вне цикла
                try {
                    // бесконечный цикл
                    while (true) {
//                        // message = прочитаному сообщение от сервера
//                        String message = dis.readUTF();
//                        // Platform.runLater - чтобы не ругался java fx
//                        // listView.getItems().add(message) будет вызвано в потоке java fx
//                        // в компонент listView выложить сообщение от сервера
//                        Platform.runLater(() -> listView.getItems().add(message));

                        // message = прочитаному сообщение от сервера
                        String message = dis.readUTF();
                        // Platform.runLater - чтобы не ругался java fx
                        // в компонент setText выложить сообщение от сервера
                        // получил ли он файл
                        Platform.runLater(() -> input.setText(message));
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
        // в переменную fileName положить файл из компонента TextField input
        String fileName = input.getText();
        // очистить поле после отправки
        input.clear();

        Path filePath = root.resolve(fileName);

        // если такой файл существует
        if (Files.exists(filePath)) {
            // отправить имя этого файла
            dos.writeUTF(fileName);
            // отправить длину этого файла
            dos.writeLong(Files.size(filePath));

//            // оправить байты файла
//            // source = filePath
//            // OutputStream = dos
//            Files.copy(filePath, dos);

            try (FileInputStream fis = new FileInputStream(filePath.toFile())) {
                int read;
                // read = байты считанные из буфера
                while ((read = fis.read(buffer)) != -1) {
                    // записать в исходящий поток байты из буфера
                    // от 0 до сколько было прочитано
                    dos.write(buffer, 0, read);
                }
            }
            // очистить внутренние буферы и принудительно записать (т.е. очистить) все ожидающие данные в назначение потока
            dos.flush();
        }
    }
}
