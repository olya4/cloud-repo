package com.geekbrains.netty;

import com.geekbrains.model.AbstractMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j

// принимает абстрактные сообщение от класса AbstractMessage из модуля common
public class MessageHandler extends SimpleChannelInboundHandler<AbstractMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage msg) throws Exception {
        // прологировать полученное сообщение
        log.debug("received: {}", msg);
        // записать и отправить клиенту полученное сообщение
        // затем очистить ctx для записи следующего сообщения
        ctx.writeAndFlush(msg);
    }

}
