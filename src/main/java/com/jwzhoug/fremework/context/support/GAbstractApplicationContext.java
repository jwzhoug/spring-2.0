package com.jwzhoug.fremework.context.support;

/**
 * IOC容器实现的顶层设计
 *
 * @author: zhoujw
 * @Date: 2019-04-19
 */
public abstract class GAbstractApplicationContext {

    /**
     * 只提供给子类重写
     *
     * @throws Exception
     */
    public void refresh() throws Exception {
    }
}
