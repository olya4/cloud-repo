package com.geekbrains.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IoUtils {
    public static void main(String[] args) throws Exception {
        Path root = Paths.get("root");
        // если папки нет-создать
        if (!Files.exists(root)) {
            Files.createDirectories(root);
        }

        // путь к файлу
//        String resource = IoUtils.class.getResource("1.txt").getFile();

        InputStream is = new FileInputStream("D:\\математика\\geek\\Облачное хранилище(БД, облачное хранилище)\\Разработка сетевого хранилища на Java" +
                "\\1.Проектирование архитектуры\\cloud-storage-oct-2021\\cloud-storage-oct-2021-server\\src\\main\\resources\\com\\geekbrains\\io\\1.txt");
        // копия файла
        // куда копировать - путь
        String copy = new String("D:\\математика\\geek\\Облачное хранилище(БД, облачное хранилище)\\Разработка сетевого хранилища на Java" +
                "\\1.Проектирование архитектуры\\cloud-storage-oct-2021\\cloud-storage-oct-2021-server\\src\\main\\resources\\com\\geekbrains\\io\\dir1\\");
        // имя нового файла, в который копировали
        OutputStream os = new FileOutputStream(copy + "/copy.txt");

        // создать буффер
        byte[] buffer = new byte[8129];
        // количество прочитанных байтов
        int readBytes;

        // когда при чтении закончатся байты из файла, вернется-1
        // пока байты не закончиличь, цикл вернет количество прочитанных байтов
        while (true) {
            // количество прочитанных байтов из стрима в буффер
            readBytes = is.read(buffer);

            if (readBytes == -1) {
                break;
            }
//            // считать по байтово
//            for (int i = 0; i < readBytes; i++) {
//                System.out.print(buffer[i] + " ");
//            }
//            System.out.println();

            // записать данные в новый файл
            // из буффера с 0 до количества прочитанных байтов
            os.write(buffer, 0, readBytes);

            // вывести из буффера начиная с 0 до количества прочитанных байтов из стрима
            System.out.println(new String(buffer, 0, readBytes));

        }
    }
}
