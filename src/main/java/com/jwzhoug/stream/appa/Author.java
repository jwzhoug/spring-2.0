package com.jwzhoug.stream.appa;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Repeatable 元注解
 *
 * @author: zhoujw
 * @Date: 2019-04-25
 */

@Repeatable(Authors.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Author {
    String name();
}
