package com.jwzhoug.demo.action;


import com.jwzhoug.demo.service.IModifyService;
import com.jwzhoug.demo.service.IQueryService;
import com.jwzhoug.fremework.annotation.GPAutowired;
import com.jwzhoug.fremework.annotation.GPController;
import com.jwzhoug.fremework.annotation.GPRequestMapping;
import com.jwzhoug.fremework.annotation.GPRequestParam;
import com.jwzhoug.fremework.webmvc.servlet.GModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 公布接口url
 *
 * @author Tom
 */
@GPController
@GPRequestMapping("/web")
public class MyAction {

    @GPAutowired
    IQueryService queryService;
    @GPAutowired
    IModifyService modifyService;

    @GPRequestMapping("/query.json")
    public void query(HttpServletRequest request, HttpServletResponse response,
                      @GPRequestParam("name") String name) {
        String result = queryService.query(name);
        out(response, result);
    }

    @GPRequestMapping("/add*.json")
    public GModelAndView add(HttpServletRequest request, HttpServletResponse response,
                             @GPRequestParam("name") String name, @GPRequestParam("addr") String addr) {
        String result = null;
        try {
            result = modifyService.add(name, addr);
            return outView(response, result);
        } catch (Exception e) {
//			e.printStackTrace();
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("detail", e.getMessage());
            model.put("stackTrace", Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]", ""));
            return new GModelAndView("500", model);
        }
    }


    @GPRequestMapping("/remove.json")
    public void remove(HttpServletRequest request, HttpServletResponse response,
                       @GPRequestParam("id") Integer id) {
        String result = modifyService.remove(id);
        out(response, result);
    }

    @GPRequestMapping("/edit.json")
    public void edit(HttpServletRequest request, HttpServletResponse response,
                     @GPRequestParam("id") Integer id,
                     @GPRequestParam("name") String name) {
        String result = modifyService.edit(id, name);
        out(response, result);
    }


    private void out(HttpServletResponse resp, String str) {
        try {
            resp.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回视图信息
     *
     * @param resp
     * @param str
     * @return
     */
    private GModelAndView outView(HttpServletResponse resp, String str) {
        try {
            resp.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
