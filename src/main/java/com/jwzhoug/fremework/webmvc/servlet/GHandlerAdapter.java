package com.jwzhoug.fremework.webmvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO...
 *
 * @author: zhoujw
 * @Date: 2019-04-22
 */
public class GHandlerAdapter {

    public boolean supports(Object handler) {
        return (handler instanceof GHandlerMapping);
    }

    GModelAndView handle(HttpServletRequest request, HttpServletResponse response,Object handler){
        return null;
    }

}
