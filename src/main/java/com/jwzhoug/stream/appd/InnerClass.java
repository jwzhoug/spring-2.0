package com.jwzhoug.stream.appd;

import java.util.function.Function;

/**
 * TODO...
 *
 * @author: zhoujw
 * @Date: 2019-04-25
 */
public class InnerClass {

    Function<Object, String> f = new Function<Object, String>() {
        @Override
        public String apply(Object o) {
            return o.toString();
        }
    };
}
