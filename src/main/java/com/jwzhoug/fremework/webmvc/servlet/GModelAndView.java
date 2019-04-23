package com.jwzhoug.fremework.webmvc.servlet;

import java.util.Map;

/**
 * 视图模型
 *
 * @author: zhoujw
 * @Date: 2019-04-22
 */
public class GModelAndView {

    private String viewName;
    private Map<String,?> model;

    public GModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public GModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

//    public void setViewName(String viewName) {
//        this.viewName = viewName;
//    }

    public Map<String, ?> getModel() {
        return model;
    }

//    public void setModel(Map<String, ?> model) {
//        this.model = model;
//    }
}
