package com.geekbrains.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter

// объект этого класса будет отправлен по сети (файл, читать с указанного байта(int from), с указанием первый ли он (boolean isFirstButch))
public class FileMessage extends AbstractMessage {

    private static final int BUTCH_SIZE = 8196;

    // из сети получить имя файла
    private final String name;
    // размер файла
    private final long size;
    // из сети получить считываемый массив байтов
    private final byte[] bytes;
    // если нет isFirstButch - создать новый файл
    // если есть - добавить в него массив считанных байтов
    private final boolean isFirstButch;
    // номер последнего байта
    private final int endByteNum;
    // последняя часть передаваемого файла
    private final boolean isFinishButch;

    public FileMessage(String name,
                       long size,
                       byte[] bytes,
                       boolean isFirstButch,
                       int endByteNum,
                       boolean isFinishButch) {
        this.name = name;
        this.size = size;
        this.bytes = bytes;
        this.isFirstButch = isFirstButch;
        this.endByteNum = endByteNum;
        this.isFinishButch = isFinishButch;
        setType(CommandType.FILE_MESSAGE);
    }
}
