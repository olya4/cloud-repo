package com.geekbrains.stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Reducer {

    private void sum(Consumer<Integer> consumer) {
        consumer.accept(Stream.of(1, 2, 3, 4, 5)
                        // отфильтровать только нечетные элементы
                        .filter(x -> x % 2 == 1)
                        // если в стриме нет элементов, вернет 0
//                      .reduce(0, Integer::sum) // сумма элементов
                        .reduce(1, (x, y) -> x * y) // произведение элементов
        );
    }

    // собрать все отфильтрованные элементы из стрима в список без collect
    private List<Integer> list() {
        return Stream.of(1, 2, 3, 4, 5)
                // отфильтровать только нечетные элементы
                .filter(x -> x % 2 == 1)
                // новый пустой список, в который добавляются элементы
                .reduce(new ArrayList<>(),
                        // value - текущий элемент стрима
                        (listValue, value) -> {
                            // текущий элемент стрима положить в список
                            listValue.add(value);
                            return listValue;
                        },
                        // комбинатор принимает левый и правый лист
                        (left, right) -> {
                            // в левый добавляется правый
                            left.addAll(right);
                            return left;
                        });
    }

    // собрать все элементы из стрима в map без collect
    private Map<Integer, Integer> collectToMap() {
        return Stream.of(1, 1, 2, 3, 3, 3, 3, 4, 5)
                .reduce(new HashMap<>(),
                        (map, value) -> {
                            // добавить текущий элемент стрима в map, количество повторений текущего элемента
                            map.put(value, map.getOrDefault(value, 0) + 1);
                            return map;
                        },
                        (left, right) -> {
                            left.putAll(right);
                            return left;
                        }
                );
    }

    public static void main(String[] args) {
        new Reducer().sum(System.out::println);
        Reducer reducer = new Reducer();
        System.out.println(reducer.list());
        System.out.println(reducer.collectToMap()); // {1=2, 2=1, 3=4, 4=1, 5=1}

        // собрать все элементы из стрима в map с collect
        System.out.println(
                Stream.of(1, 2, 1, 1, 1, 2, 3, 2)
                        .collect(Collectors.toMap(
                                Function.identity(),
                                // добавить элемент
                                value -> 1,
                                // суммировать количество повторений текущего элемента
                                Integer::sum
                        ))
        ); // {1=4, 2=3, 3=1}

        // соберет user в map, где они будут сгруппированы по возрасту
        // для каждого возраста будет свой список user
        // (будет два списка: для 12 (внутри два user) и для 15 лет (внутри один user) )
        Map<Integer, List<User>> users = Stream.of(
                User.builder()
                        .age(12).name("Tom")
                        .build(),
                User.builder()
                        .age(12).name("Jack")
                        .build(),
                User.builder()
                        .age(15).name("Peter")
                        .build())
                .collect(Collectors.groupingBy(
                        User::getAge)
                );
        System.out.println(users); // {12=[User(age=12, name=Tom), User(age=12, name=Jack)], 15=[User(age=15, name=Peter)]}
    }
}
