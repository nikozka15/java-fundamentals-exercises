package com.bobocode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.Supplier;

public class DemoApp {
    public static void main(String[] args) {
// Function
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<Integer> result1 = convert(numbers, number -> number * 2);
        System.out.println(result1);
// IntBinaryOperator
        System.out.println(convert(1, 1, Integer::sum));
// Consumer
        print("Consumer", System.out::println);

// Supplier
        System.out.println(getObject(() -> "Supplier"));
// BiFunction
        BiFunction<String, String, String> stringConcatenator = (str1, str2) -> str1 + str2;
        System.out.println(convert("first", "second", stringConcatenator));

        // quize
        // Object o = () -> {System.out.println("Tricky example"); }; // will not compile
        Runnable trickyExample = () -> {
            System.out.println("Tricky example");
        };

        

    }
// Function
    public static <T, R> List<R> convert(List<T> list, Function<T, R> function){

        List<R> result = new ArrayList<>();
        for (T t : list) {
            result.add(function.apply(t));
        }
        return result;
    }

    // IntBinaryOperator
    public static int convert(int number, int number2, IntBinaryOperator function){
        return function.applyAsInt(number, number2);
    }
    // Consumer
    public static <T> void print(T string, Consumer<T> consumer) {
        consumer.accept(string);
    }
    // Supplier
    public static <T> T getObject(Supplier<T> supplier) {
        return supplier.get();
    }
    // BiFunction
    public static <T, U, R> R convert(T t, U u, BiFunction<T, U, R> function) {
        return function.apply(t, u);
    }
}
