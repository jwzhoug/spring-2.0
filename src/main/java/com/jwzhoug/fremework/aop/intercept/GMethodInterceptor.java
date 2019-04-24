package com.jwzhoug.fremework.aop.intercept;

/**
 * 拦截器接口
 *
 * @author: zhoujw
 * @Date: 2019-04-24
 */
public interface GMethodInterceptor {
    Object invoke(GMethodInvocation invocation) throws Exception;
}
