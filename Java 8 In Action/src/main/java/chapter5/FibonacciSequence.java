package chapter5;

import java.util.stream.Stream;

public class FibonacciSequence {
    // 斐波那契数列
    // 0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987, 1597, 2584, 4181
    public static void main(String[] args) {
        Stream.iterate(new int[]{0, 1}, t -> new int[]{t[1], t[0]+t[1]})
                .limit(20)
                .map(t->t[0])
                .forEach(t -> System.out.print(t+", "));
    }
}
