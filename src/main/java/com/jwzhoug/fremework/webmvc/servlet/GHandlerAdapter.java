package com.jwzhoug.fremework.webmvc.servlet;

import com.jwzhoug.fremework.annotation.GPRequestParam;
import sun.security.action.PutAllAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.print.PrinterAbortException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理适配器
 *
 * @author: zhoujw
 * @Date: 2019-04-22
 */
public class GHandlerAdapter {

    public boolean supports(Object handler) {
        // 默认支持GHandlerMapping
        return (handler instanceof GHandlerMapping);
    }

    GModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        GHandlerMapping handlerMapping = (GHandlerMapping) handler;

        // 把方法的形参列表和request的参数列表所在的顺序一一对应
        Map<String, Integer> paramIndexMapping = new HashMap<String, Integer>();

        // 提取方法中 加了注解的参数
        // 方法上的注解拿到的是一个二位数组 > 一个参数可以有多个注解，一个方法又可以有多个参数
        Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
        for (int i = 0; i < pa.length; i++) {
            for (Annotation a : pa[i]) {
                if (a instanceof GPRequestParam) {
                    String paramName = ((GPRequestParam) a).value();
                    if (!"".equals(paramName.trim())) {
                        paramIndexMapping.put(paramName, i);
                    }
                }
            }
        }

        // 提取方法中的request和response参数
        Class<?>[] paramTypes = handlerMapping.getMethod().getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> type = paramTypes[i];
            if (type == HttpServletRequest.class || type == HttpServletResponse.class) {
                paramIndexMapping.put(type.getName(), i);
            }
        }

        // 获取方法的形参列表
        Map<String, String[]> params = request.getParameterMap();

        // 实参列表
        Object[] paramValues = new Object[paramTypes.length];

        for (Map.Entry<String, String[]> param : params.entrySet()) {
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "")
                    .replaceAll("\\s", ",");
            if (!paramIndexMapping.containsKey(param.getKey())) {
                continue;
            }
            int index = paramIndexMapping.get(param.getKey());
            paramValues[index] = caseStringValue(value, paramTypes[index]);
        }

        if (paramIndexMapping.containsKey(HttpServletRequest.class.getName())) {
            int reqIndex = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = request;
        }

        if (paramIndexMapping.containsKey(HttpServletResponse.class.getName())) {
            int respIndex = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = response;
        }

        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(), paramValues);
        if (result == null || result instanceof Void) {
            return null;
        }

        boolean isModelAndView = handlerMapping.getMethod().getReturnType() == GModelAndView.class;
        if (isModelAndView){
            return (GModelAndView) result;
        }

        return null;
    }

    /**
     * 转换对应的类型值
     *
     * @param value
     * @param paramType
     * @return
     */
    private Object caseStringValue(String value, Class<?> paramType) {

        if (String.class == paramType) {
            return value;
        }
        if (Integer.class == paramType) {
            return Integer.valueOf(value);
        }
        if (Double.class == paramType) {
            return Double.valueOf(value);
        } else {
            if (value != null) {
                return value;
            }
            return null;
        }

        // 还存在其他类型
    }

}
