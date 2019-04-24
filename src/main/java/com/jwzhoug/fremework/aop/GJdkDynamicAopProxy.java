package com.jwzhoug.fremework.aop;

import com.jwzhoug.fremework.aop.intercept.GMethodInvocation;
import com.jwzhoug.fremework.aop.support.GAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * TODO...
 *
 * @author: zhoujw
 * @Date: 2019-04-24
 */
public class GJdkDynamicAopProxy implements GAopProxy, InvocationHandler {

    private GAdvisedSupport advised;

    public GJdkDynamicAopProxy(GAdvisedSupport config) {
        this.advised = config;
    }

    @Override
    public Object getProxy() {
        return getProxy(this.advised.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader,this.advised.getTargetClass().getInterfaces(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        List<Object> interceptorsAndDynamicMethodMatchers =
                this.advised.getInterceptorsAndDynamicInterceptionAdvice(method,this.advised.getTargetClass());
        GMethodInvocation invocation = new GMethodInvocation(proxy,method,this.advised.getTarget(),
                args,interceptorsAndDynamicMethodMatchers,this.advised.getTargetClass());
        return invocation.proceed();
    }
}
