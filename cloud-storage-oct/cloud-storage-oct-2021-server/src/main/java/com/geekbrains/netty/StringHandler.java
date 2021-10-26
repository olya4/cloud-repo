package com.geekbrains.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

// SimpleChannelInboundHandler<I> - параметризированный класс (вместо <I> подставлен <String>) ,
// кот.наследуется от абстрактный класса ChannelInboundHandlerAdapter, а он имплементирует интерфейс ChannelInboundHandlerAdapter
// т.к. абстрактный класс ChannelInboundHandlerAdapter имеет реализацию всех методов интерфейса ChannelInboundHandlerAdapter
// на всех реализациях стоят заглушки (методы имплементированы, но алгоритм в них не прописан)
// это дает возможность унаследовать только желаемые методы и прописать их на свое усмотрение

// второй Handler

// аннотация lombok
@Slf4j
public class StringHandler extends SimpleChannelInboundHandler<String> {

    // дата формат будет добавлена к полученному строковому сообщению
    private final SimpleDateFormat format;

    public StringHandler() {
        // инициализация даты
        format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    }

//    // принять сообщение (преобразованную строку) от первого Handler и преобразовать его (добавить дату)
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        // полученное сообщение от первого Handler положить в строку
//        String str = (String) msg;
//        log.debug("received: {}", msg);
//        // result = текущая дата + полученное сообщение
//        String result = "[" + format.format(new Date()) + "] " + str;
//        // result - результат работы второго Handler
//        // записать result для передачи из второго Handler в третий
//        ctx.write(result);
//    }

    // принять строку (т.к. указан параметр <String>) от первого Handler и преобразовать его (добавить дату)
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        log.debug("received: {}", s);
        // result = текущая дата + полученное сообщение
        String result = "[" + format.format(new Date()) + "] " + s;
        // result - результат работы второго Handler
        // записать result для передачи из второго Handler в третий,
        // затем очистить result для получения новой строки
        ctx.writeAndFlush(result);
    }

//    // метод для обработки исключений
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        super.exceptionCaught(ctx, cause);
//    }

}
