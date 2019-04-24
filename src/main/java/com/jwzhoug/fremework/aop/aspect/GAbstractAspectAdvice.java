package com.jwzhoug.fremework.aop.aspect;

import java.awt.image.AreaAveragingScaleFilter;
import java.lang.reflect.Method;

/**
 * TODO...
 *
 * @author: zhoujw
 * @Date: 2019-04-24
 */
public abstract class GAbstractAspectAdvice implements GAdvice {

    private Method aspectMethod;
    private Object aspectTarget;

    public GAbstractAspectAdvice(Method aspectMethod, Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }

    public Object invokeAdviceMethod(GJoinPoint joinPoint, Object returnValue, Throwable tx) throws Exception {

        Class<?>[] paramTypes = this.aspectMethod.getParameterTypes();

        // 确认参数有无
        if (paramTypes == null || paramTypes.length == 0) {
            return this.aspectMethod.invoke(aspectTarget);
        } else {
            // 实参组合
            Object[] args = new Object[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                if (paramTypes[i] == GJoinPoint.class) {
                    args[i] = joinPoint;
                } else if (paramTypes[i] == Object.class) {
                    args[i] = returnValue;
                } else if (paramTypes[i] == Throwable.class) {
                    args[i] = tx;
                }
            }
            // 调用LogAspect实例得方法
            return this.aspectMethod.invoke(aspectTarget, args);
        }
    }


}
