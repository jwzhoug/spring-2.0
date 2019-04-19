package com.jwzhoug.fremework.core;

/**
 * 单例工厂的顶层设计
 *
 * @author: zhoujw
 * @Date: 2019-04-19
 */
public interface GBeanFactory {

    /**
     * 根据beanName从IOC容器中获取一个实例Bean
     *
     * @param beanName
     * @return
     * @throws Exception
     */
    Object getBean(String beanName) throws Exception;

    Object getBean(Class<?> beanClass) throws Exception;
}
