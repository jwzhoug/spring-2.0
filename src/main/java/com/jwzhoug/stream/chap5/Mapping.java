package com.jwzhoug.stream.chap5;


import com.jwzhoug.stream.chap4.Dish;

import javax.sound.midi.Soundbank;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.jwzhoug.stream.chap4.Dish.menu;
import static java.util.stream.Collectors.toList;

/**
 * TODO...
 *
 * @author: zhoujw
 * @Date: 2019-04-25
 */
public class Mapping {

    public static void main(String[] args) {

        List<String> dishName = menu.stream()
                .map(Dish::getName)
                .collect(toList());
        System.out.println(dishName);

        // map
        List<String> words = Arrays.asList("Hello", "World");
        List<Integer> wordLengths = words.stream()
                .map(String::length)
                .collect(toList());
        System.out.println(wordLengths);


        // flatMap
        List<Integer> numbers1 = Arrays.asList(1,2,3,4,5);
        List<Integer> numbers2 = Arrays.asList(6,7,8);
        List<int[]> pairs = numbers1.stream()
                .flatMap(i -> numbers2.stream().map(j -> new int[]{i,j}))
                .filter(pair -> (pair[0] + pair[1]) % 3 == 0)
                .collect(toList());
        pairs.forEach(pair -> System.out.println("(" + pair[0] + "," + pair[1] +")"));

    }
}
