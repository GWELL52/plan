package chapter3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

    public class ExecuteAround {
        public static void main(String[] args) throws IOException {
            String twoLines = processFile((BufferedReader b) -> b.readLine() + b.readLine());
            System.out.println(twoLines);
        }

        public static String processFile(BufferedReaderProcessor p) throws IOException {
            try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/chapter3/data.txt"))) {
                return p.process(br);
            }
        }

        @FunctionalInterface
        public interface BufferedReaderProcessor {
            public String process(BufferedReader b) throws IOException;
    }
};
