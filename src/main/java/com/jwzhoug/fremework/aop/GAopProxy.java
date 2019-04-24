package com.jwzhoug.fremework.aop;

/**
 * Aop接口
 *
 * @author: zhoujw
 * @Date: 2019-04-24
 */
public interface GAopProxy {

    Object getProxy();

    Object getProxy(ClassLoader classLoader);

}
