package com.jwzhoug.fremework.aop.support;

import com.jwzhoug.fremework.aop.aspect.GAfterReturningAdviceInterceptor;
import com.jwzhoug.fremework.aop.aspect.GMethodBeforeAdviceInterceptor;
import com.jwzhoug.fremework.aop.config.GAopConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO...
 *
 * @author: zhoujw
 * @Date: 2019-04-24
 */
public class GAdvisedSupport {

    private Class<?> targetClass;
    private Object target;
    private GAopConfig config;
    private Pattern pointCutClassPattern;

    // 存放对应得方法，已经添加得拦截器
    private transient Map<Method, List<Object>> methodCache;

    public GAdvisedSupport(GAopConfig config) {
        this.config = config;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Object getTarget() {
        return target;
    }

    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws Exception {
        List<Object> cached = methodCache.get(method);
        if (cached == null) {
            // 如果没取到Method，去取目标类中得方法
            Method m = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cached = methodCache.get(m);

            this.methodCache.put(m, cached);
        }
        return cached;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    private void parse() {
        String pointCut = config.getPointCut()
                .replaceAll("\\.", "\\\\.")
                .replaceAll("\\\\.\\*", ".*")
                .replaceAll("\\(", "\\\\(")
                .replaceAll("\\)", "\\\\)");

        String pointCutForClassRegex = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);
        // 切点中类正则
        pointCutClassPattern = Pattern.compile("class" + pointCutForClassRegex.substring(
                pointCutForClassRegex.lastIndexOf(" ") + 1));

        try {

            methodCache = new HashMap<Method, List<Object>>();
            Pattern pattern = Pattern.compile(pointCut);

            // LogAspect > 切面中得方法与对应得拦截器 是 一一对应得
            Class aspectClass = Class.forName(this.config.getAspectClass());
            Map<String, Method> aspectMethods = new HashMap<String, Method>();
            for (Method method : aspectClass.getMethods()) {
                aspectMethods.put(method.getName(), method);
            }

            // 判断初始化BeanDefinition实例中，对应得方法是否能够匹配PointCut
            for (Method method : this.targetClass.getMethods()) {
                // 处理目标类中得方法名
                String methodString = method.toString();
                if (methodString.contains("throws")) {
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }

                // 校验pointCut正则
                Matcher matcher = pattern.matcher(methodString);
                if (matcher.matches()) {
                    // 执行器链
                    List<Object> advices = new LinkedList<Object>();
                    // 把每一个方法包装成MethodInterceptor
                    // before
                    if (!(null == config.getAspectBefore() || "".equals(config.getAspectBefore()))) {
                        // 创建一个advice
                        advices.add(new GMethodBeforeAdviceInterceptor(aspectMethods.get(config.getAspectBefore()), aspectClass.newInstance()));
                    }
                    // after
                    if (!(null == config.getAspectAfter() || "".equals(config.getAspectAfter()))) {
                        // 创建一个advice
                        advices.add(new GAfterReturningAdviceInterceptor(aspectMethods.get(config.getAspectAfter()), aspectClass.newInstance()));
                    }

                    // 目标类中对应得匹配得方法传入
                    methodCache.put(method, advices);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }
}
