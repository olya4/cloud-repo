package com.geekbrains.nio;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class MyFile {
    public static void main(String[] args) {
        Path path = Paths.get("root");
        Path file = Paths.get("root", "1.txt");
        String result = "touch_file";

        if (result.equals("ls")) {
            try {
                // обходит дерево каталогов
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws
                            IOException {
                        // добавить имя файла в список
                        String list = file.getFileName().toString();
                        System.out.println(list);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (result.equals("cat_file")) {
            List<String> list;
            // создать builder
            StringBuilder builder = new StringBuilder();
            try {
                // прочитать все строки из файла
                list = Files.readAllLines(file, StandardCharsets.UTF_8);
                System.out.println(list);

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
                    System.out.println(str);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
