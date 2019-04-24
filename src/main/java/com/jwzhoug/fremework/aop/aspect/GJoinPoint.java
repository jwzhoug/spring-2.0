package com.jwzhoug.fremework.aop.aspect;

import java.lang.reflect.Method;

/**
 * 切入点接口
 *
 * @author: zhoujw
 * @Date: 2019-04-24
 */
public interface GJoinPoint {

    Object getThis();

    Object[] getArguments();

    Method getMethod();

    void setUserAttribute(String key,Object value);

    Object getUserAttribute(String key);

}
