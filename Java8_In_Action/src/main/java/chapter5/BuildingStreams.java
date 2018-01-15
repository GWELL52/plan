package chapter5;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

public class BuildingStreams {
    public static void main(String[] args) {
        // Stream.of
        Stream<String> stream = Stream.of("Java 8", "Lambdas", "In", "Action");
        stream.forEach(System.out::println);

        // Stream.empty
        Stream<String> emptyStream = Stream.empty();
        emptyStream.forEach(System.out::println);

        // Arrays.stream
        int[] numbers = {2, 3, 5, 7, 11, 13};
        System.out.println(Arrays.stream(numbers).sum());

        // Files.lines
        long uniqueWords = 0;
        try (Stream<String> lines =
                     Files.lines(Paths.get("src/main/resources/chapter5/data.txt"), Charset.defaultCharset())) {
            uniqueWords = lines.flatMap(line -> Arrays.stream(line.split(" ")))
                    .map(String::toLowerCase)
                    .distinct()
                    .count();
        } catch (IOException e) {
        }
        System.out.println(uniqueWords);

        // Stream.iterate //无限
        Stream.iterate(0, n -> n+2)
                .limit(10)
                .forEach(System.out::println);

        // Stream.generate
        Stream.generate(Math::random)
                .limit(10)
                .forEach(System.out::println);
    }
}
