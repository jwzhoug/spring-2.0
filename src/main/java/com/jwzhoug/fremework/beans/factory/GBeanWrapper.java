package com.jwzhoug.fremework.beans.factory;

/**
 * BeanDefinition包装类 > 不直接返回BeanDefinition实例
 *
 * @author: zhoujw
 * @Date: 2019-04-19
 */
public class GBeanWrapper {

    private Object wrapperedInstance;
    private Class<?> wrapperedClass;

    public GBeanWrapper(Object wrapperedInstance) {
        this.wrapperedInstance = wrapperedInstance;
    }

    public Object getWrapperedInstance() {
        return wrapperedInstance;
    }

    /**
     * 返回包装后的Class对象
     *
     * @return
     */
    public Class<?> getWrapperedClass() {
        return this.wrapperedInstance.getClass();
    }
}
