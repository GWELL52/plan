package chapter5;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PythagoreanTriple {
    // 勾股数
    public static void main(String[] args) {
        Stream<double[]> pythagoreanTriple =
                IntStream.rangeClosed(1, 100).boxed()
                        .flatMap(a -> IntStream.rangeClosed(a, 100)
                                .filter(b -> Math.sqrt(a * a + b * b) % 1 == 0)
                                .mapToObj(b -> new double[]{a, b, Math.sqrt(a * a + b * b)}));
        pythagoreanTriple.forEach(t -> System.out.println(t[0] + ", " + t[1] + ", " + t[2]));

        Stream<double[]> pythagoreanTriple1 =
                IntStream.rangeClosed(1, 100).boxed()
                        .flatMap(a -> IntStream.rangeClosed(a, 100).boxed()
                                .map(b -> new double[]{a, b, Math.sqrt(a * a + b * b)}))
                        .filter(t -> t[2] % 1 == 0);
        pythagoreanTriple1.forEach(t -> System.out.println(t[0] + ", " + t[1] + ", " + t[2]));
    }
}
