package com.jwzhoug.fremework.beans.factory.support;

import com.jwzhoug.fremework.beans.factory.config.GBeanDefinition;
import com.jwzhoug.fremework.context.support.GAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 顶层设计的最外层实现
 *
 * @author: zhoujw
 * @Date: 2019-04-19
 */
public class GDefaultListableBeanFactory extends GAbstractApplicationContext {

    // 存储注册信息
    protected final Map<String, GBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, GBeanDefinition>();
}
