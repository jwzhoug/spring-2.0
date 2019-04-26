package com.jwzhoug.stream.chap6;

import javax.sound.midi.Soundbank;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.jwzhoug.stream.chap6.Dish.menu;
import static com.jwzhoug.stream.chap6.Dish.dishTags;
import static java.util.stream.Collectors.*;

/**
 * 分组操作
 *
 * @author: zhoujw
 * @Date: 2019-04-26
 */
public class Grouping {

    enum CaloricLevel {DIET, NORMAL, FAT}

    public static void main(String[] args) {

        System.out.println("Dishes grouped by type: " + groupDishesByType());
        System.out.println("Dish names grouped by type: " + groupDishNamesByType());
//        System.out.println("Dish tags grouped by type: " + groupDishTagsByType());
        System.out.println(groupdishedByTypeAndCaloricLevel());

        System.out.println(mostCaloricDishesByType());

        System.out.println(mostCaloricDishesByTypeWithoutOptionals());

        System.out.println(sumCaloricsByType());

        System.out.println(caloricLevelsByType());

    }

    private static Map<Dish.Type, List<Dish>> groupDishesByType() {
        return menu.stream().collect(groupingBy(Dish::getType));
    }

    private static Map<Dish.Type, List<String>> groupDishNamesByType() {
        return menu.stream().collect(groupingBy(Dish::getType, mapping(Dish::getName, toList())));
    }

    // JDK 9 < flatMapping(Stream<?>,Collectors)
//    private static Map<Dish.Type, Set<String>> groupDishTagsByType() {
//        return menu.stream().collect(groupingBy(Dish::getType, flatMapping(dish -> dishTags.get(dish.getName()).stream(), toSet())));
//    }

    private static Map<Dish.Type, Map<CaloricLevel, List<Dish>>> groupdishedByTypeAndCaloricLevel() {
        return menu.stream()
                .collect(
                        groupingBy(Dish::getType,
                                groupingBy(dish -> groupCaloricLevel(dish))));
    }

    // groupingBy(Function<?>,Collector)
    private static Map<Dish.Type, Optional<Dish>> mostCaloricDishesByType() {
        return menu.stream().collect(groupingBy(Dish::getType,
                reducing((d1, d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2)));
    }

    // Collector < collectingAndThen(Collector,Function)
    private static Map<Dish.Type, Dish> mostCaloricDishesByTypeWithoutOptionals() {
        return menu.stream().collect(groupingBy(Dish::getType,
                collectingAndThen(reducing((d1, d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2), Optional::get)));
    }

    private static Map<Dish.Type, Integer> sumCaloricsByType() {
        return menu.stream().collect(groupingBy(Dish::getType, summingInt(Dish::getCalories)));
    }

    private static Map<Dish.Type, Set<CaloricLevel>> caloricLevelsByType() {
        return menu.stream().collect(
                groupingBy(Dish::getType, mapping(
                        dish -> groupCaloricLevel(dish),
                        toSet())));
    }

    public static CaloricLevel groupCaloricLevel(Dish dish) {
        if (dish.getCalories() <= 400) {
            return CaloricLevel.DIET;
        } else if (dish.getCalories() <= 700) {
            return CaloricLevel.NORMAL;
        } else {
            return CaloricLevel.FAT;
        }
    }

}
