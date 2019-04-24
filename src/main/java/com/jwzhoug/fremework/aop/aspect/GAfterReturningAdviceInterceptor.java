package com.jwzhoug.fremework.aop.aspect;

import com.jwzhoug.fremework.aop.intercept.GMethodInterceptor;
import com.jwzhoug.fremework.aop.intercept.GMethodInvocation;

import java.lang.reflect.Method;

/**
 * After拦截器实现
 *
 * @author: zhoujw
 * @Date: 2019-04-24
 */
public class GAfterReturningAdviceInterceptor extends GAbstractAspectAdvice implements GMethodInterceptor {

    private GJoinPoint joinPoint;

    public GAfterReturningAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(GMethodInvocation invocation) throws Exception {

        Object retVal = invocation.proceed();
        this.joinPoint = invocation;
        this.afterReturning(retVal,invocation.getMethod(),invocation.getArguments(),invocation.getThis());
        return retVal;
    }

    private void afterReturning(Object retVal, Method method, Object[] arguments, Object aThis) throws Exception {
        super.invokeAdviceMethod(this.joinPoint,retVal,null);
    }
}
