package com.geekbrains.model;

import lombok.Getter;
import lombok.ToString;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
public class ListMessage extends AbstractMessage {
    // список файлов
    private final List<String> files;

    // список будет собираться из директории
    public ListMessage(Path dir) throws Exception {
        setType(CommandType.LIST_MESSAGE);
        // из директории взять имена файлов и преобразовать в строку
        files = Files.list(dir).map(p -> p.getFileName().toString())
                // полученный результат собрать в список
                .collect(Collectors.toList());
    }
}
