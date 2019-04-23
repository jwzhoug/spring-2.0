package com.jwzhoug.fremework.webmvc.servlet;

import java.io.File;
import java.util.Locale;

/**
 * 视图处理
 *
 * @author: zhoujw
 * @Date: 2019-04-23
 */
public class GViewResolver {

    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";

    private File templateRootDir;

    public GViewResolver(String templateRootDir) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRootDir).getFile();
        this.templateRootDir = new File(templateRootPath);
    }


    public GView resolveViewName(String viewName, Locale locale){
        if (null == viewName || "".equals(viewName.trim())){return null;}
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFIX);
        File templateFile = new File((templateRootDir.getPath()+ "/" + viewName).replaceAll("/+","/"));
        return new GView(templateFile);
    }

}
