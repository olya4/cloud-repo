package com.geekbrains.stream;

import java.util.Arrays;
import java.util.stream.Stream;

public class Flatten {

    // функция принимает неровный двумерный массив, а возвращает одномерный
    // построчно собирает все массивы в один
    public static int[] flatten(Integer[][] array) {
        return Arrays.stream(array)
                // двумерный массив распаковывается на отдельные массивы
                // аргумент a - это массив целых чисел (array)
                // flatMap соберет все стримы в один
                // flatMap может вернуть только стрим
                .flatMap(a -> Stream.of(a))
                // преобразовать массивы из Integer в int
                .mapToInt(Integer::intValue)
                // отдельные массивы собираются в одни массив
                .toArray();
    }

    public static void main(String[] args) {
        Integer[][] array = new Integer[][]{
                {1, 2, 3},
                {},
                {4},
                {5, 6, 7, 8},
                {},
                {},
                {9, 10}
        };
        int[] result = flatten (array);
        System.out.println(Arrays.toString(result));
    }
}
