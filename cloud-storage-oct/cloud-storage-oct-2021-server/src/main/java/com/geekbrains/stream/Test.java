package com.geekbrains.stream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test {
    public static void main(String[] args) throws IOException {

        Stream.of(1, 2, 3, 4, 5, 6, 7)
                // оставить только четные
                .filter(x -> x % 2 == 0)
                // вывести в консоль столбиком
                .forEach(System.out::println);

        Stream.of(1, 2, 3, 4, 5, 6, 7)
                // оставить только четные
                .filter(x -> x % 2 == 0)
                // увеличить каждое значение в два раза
                .map(x -> x * 2)
                // вывести в консоль строкой
                .forEach(x -> System.out.print(x + " "));

        System.out.println();

        List<String> list = Stream.of("A", "B", "C")
                // собрать в строку
                .collect(Collectors.toList());
        System.out.println(list); // [A, B, C]

        String str = Stream.of("D", "E", "F")
                // собрать в строку с разделителем
                .collect(Collectors.joining(", "));
        System.out.println(str); // D, E, F

        // собрать имена файлов из папки
        List<String> fileNames = Files.list(Paths.get("root"))
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
        System.out.println(fileNames);

    }
}
