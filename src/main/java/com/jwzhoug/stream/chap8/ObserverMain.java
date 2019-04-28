package com.jwzhoug.stream.chap8;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO...
 *
 * @author: zhoujw
 * @Date: 2019-04-28
 */
public class ObserverMain {


    public static void main(String[] args) {

        Feed feed = new Feed();
        feed.registerObserver(new NYTimes());
        feed.registerObserver(new Guardian());
        feed.registerObserver(new LeMonde());
        feed.notifyObservers("The queen said her favourite book is Java 8 in Action!");


        Feed feedLambda = new Feed();
        feedLambda.registerObserver(tweet -> {
            if (tweet != null && tweet.contains("money")) {
                System.out.println("Breaking news in NY!" + tweet);
            }
        });
        feedLambda.registerObserver(tweet -> {
            if (tweet != null && tweet.contains("queen")){
                System.out.println("Yet another news in London.." + tweet);
            }
        });

        feedLambda.notifyObservers("money money,my queen give me money");

    }

    interface Observer {
        void inform(String tweet);
    }

    interface Subject {
        void registerObserver(Observer o);

        void notifyObservers(String tweet);
    }

    private static class NYTimes implements Observer {

        @Override
        public void inform(String tweet) {
            if (tweet != null && tweet.contains("monev")) {
                System.out.println("Breaking news in NY!" + tweet);
            }
        }
    }

    private static class Guardian implements Observer {

        @Override
        public void inform(String tweet) {
            if (tweet != null && tweet.contains("queen")) {
                System.out.println("Yet another news in London... " + tweet);
            }
        }
    }

    static private class LeMonde implements Observer {
        @Override
        public void inform(String tweet) {
            if (tweet != null && tweet.contains("wine")) {
                System.out.println("Today cheese, wine and news! " + tweet);
            }
        }
    }

    private static class Feed implements Subject {

        private final List<Observer> observers = new ArrayList<>();

        @Override
        public void registerObserver(Observer o) {
            this.observers.add(o);
        }

        @Override
        public void notifyObservers(String tweet) {
            observers.forEach(o -> o.inform(tweet));
        }
    }
}
