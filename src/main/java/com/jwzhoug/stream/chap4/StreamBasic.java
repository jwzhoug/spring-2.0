package com.jwzhoug.stream.chap4;

import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * TODO...
 *
 * @author: zhoujw
 * @Date: 2019-04-25
 */
public class StreamBasic {

    public static void main(String[] args) {


        getLowCaloricDishesNamesInJava8(Dish.menu).forEach(System.out::println);

    }

    private static List<String> getLowCaloricDishesNamesInJava8(List<Dish> dishes) {
        return dishes.stream()
                .filter(dish -> dish.getCalories() < 400)
                .sorted(Comparator.comparing(Dish::getCalories))
                .map(Dish::getName)
                .collect(toList());
    }
}
