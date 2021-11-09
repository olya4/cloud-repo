package com.geekbrains.mem;

public class MemTest {
    public static void main(String[] args) {
        // meta space
        MemExample.i = 5;
        MemExample example = new MemExample("123");
        String s = new String("1234");
//        example.foo(s, 5);
//        System.out.println(s);

        User user = new User();
        user.setName("Tom");
        System.out.println(user);

        example.change(user);
        System.out.println(user);
    }
}
