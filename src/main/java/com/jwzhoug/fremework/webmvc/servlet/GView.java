package com.jwzhoug.fremework.webmvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO...
 *
 * @author: zhoujw
 * @Date: 2019-04-23
 */
public class GView {

    private final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";

    private File viewFile;

    public GView(File viewFile) {
        this.viewFile = viewFile;
    }

    public void render(Map<String,?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        StringBuffer sb = new StringBuffer();

        RandomAccessFile ra = new RandomAccessFile(this.viewFile,"r");

        String line = null;
        while (null != (line = ra.readLine())){
            line = new String(line.getBytes("ISO-8859-1"),"UTF-8");
            // 注意正则处理
            Pattern pattern = Pattern.compile("￥\\{[^\\}]+\\}",Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()){
                String paramName = matcher.group();
                paramName = paramName.replaceAll("￥\\{|\\}","");
                Object paramValue = model.get(paramName);
                if (paramValue == null) {
                    continue;
                }
                line = matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
                // 当前行内容进行重复比对
                matcher = pattern.matcher(line);
            }
            sb.append(line);
        }

        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(sb.toString());

    }


    //处理特殊字符
    public static String makeStringForRegExp(String str) {
        return str.replace("\\", "\\\\").replace("*", "\\*")
                .replace("+", "\\+").replace("|", "\\|")
                .replace("{", "\\{").replace("}", "\\}")
                .replace("(", "\\(").replace(")", "\\)")
                .replace("^", "\\^").replace("$", "\\$")
                .replace("[", "\\[").replace("]", "\\]")
                .replace("?", "\\?").replace(",", "\\,")
                .replace(".", "\\.").replace("&", "\\&");
    }
}
