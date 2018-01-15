package chapter4;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class StreamBasic {
    public static void main(String[] args) {
        // Java 7
        getDishNameJava7(Dish.menu).forEach(System.out::println);

        System.out.println("---");

        // Java 8
        getDishNameJava8(Dish.menu).forEach(System.out::println);

    }


    public static List<String> getDishNameJava7(List<Dish> dishes) {
        List<Dish> lowCaloricDishes = new ArrayList<>();
        for(Dish d: dishes){
            if(d.getCalories() < 400){
                lowCaloricDishes.add(d);
            }
        }
        List<String> lowCaloricDishesName = new ArrayList<>();
        Collections.sort(lowCaloricDishes, new Comparator<Dish>() {
            public int compare(Dish d1, Dish d2){
                return Integer.compare(d1.getCalories(), d2.getCalories());
            }
        });
        for(Dish d: lowCaloricDishes){
            lowCaloricDishesName.add(d.getName());
        }
        return lowCaloricDishesName;
    }

    public static List<String> getDishNameJava8(List<Dish> dishes) {
        List<String> lowCaloricDishesName =
                dishes.parallelStream()
                        .filter(d->d.getCalories() < 400)
                        .sorted(comparing(Dish::getCalories))
                        .map(Dish::getName)
                        .collect(toList());
        return lowCaloricDishesName;
    }
}
