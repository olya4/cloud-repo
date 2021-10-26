package com.geekbrains.nio;

import java.nio.ByteBuffer;

public class Buffers {
    public static void main(String[] args) {
        // длина буфера = 5
        // больше 5 значений не положить
        ByteBuffer buffer = ByteBuffer.allocate(5);

        // добавление значений в буфер

//        // через цикл
//        for (int i = 0; i < 5; i++) {
//            buffer.put((byte) i);
//        }

        // добавление по одному элементу
        buffer.put((byte) 'a');
        buffer.put((byte) 'b');
        buffer.put((byte) 'c');

        // вывести значения из буфера

        // переключает буфер из режима записи в режим чтения
        buffer.flip();
        // hasRemaining() - дойти до лимита буфера (до конца)
        while (buffer.hasRemaining()) {
            // буфер работает с байтами, поэтому надо явное преобразование
            System.out.println((char) buffer.get()); // выведет два пустых байта
            // длина буфера 5, а добавлено 3 элемента
        }

        // еще раз прочитать данные из буфера
        buffer.rewind();
        // hasRemaining() - дойти до лимита буфера (до конца)
        while (buffer.hasRemaining()) {
            // буфер работает с байтами, поэтому надо явное преобразование
            System.out.println((char) buffer.get()); // выведет два пустых байта
            // длина буфера 5, а добавлено 3 элемента
        }

        // сбрасывает буфер (можно записывать сначала)
        buffer.clear();
        // добавление по одному элементу
        buffer.put((byte) '1');
        buffer.put((byte) '2');
        buffer.put((byte) '3');

        // переключает буфер из режима записи в режим чтения
        buffer.flip();
        // hasRemaining() - дойти до лимита буфера (до конца)
        while (buffer.hasRemaining()) {
            // буфер работает с байтами, поэтому надо явное преобразование
            System.out.println((char) buffer.get()); // выведет два пустых байта
            // длина буфера 5, а добавлено 3 элемента
        }

    }
}
