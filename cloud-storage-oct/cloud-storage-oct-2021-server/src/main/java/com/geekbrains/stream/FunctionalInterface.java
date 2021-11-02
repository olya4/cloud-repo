package com.geekbrains.stream;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FunctionalInterface {

    static void sum(int a, int b) {
        System.out.println(a + b);
    }

    public static void main(String[] args) {
        // I реализация
        // реализация лябды-выражения
        // Integer arg1, Integer arg2 - аргументы абстрактного метода
        Foo<Integer> sum = (Integer arg1, Integer arg2) -> {
            // реализация метода
            System.out.println(arg1 + arg2);
        };
        // вызов метода функционального интерфейса
        sum.foo(1, 2);

        // II реализация
        // метод reference (ссылка на метод)
        // у метода и лябды должны совпадать:
        // тип возвращаемого значения и входящие параметры
        Foo<Integer> sum1 = FunctionalInterface::sum;
        sum1.foo(3, 4);

        // forEach (добавить какое-то значение всем элементам коллекции, вывести в консоль),
        // peek (пройти по весм элементам, не закрывая стрим, и вызвать некоторое действие)
        // вызов метода accept у функционального интерфейса Consumer
        Consumer<String> print = (String str) -> {
            System.out.println(str);
        };
        print.accept("hello");

        Consumer<String> print1 = System.out::println;
        print1.accept("bye");

        // реализация дефолтного метода интерфейса Consumer
        // в начале отпечатает из метода accept
        // а на второй строке из System.out.print
        print1.andThen(s -> System.out.println(s + " & " + s)).accept("cat");

        // dropWhile, filter -> Stream (промежуточное действие, которе возвращает стрим),
        // allMatch, anyMatch, noMatch -> boolean (терминальные операции, возвращает boolean)
        // arg < 0 - условие (делится ли аргумент с остаком на 2)
        Predicate<Integer> isOdd = arg -> arg % 2 == 1;
        System.out.println(isOdd.test(9));

        // arg - аргумент метода интерфейса Predicate
        // return arg < 0 - условие (является ли аргумент отрицательным числом)
        Predicate<Integer> negative = arg -> {
            return arg < 0;
        };
        System.out.println(negative.test(9));

        // map, faltMap
        // принимает Integer
        // возвращает Double
        Function<Integer, Double> func = arg -> Double.valueOf((arg));
        System.out.println(func.apply(5));

        // принимает Integer
        // возвращает String
        Function<Integer, String> func1 = arg -> String.valueOf((arg + 1));
        System.out.println(func1.apply(10));

        // принимает Integer
        // возвращает String
        Function<Integer, String> func2 = String::valueOf;
        System.out.println(func2.apply(19));

        String t = "One";
        // нет аргументов у метода get()
        // t - переменная, над которой будут выполненый какие-то действия
        // toUpperCase() - какие-то действия
        // supplierStr.get() - вернет результат выполнения каких-то действий над переменной
        Supplier<String> supplierStr = () -> t.toUpperCase();
        System.out.println(supplierStr.get());

//        Supplier <ArrayList<Integer>> listSupplier = ArrayList::new;
        Supplier<ArrayList<Integer>> listSupplier = () -> new ArrayList<>();
        // при вызове метода get() создается новый список
        listSupplier.get();
    }
}
