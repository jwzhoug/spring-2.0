package com.jwzhoug.stream.chap8;

import java.util.function.Consumer;

/**
 * TODO...
 *
 * @author: zhoujw
 * @Date: 2019-04-28
 */
public class OnlineBankingLambda {

    public static void main(String[] args) {
        new OnlineBankingLambda().processCustmoer(1377, customer -> System.out.println("Happy"));
    }

    public void processCustmoer(int id, Consumer<Customer> makeCustomerHappy) {
        Customer c = Database.getCustomerWithId(id);
        makeCustomerHappy.accept(c);
    }

    private static class Customer {
    }

    private static class Database {
        static Customer getCustomerWithId(int id) {
            return new Customer();
        }
    }
}
