package com.geekbrains.netty;

public class Test {
    public static void main(String[] args) {
        LombokExample hello = LombokExample.builder()
                .s("Hello")
                .x(15)
                .build();

        System.out.println(hello.getS());
        hello.setS("Bye");
        System.out.println(hello.getS());

    }
}
