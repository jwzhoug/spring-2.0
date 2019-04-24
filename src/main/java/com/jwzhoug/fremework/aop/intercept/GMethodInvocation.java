package com.jwzhoug.fremework.aop.intercept;

import com.jwzhoug.fremework.aop.aspect.GJoinPoint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * TODO...
 *
 * @author: zhoujw
 * @Date: 2019-04-24
 */
public class GMethodInvocation implements GJoinPoint {

    private Object proxy;
    private Method method;
    private Object target;
    private Object[] arguments;
    private List<Object> interceptorsAndDynamicMethodMatchers;
    private Class<?> targetClass;

    private Map<String, Object> userAttributes;

    // 定义一个索引，从-1开始记录当前拦截器执行的位置
    private int currentInterceptorIndex = -1;

    public GMethodInvocation(Object proxy, Method method, Object target,
                             Object[] arguments, List<Object> interceptorsAndDynamicMethodMatchers,
                             Class<?> targetClass) {
        this.proxy = proxy;
        this.method = method;
        this.target = target;
        this.arguments = arguments;
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
        this.targetClass = targetClass;
    }

    public Object proceed() throws Exception {

        // 如果Interceptor执行完了，则执行JoinPoint
        if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
            return this.method.invoke(this.target, this.arguments);
        }

        Object interceptorOrInterceptionAdvice =
                this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);

        // 如果要动态匹配JoinPoint
        if (interceptorOrInterceptionAdvice instanceof GMethodInterceptor){
            GMethodInterceptor mi = (GMethodInterceptor) interceptorOrInterceptionAdvice;
            return mi.invoke(this);
        }else {
            // 动态匹配失败，掠过当前Interceptor，调用下一个Interceptor
            return proceed();
        }

    }

    @Override
    public Object getThis() {
        return this.target;
    }

    @Override
    public Object[] getArguments() {
        return new Object[0];
    }

    @Override
    public Method getMethod() {
        return null;
    }

    @Override
    public void setUserAttribute(String key, Object value) {

    }

    @Override
    public Object getUserAttribute(String key) {
        return null;
    }
}
