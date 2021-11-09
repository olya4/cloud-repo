package com.geekbrains.netty;

import com.geekbrains.model.FileMessage;
import com.geekbrains.model.FileRequest;
import com.geekbrains.model.ListMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import com.geekbrains.model.AbstractMessage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j

// принимает абстрактные сообщение от класса com.geekbrains.model.AbstractMessage из модуля common
public class MessageHandler extends SimpleChannelInboundHandler<AbstractMessage> {

    private Path serverClientDir;
    private byte[] buffer;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // инициализация директории
        serverClientDir = Paths.get("server");
        // отправить клиенту список всех файлов, которые есть на сервере
        ctx.writeAndFlush(new ListMessage(serverClientDir));
        buffer = new byte[8192];
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage msg) throws Exception {
        switch (msg.getType()) {
            case FILE_MESSAGE:
                processFile((FileMessage) msg, ctx);
                break;
            case FILE_REQUEST:
                sendFile((FileRequest) msg, ctx);
                break;
        }
    }

    private void processFile(FileMessage msg, ChannelHandlerContext ctx) throws Exception {
        Path file = serverClientDir.resolve(msg.getName());

        // если первый файл
        if (msg.isFirstButch()) {
            // удалить предыдущий файл
            Files.deleteIfExists(file);
        }

        // записать файл
        try (FileOutputStream os = new FileOutputStream(file.toFile(), true)) {
            os.write(msg.getBytes(), 0, msg.getEndByteNum());
        }

        // если весь файл записан
        if (msg.isFinishButch()) {
            ctx.writeAndFlush(new ListMessage(serverClientDir));
        }

    }

    private void sendFile(FileRequest msg, ChannelHandlerContext ctx) throws Exception {

        boolean isFirstButch = true;

        Path filePath = serverClientDir.resolve(msg.getName());
        long size = Files.size(filePath);

        try (FileInputStream is = new FileInputStream(serverClientDir.resolve(msg.getName()).toFile())) {
            // сколько байтов было прочитано в буфер
            int read;

            while ((read = is.read(buffer)) != -1) {
                // собрать посылку
                FileMessage message = FileMessage.builder()
                        .bytes(buffer)
                        .name(filePath.getFileName().toString())
                        .size(size)
                        .isFirstButch(isFirstButch)
                        .isFinishButch(is.available() <= 0)
                        .endByteNum(read)
                        .build();
                ctx.writeAndFlush(message);
                isFirstButch = false;
            }
        } catch (Exception e) {
            log.error("e: ", e);
        }
    }

//    @Override
//    protected void channelRead0(ChannelHandlerContext ctx, com.geekbrains.model.AbstractMessage msg) throws Exception {
//        // прологировать полученное сообщение
//        log.debug("received: {}", msg);
//        // записать и отправить клиенту полученное сообщение
//        // затем очистить ctx для записи следующего сообщения
//        ctx.writeAndFlush(msg);
//    }

}
