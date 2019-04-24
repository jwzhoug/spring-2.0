package com.jwzhoug.fremework.aop;

import com.jwzhoug.fremework.aop.support.GAdvisedSupport;

/**
 * TODO...
 *
 * @author: zhoujw
 * @Date: 2019-04-24
 */
public class GCglibAopProxy implements GAopProxy{

    public GCglibAopProxy(GAdvisedSupport config) {
    }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
