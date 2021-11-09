package com.geekbrains.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

// в MessageDecoder не нужно обрабатывать каждый метод интерфейса ChannelInboundHandlerAdapter
// т.к. абстрактный класс ChannelInboundHandlerAdapter имеет реализацию всех методов интерфейса ChannelInboundHandlerAdapter
// на всех реализациях стоят заглушки (методы имплементированы, но алгоритм в них не прописан)
// это дает возможность унаследовать только желаемые методы и прописать их на свое усмотрение

// первый Handler

// аннотация lombok
@Slf4j
public class MessageDecoder extends ChannelInboundHandlerAdapter {

    // клиент подключился
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Client connected...");
    }

    // клиент отключился
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Client disconnected...");
    }

    // принять сообщение от клиента и преобразовать его в строку
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // полученное сообщение от клиента положить в  ByteBuf сервера
        ByteBuf buf = (ByteBuf) msg;
        // логирование буфера
        log.debug("{}", buf);
        // создать StringBuilder
        StringBuilder sb = new StringBuilder();
        // считать сообщение из буфера
        while (buf.isReadable()) {
            // преобразовать считанные байты из буфера в символы
            char c = (char) buf.readByte();
            // считанные символы собрать в StringBuilder
            sb.append(c);
        }
        // StringBuilder преобразовать в строку
        String message = sb.toString();
        // передать сообщение из ByteBuf, которое было преобразовано в строку
        // из первого Handler во второй
        ctx.fireChannelRead(message);
    }
}
