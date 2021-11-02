package com.geekbrains.nio;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.*;

public class NioUtils {
    public static void main(String[] args) throws IOException {
        // Path — это конкретное местоположение объекта
        // создание path
        // в  Paths.get - передается путь
        // пасс собирается из 1 папки, 2 папки и файла 1.txt
//        Path path = Paths.get("1", "2", "1.txt");
//        // или через ./ - корень проекта
//        Path path1 = Path.of("./");

//        // не надо пересоздавать экземпляр path
//        // к текущему path можно добавить ресурс
//        path.resolve("какой-то ресурс");

//        // путь до ОС
//        path.toAbsolutePath();
//        path.getParent();
//
//        // путь до корня проекта
//        // если использовать без path.toAbsolutePath();
//        path.getParent();

//        Path path = Path.of("./");
//        System.out.println(path.getParent()); // null

//        Path path = Path.of("./").toAbsolutePath();
        // выведет абсолютный путь
//        System.out.println(path.getParent()); //D:\математика\geek\Облачное хранилище(БД, облачное хранилище)\Разработка сетевого хранилища на Java\1.Проектирование архитектуры\cloud-storage-oct-2021

//        Path path = Path.of("root", "dir1");
//        // parent у dir1 = root
//        // выведет только родителя, а не абсолютный путь
//        System.out.println(path.getParent()); // root

        Path path = Path.of("root");

        // наблюдатель за папкой root
        WatchService watchService = FileSystems.getDefault().newWatchService();
        // запускается в отдельном потоке, т.к. блокирует
        new Thread(() -> {
            while (true) {
                WatchKey poll = null;
                try{
                    poll = watchService.take();
                    List<WatchEvent<?>> watchEvents = poll.pollEvents();
                    for (WatchEvent<?> watchEvent : watchEvents) {
                        // объект наблюдения
                        System.out.println(watchEvent.context());
                        // на какие события реагирует наблюдатель
                        System.out.println(watchEvent.kind());
                    }
                    poll.reset();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }).start();

        // к папке root повесить (наблюдателя, события, на которые он будет реагировать)
        path.register(watchService, ENTRY_MODIFY, ENTRY_DELETE, ENTRY_CREATE);
    }
}
