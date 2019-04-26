package com.jwzhoug.stream.chap6;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.partitioningBy;

/**
 * TODO...
 *
 * @author: zhoujw
 * @Date: 2019-04-26
 */
public class PartitionPrimeNumbers {

    public static void main(String[] args) {

        System.out.println(Math.sqrt(9));
        System.out.println("Numbers partitioned in prime and non-prime: " + partitionPrimes(100));
    }

    private static Map<Boolean, List<Integer>> partitionPrimes(int n) {
        return IntStream.rangeClosed(2, n).boxed().collect(partitioningBy(candidate -> isPrime(candidate)));
    }

    // 是否 是 质数 (上限 对 开方范围内得 数据能否进行整除)
    public static boolean isPrime(int candidate) {
        return IntStream.rangeClosed(2, candidate - 1)
                .limit((long) Math.floor(Math.sqrt((double) candidate)) - 1)
                .noneMatch(i -> candidate % i == 0);
    }

}
