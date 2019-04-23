package com.jwzhoug.fremework.beans.factory.support;

import com.jwzhoug.fremework.beans.factory.config.GBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * BeanDefinition读取
 *
 * @author: zhoujw
 * @Date: 2019-04-19
 */
public class GBeanDefinitionReader {

    // 注册Bean
    private List<String> registyBeanClasses = new ArrayList<String>();

    // 文件配置
    private Properties config = new Properties();

    // 固定配置文件中的key
    private final String SCAN_PACKAGE = "scanPackage";

    /**
     * 构造器，读取配置文件，扫描配置文件中设置的包路径
     *
     * @param locations
     */
    public GBeanDefinitionReader(String... locations) {
        // 通过URL定位找到其对应的文件，然后转换成文件流
        InputStream is = this.getClass().getClassLoader()
                .getResourceAsStream(locations[0].replace("classpath:", ""));
        try {
            config.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // 扫描指定包路径
        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    /**
     * 扫描指定包路径
     *
     * @param scanPackage
     */
    private void doScanner(String scanPackage) {
        // 转换为 文件路径
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String className = scanPackage + "." + file.getName().replace(".class", "");
                // 将扫描到的className 放入待注册集合中
                registyBeanClasses.add(className);
            }
        }
    }

    public Properties getConfig() {
        return config;
    }

    public List<GBeanDefinition> loadBeanDefinitios() {
        List<GBeanDefinition> result = new ArrayList<GBeanDefinition>();

        try {
            for (String className : registyBeanClasses) {
                Class<?> beanClass = Class.forName(className);
                // 如果是接口，是不能实例化的
                if (beanClass.isInterface()) {
                    continue;
                }

                // beanName有三种情况
                // 1，默认是类名首字母小写
                // 2.自定义名字
                // 3.接口注入
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));

                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> i : interfaces) {
                    // 如果是多个实现类，只能覆盖
                    result.add(doCreateBeanDefinition(i.getName(), beanClass.getName()));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    /**
     * 把配置信息解析成一个BeanDefinition
     *
     * @param factoryBeanName
     * @param beanClassName
     * @return
     */
    private GBeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName) {
        GBeanDefinition beanDefinition = new GBeanDefinition();
        beanDefinition.setFactoryBeanName(factoryBeanName);
        beanDefinition.setBeanClassName(beanClassName);
        return beanDefinition;
    }

    /**
     * 首字母小写
     *
     * @param simpleName
     * @return
     */
    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        // 在Java中，对char做数学运算，实际上就是对ASCII码做数学运算
        chars[0] += 32;
        return String.valueOf(chars);
    }


}
