package com.jwzhoug.fremework.beans.factory.config;

/**
 * 包装，实例初始化前后 进行额外的动作
 */
public class GPBeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }
}
