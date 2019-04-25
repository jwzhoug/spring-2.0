package com.jwzhoug.stream.chap3;

import com.jwzhoug.stream.appa.Apple;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * TODO...
 *
 * @author: zhoujw
 * @Date: 2019-04-25
 */
public class Lambdas {

    public static void main(String[] args) {

        Runnable r = () -> System.out.println("Hello");
        r.run();

        // Filtering with lambdas
        List<Apple> inventory = Arrays.asList(new Apple(80,"green"), new Apple(155, "green"), new Apple(120, "red"));

        Comparator<Apple> c = Comparator.comparing(Apple::getWeight);
        inventory.sort(c);
        System.out.println(inventory);

    }
}
