package com.jwzhoug.fremework.aop.aspect;

import com.jwzhoug.fremework.aop.intercept.GMethodInterceptor;
import com.jwzhoug.fremework.aop.intercept.GMethodInvocation;

import java.lang.reflect.Method;

/**
 * Before拦截器
 *
 * @author: zhoujw
 * @Date: 2019-04-24
 */
public class GMethodBeforeAdviceInterceptor extends GAbstractAspectAdvice implements GMethodInterceptor {

    private GJoinPoint joinPoint;

    public GMethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(GMethodInvocation invocation) throws Exception {
        this.joinPoint = invocation;
        before(invocation.getMethod(),invocation.getArguments(),invocation.getThis());
        return invocation.proceed();
    }

    private void before(Method method, Object[] arguments, Object aThis) throws Exception {
        super.invokeAdviceMethod(joinPoint,null,null);
    }
}
