package com.jwzhoug.fremework.webmvc.servlet;

import com.jwzhoug.fremework.annotation.GPController;
import com.jwzhoug.fremework.annotation.GPRequestMapping;
import com.jwzhoug.fremework.context.GApplicationContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义Servlet启动类
 *
 * @author: zhoujw
 * @Date: 2019-04-22
 */
@Slf4j
public class GDispatcherServlet extends HttpServlet {


    private GApplicationContext context;

    private final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

    private List<GHandlerMapping> handlerMappings = new ArrayList<GHandlerMapping>();

    private Map<GHandlerMapping, GHandlerAdapter> handlerAdapters = new HashMap<GHandlerMapping, GHandlerAdapter>();

    private List<GViewResolver> viewResolvers = new ArrayList<GViewResolver>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("500 Exception,Details:\n\r" +
                    Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]]", ""));
        }

    }

    /**
     * 处理请求 > 相当于源码中 this.doService()
     *
     * @param req
     * @param resp
     */
    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {

        // 1.用请求中对应的url 获取 handlerMapping
        GHandlerMapping handler = getHandler(req);

        if (handler == null) {
            processDispatchResult(req, resp, new GModelAndView("404"));
            return;
        }

        // 2.准备调用前的参数
        GHandlerAdapter ha = getHandlerAdapter(handler);

        // 3.真正的调用方法，返回ModelAndView存储了要传上页面的值，和页面模板名称
        GModelAndView mav = ha.handle(req, resp, handler);

        processDispatchResult(req, resp, mav);

    }

    private GHandlerAdapter getHandlerAdapter(GHandlerMapping handler) {

        if (this.handlerAdapters.isEmpty()) {
            return null;
        }
        GHandlerAdapter ha = this.handlerAdapters.get(handler);
        if (ha.supports(handler)) {
            return ha;
        }
        return null;
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, GModelAndView mav) throws Exception {

        if (mav == null) {
            return;
        }

        if (this.viewResolvers.isEmpty()) {
            return;
        }

        for (GViewResolver viewResolver : this.viewResolvers) {
            // 处理mav -> 得到带渲染视图
            GView view = viewResolver.resolveViewName(mav.getViewName(), null);
            // 渲染视图
            view.render(mav.getModel(), req, resp);
            return;
        }

    }

    /**
     * 匹配到对应的GHandlerMapping
     *
     * @param req
     * @return
     */
    private GHandlerMapping getHandler(HttpServletRequest req) {

        if (this.handlerMappings.isEmpty()) {
            return null;
        }

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");

        for (GHandlerMapping handlerMapping : this.handlerMappings) {
            Matcher matcher = handlerMapping.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handlerMapping;
        }

        return null;
    }

    /**
     * 初始化
     *
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        // 1. 初始化ApplicationContext
        context = new GApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));
        // 2. 初始化Spring MVC 九大组件
        initStrategies(context);
    }

    // 初始化策略
    protected void initStrategies(GApplicationContext context) {

        //多文件上传的组件
        initMultipartResolver(context);
        //初始化本地语言环境
        initLocaleResolver(context);
        //初始化模板处理器
        initThemeResolver(context);


        //handlerMapping，必须实现
        initHandlerMappings(context);
        //初始化参数适配器，必须实现
        initHandlerAdapters(context);

        //初始化异常拦截器
        initHandlerExceptionResolvers(context);
        //初始化视图预处理器
        initRequestToViewNameTranslator(context);


        //初始化视图转换器，必须实现
        initViewResolvers(context);

        //参数缓存器
        initFlashMapManager(context);
    }

    private void initFlashMapManager(GApplicationContext context) {

    }

    private void initViewResolvers(GApplicationContext context) {

        // 拿到模板的存放目录
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File templateRootDir = new File(templateRootPath);
        String[] templates = templateRootDir.list();
        for (int i = 0; i < templates.length; i++) {
            // 兼容多模板
            this.viewResolvers.add(new GViewResolver(templateRoot));
        }

    }

    private void initRequestToViewNameTranslator(GApplicationContext context) {
    }

    private void initHandlerExceptionResolvers(GApplicationContext context) {
    }

    private void initHandlerAdapters(GApplicationContext context) {

        for (GHandlerMapping handlerMapping : this.handlerMappings) {
            this.handlerAdapters.put(handlerMapping, new GHandlerAdapter());
        }


    }

    /**
     * 将初始化的Controller中@RequestMapping的映射信息包装成 GHandlerMapping 集合信息进行存储
     *
     * @param context
     */
    private void initHandlerMappings(GApplicationContext context) {

        String[] beanNames = context.getBeanDefinitionNames();

        try {

            for (String beanName : beanNames) {

                Object controller = context.getBean(beanName);
                Class<?> clazz = controller.getClass();
                if (!clazz.isAnnotationPresent(GPController.class)) {
                    continue;
                }

                String baseUrl = "";
                // 获取Controller中@RequestMapping()的值
                if (clazz.isAnnotationPresent(GPRequestMapping.class)) {
                    GPRequestMapping requestMapping = clazz.getAnnotation(GPRequestMapping.class);
                    baseUrl = requestMapping.value();
                }

                // 获取Method配置
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (!method.isAnnotationPresent(GPRequestMapping.class)) {
                        continue;
                    }
                    GPRequestMapping requestMapping = method.getAnnotation(GPRequestMapping.class);
                    String regex = ("/" + baseUrl + "/" + requestMapping.value().replaceAll("\\*", ".*"))
                            .replaceAll("/+", "/");
                    Pattern pattern = Pattern.compile(regex);
                    this.handlerMappings.add(new GHandlerMapping(pattern, controller, method));
                    log.info("Mapped" + regex + "," + method);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initThemeResolver(GApplicationContext context) {

    }

    private void initLocaleResolver(GApplicationContext context) {

    }

    private void initMultipartResolver(GApplicationContext context) {

    }
}
