package com.geekbrains.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

// аннотация lombok генерирует логгер
@Slf4j
public class Server {

    public Server() {
        // прием входящих подключений
        // ограничить количество потоков 1
        EventLoopGroup auth = new NioEventLoopGroup(1);
        // обрабатывает потоки данных
        // не ограничивать количество потоков
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            // создать сервер
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(auth, worker)
                    .channel(NioServerSocketChannel.class)
                    // инициализатор канала
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // логика взаимодействия сервера с клиентом
                        @Override
                        // pipeline - список Handler
                        protected void initChannel(SocketChannel channel) throws Exception {
                            // добавить Handler в pipeline
                            // последовательность Handler очень важна
                            channel.pipeline().addLast(
                                    // собственно прописанные классы
//                                    new MessageDecoder(),
//                                    new MessageEncoder(),
//                                    new StringHandler()

//                                    // классы по умолчанию
//                                    new StringDecoder(),
//                                    new StringEncoder(),
//                                    new StringHandler()

                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    // принимает сообщения от класса AbstractMessage
                                    new MessageHandler()
                            );
                        }
                    });
            // установить соединение через порт 8189
            ChannelFuture future = bootstrap.bind(8189).sync();
            log.debug("Server started...");
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            // будут логироваться error
            log.error("error: ", e);
        } finally {
            // shutdownGracefully() - заканчивает работу:
            // все EventLoopGroup завершены и все каналы, обрабатываемые этими пулами закрыты
            auth.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
