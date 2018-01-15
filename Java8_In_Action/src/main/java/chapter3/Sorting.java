package chapter3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import static java.util.Comparator.comparing;
import java.util.List;

public class Sorting {
    public static void main(String[] args) {
        List<Apple> inventory = new ArrayList<>();
        inventory.addAll(Arrays.asList(new Apple(80,"green"), new Apple(155, "green"), new Apple(120, "red")));

        // 1.传递代码
        // [Apple{color='green', weight=80}, Apple{color='red', weight=120}, Apple{color='green', weight=155}]
        inventory.sort(new AppleComparator());
        System.out.println(inventory);

        // 2.使用匿名类
        // [Apple{color='green', weight=155}, Apple{color='red', weight=120}, Apple{color='green', weight=80}]
        inventory.sort(new Comparator<Apple>() {
            @Override
            public int compare(Apple a1, Apple a2) {
                return a2.getWeight().compareTo(a1.getWeight());
            }
        });
        System.out.println(inventory);

        // 更新清单
        inventory.add(1, new Apple(20, "red"));

        // 3.使用 Lambda 表达式
        // [Apple{color='red', weight=20}, Apple{color='green', weight=80}, Apple{color='red', weight=120}, Apple{color='green', weight=155}]
        inventory.sort((a1, a2) -> a1.getWeight().compareTo(a2.getWeight()));
        System.out.println(inventory);

        // 4.使用方法引用 reversed() 倒序
        //[Apple{color='green', weight=155}, Apple{color='red', weight=120}, Apple{color='green', weight=80}, Apple{color='red', weight=20}]
        inventory.sort(comparing(Apple::getWeight).reversed());
        System.out.println(inventory);

}

    public static class AppleComparator implements Comparator<Apple> {
        public int compare(Apple a1, Apple a2) {
            return a1.getWeight().compareTo(a2.getWeight());
        }
    }

    public static class Apple {
        private int weight = 0;
        private String color = "";

        public Apple(int weight, String color){
            this.weight = weight;
            this.color = color;
        }

        public Integer getWeight() {
            return weight;
        }

        public void setWeight(Integer weight) {
            this.weight = weight;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String toString() {
            return "Apple{" +
                    "color='" + color + '\'' +
                    ", weight=" + weight +
                    '}';
        }
    }
}
