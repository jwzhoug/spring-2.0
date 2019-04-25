package com.jwzhoug.stream.appa;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 容器注解类
 *
 * @author: zhoujw
 * @Date: 2019-04-25
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Authors {
    Author[] value();
}
