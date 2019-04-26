package com.jwzhoug.stream.chap6;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.jwzhoug.stream.chap6.Dish.menu;
import static java.util.stream.Collectors.*;

/**
 * 分区
 *
 * @author: zhoujw
 * @Date: 2019-04-26
 */
public class Partitioning {

    public static void main(String[] args) {
        System.out.println("Dishes partitioned by vegetarian: " + partitionByVegeterian());
        System.out.println("Vegetarian Dishes by type: " + vegetarianDishesByType());
        System.out.println("Most caloric dishes by vegetarian: " + mostCaloricPartitionedByVegetarian());

    }

    private static Map<Boolean, List<Dish>> partitionByVegeterian() {
        return menu.stream().collect(partitioningBy(Dish::isVegetarian));
    }

    private static Map<Boolean, Map<Dish.Type, List<Dish>>> vegetarianDishesByType() {
        return menu.stream().collect(partitioningBy(Dish::isVegetarian, groupingBy(Dish::getType)));
    }

    private static Object mostCaloricPartitionedByVegetarian() {
        return menu.stream().collect(partitioningBy(Dish::isVegetarian,
                collectingAndThen(maxBy(Comparator.comparingInt(Dish::getCalories)), Optional::get)));
    }
}
