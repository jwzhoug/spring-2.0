package com.jwzhoug.stream.chap11.v1;

import com.jwzhoug.stream.chap11.ExchangeService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TODO...
 *
 * @author: zhoujw
 * @Date: 2019-04-28
 */
public class BestPriceFinder {

    private final List<Shop> shops = Arrays.asList(new Shop("BestPrice"),
            new Shop("LetsSaveBig"),
            new Shop("MyFavoriteShop"),
            new Shop("BuyItAll"));

    private final Executor executor = Executors.newFixedThreadPool(shops.size(), (Runnable r) -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    public List<String> findPricesSequential(String product) {
        return shops.stream()
                .map(shop -> shop.getName() + "price is " + shop.getPrice(product))
                .collect(Collectors.toList());
    }

    public List<String> findPricesParallel(String product) {
        return shops.parallelStream()
                .map(shop -> shop.getName() + "price is " + shop.getPrice(product))
                .collect(Collectors.toList());
    }

    public List<String> findPricesFuture(String product) {
        List<CompletableFuture<String>> priceFuture =
                shops.stream()
                        .map(shop -> CompletableFuture.supplyAsync(
                                () -> shop.getName() + "price is " + shop.getPrice(product), executor))
                        .collect(Collectors.toList());

        List<String> prices = priceFuture.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        return prices;
    }

    /**
     * 异步操作，进行金额提取 以及汇率 转换
     *
     * @param product
     * @return
     */
    public List<String> findPricesInUSD(String product) {
        List<CompletableFuture<Double>> priceFutures = new ArrayList<>();
        for (Shop shop : shops) {
            CompletableFuture<Double> futurePriceInUSD =
                    CompletableFuture.supplyAsync(() -> shop.getPrice(product))
                            .thenCombine(
                                    CompletableFuture.supplyAsync(() -> ExchangeService.getRate(ExchangeService.Money.EUR, ExchangeService.Money.USD)),
                                    (price, rate) -> price * rate
                            );
            priceFutures.add(futurePriceInUSD);
        }

        List<String> prices = priceFutures.stream()
                .map(CompletableFuture::join)
                .map(price -> "price is " + price)
                .collect(Collectors.toList());

        return prices;

    }

    public List<String> findPricesInUSDJava7(String product) {

        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Future<Double>> priceFutures = new ArrayList<>();
        for (Shop shop : shops) {
            final Future<Double> futureRate = executorService
                    .submit(() -> ExchangeService.getRate(ExchangeService.Money.EUR, ExchangeService.Money.USD));
            // 阻塞获取 汇率
            Future<Double> futurePriceInUSD = executorService.submit(() -> {
                double priceInEUR = shop.getPrice(product);
                return priceInEUR * futureRate.get();
            });
            priceFutures.add(futurePriceInUSD);
        }

        List<String> prices = new ArrayList<>();
        for (Future<Double> priceFuture : priceFutures) {
            try {
                prices.add("price is " + priceFuture.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return prices;

    }

    public List<String> findPricesInUSD2(String product) {

        List<CompletableFuture<String>> priceFutures = new ArrayList<>();
        for (Shop shop : shops) {
            CompletableFuture<String> futurePriceInUSD =
                    CompletableFuture.supplyAsync(() -> shop.getPrice(product))
                    .thenCombine(
                            CompletableFuture.supplyAsync(
                                    () -> ExchangeService.getRate(ExchangeService.Money.EUR, ExchangeService.Money.USD)),
                            (price,rate) -> price * rate)
                    .thenApply(price -> "price is " + price);
            priceFutures.add(futurePriceInUSD);
        }

        List<String> prices = priceFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        return prices;

    }

    public List<String> findPricesInUSD3(String product) {

        List<CompletableFuture<String>> priceFutures = shops.stream()
                .map(shop -> CompletableFuture
                        .supplyAsync(() -> shop.getPrice(product))
                        .thenCombine(
                            CompletableFuture.supplyAsync(
                                    () -> ExchangeService.getRate(ExchangeService.Money.EUR, ExchangeService.Money.USD)),
                            (price,rate) -> price * rate)
                        .thenApply(price -> shop.getName() + "price is" + price))
                .collect(Collectors.toList());

        List<String> prices = priceFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        return prices;

    }


}
