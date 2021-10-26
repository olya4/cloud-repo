package com.geekbrains.nio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FilesTest {
    public static void main(String[] args) throws IOException {
        // записать строку в файл
        // root - в этой папке находится файл
        // 1.txt - имя файла
        //  StandardOpenOption - можно выбрать: перезаписать текст, добавить и т.д
        Files.writeString(Paths.get("root", "1.txt"),
                "Hello world!",
                StandardOpenOption.APPEND);

        Path path = Paths.get("root");

    }
}
