package com.geekbrains.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

// в MessageEncoder не нужно обрабатывать каждый метод интерфейса ChannelOutboundHandlerAdapter
// т.к. абстрактный класс ChannelOutboundHandlerAdapter имеет реализацию всех методов интерфейса ChannelOutboundHandlerAdapter
// на всех реализациях стоят заглушки (методы имплементированы, но алгоритм в них не прописан)
// это дает возможность унаследовать только желаемые методы и прописать их на свое усмотрение

// третий Handler

// аннотация lombok
@Slf4j
public class MessageEncoder extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        // полученное сообщение от второго Handler положить в строку
        String message = (String) msg;
        // логирование сообщения, которое получено от второго Handler
        log.debug("received: {}", message);
        // достать буфер
        ByteBuf buffer = ctx.alloc().buffer();
        // записать полученное сообщение в буфер
        buffer.writeBytes(message.getBytes(StandardCharsets.UTF_8));
        buffer.retain();
        // записать и очистить буфер
        ctx.writeAndFlush(buffer);
    }
}
