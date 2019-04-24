package com.jwzhoug.fremework.context;

import com.jwzhoug.fremework.annotation.GPAutowired;
import com.jwzhoug.fremework.annotation.GPController;
import com.jwzhoug.fremework.annotation.GPService;
import com.jwzhoug.fremework.aop.GAopProxy;
import com.jwzhoug.fremework.aop.GCglibAopProxy;
import com.jwzhoug.fremework.aop.GJdkDynamicAopProxy;
import com.jwzhoug.fremework.aop.config.GAopConfig;
import com.jwzhoug.fremework.aop.support.GAdvisedSupport;
import com.jwzhoug.fremework.beans.factory.GBeanWrapper;
import com.jwzhoug.fremework.beans.factory.config.GBeanDefinition;
import com.jwzhoug.fremework.beans.factory.config.GPBeanPostProcessor;
import com.jwzhoug.fremework.beans.factory.support.GBeanDefinitionReader;
import com.jwzhoug.fremework.beans.factory.support.GDefaultListableBeanFactory;
import com.jwzhoug.fremework.core.GBeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 应用上下文，按照 IOC、DI、MVC、AOP
 *
 * @author: zhoujw
 * @Date: 2019-04-19
 */
public class GApplicationContext extends GDefaultListableBeanFactory implements GBeanFactory {

    // 配置文件存放
    private String[] configLocations;
    // BeanDefinition阅读器
    private GBeanDefinitionReader reader;

    // 单例缓存IOC容器（原始实例）
    private Map<String, Object> singletonObjects = new ConcurrentHashMap<String, Object>();
    // 通用的IOC容器 （包装过实例）
    private Map<String, GBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, GBeanWrapper>();

    public GApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动应用上下文
     *
     * @throws Exception
     */
    @Override
    public void refresh() throws Exception {

        // 1. 定位，定位配置文件
        reader = new GBeanDefinitionReader(this.configLocations);

        // 2. 加载，扫描指定包路径下的类文件，把它们封装成BeanDefinition
        List<GBeanDefinition> beanDefinitions = reader.loadBeanDefinitios();

        // 3. 注册
        doRegisterBeanDefinition(beanDefinitions);

        // 4. 把不是延时加载的类，初始化
        doAutowired();
    }

    /**
     * 飞延时加载自动注入
     */
    private void doAutowired() {
        for (Map.Entry<String, GBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 初始化IOC容器
     *
     * @param beanDefinitions
     * @throws Exception
     */
    private void doRegisterBeanDefinition(List<GBeanDefinition> beanDefinitions) throws Exception {
        for (GBeanDefinition beanDefinition : beanDefinitions) {
            if (super.beanDefinitionMap.containsKey(beanDefinition.getBeanClassName())) {
                throw new Exception("The “" + beanDefinition.getFactoryBeanName() + "” is exists!!");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
    }

    /**
     * 从IOC容器中读取对应的BeanDefinition信息，通过反射创建一个实例返回。
     * Spring做法是，不会把最原始的对象放出去，会用一个BeanWrapper进行一次包装
     * 装饰器模式：
     * 1. 保留原来的OOP关系
     * 2. 方便以后的拓展
     *
     * @param beanName
     * @return
     * @throws Exception
     */
    @Override
    public Object getBean(String beanName) throws Exception {

        GBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        Object instance = null;

        //这个逻辑还不严谨，自己可以去参考Spring源码
        //工厂模式 + 策略模式
        GPBeanPostProcessor postProcessor = new GPBeanPostProcessor();

        postProcessor.postProcessBeforeInitialization(instance, beanName);

        instance = instantiateBean(beanName, beanDefinition);

        // 将实例封装到包装类中
        GBeanWrapper beanWrapper = new GBeanWrapper(instance);

        // 2、得到包装对象后，保存到 IOC容器中
        this.factoryBeanInstanceCache.put(beanName, beanWrapper);

        postProcessor.postProcessAfterInitialization(instance, beanName);

        // 3、注入
        populateBean(beanName, new GBeanDefinition(), beanWrapper);

        return this.factoryBeanInstanceCache.get(beanName).getWrapperedInstance();
    }

    private void populateBean(String beanName, GBeanDefinition gBeanDefinition, GBeanWrapper beanWrapper) {

        Object instance = beanWrapper.getWrapperedInstance();

        Class<?> clazz = beanWrapper.getWrapperedClass();

        if (!(clazz.isAnnotationPresent(GPController.class)) || clazz.isAnnotationPresent(GPService.class)) {
            return;
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(GPAutowired.class)) {
                continue;
            }
            GPAutowired autowired = field.getAnnotation(GPAutowired.class);
            String autowiredBeanName = autowired.value().trim();
            if ("".equals(autowiredBeanName)) {
                autowiredBeanName = field.getType().getName();
            }
            field.setAccessible(true);

            try {
                if (this.factoryBeanInstanceCache.get(autowiredBeanName) == null) {
                    continue;
                }
                // 基于实例属性的修改
                field.set(instance, factoryBeanInstanceCache.get(autowiredBeanName).getWrapperedInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * 创建实例
     *
     * @param beanName
     * @param beanDefinition
     * @return
     */
    private Object instantiateBean(String beanName, GBeanDefinition beanDefinition) {

        String className = beanDefinition.getBeanClassName();

        Object instance = null;

        try {

            if (this.singletonObjects.containsKey(className)) {
                instance = this.singletonObjects.get(className);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                GAdvisedSupport config = instantionAopConfig();
                config.setTarget(instance);
                config.setTargetClass(clazz);

                // 校验目标类是否匹配对应得正则
                if (config.pointCutMatch()) {
                    instance = createProxy(config).getProxy();
                }

                this.singletonObjects.put(className, instance);
                this.singletonObjects.put(beanDefinition.getFactoryBeanName(), instance);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return instance;
    }

    /**
     * 创建代理对象实例
     *
     * @param config
     * @return
     */
    private GAopProxy createProxy(GAdvisedSupport config) {

        Class<?> targetClass = config.getTargetClass();
        // 判断是否有接口实现 > 来确认 代理类型
        if (targetClass.getInterfaces().length > 0) {
            return new GJdkDynamicAopProxy(config);
        }
        return new GCglibAopProxy(config);
    }

    /**
     * 实例化 GAdviceSupport
     *
     * @return
     */
    private GAdvisedSupport instantionAopConfig() {
        GAopConfig config = new GAopConfig();
        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));
        return new GAdvisedSupport(config);
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return null;
    }

    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig() {
        return this.reader.getConfig();
    }
}
