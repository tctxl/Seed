package com.opdar.seed.extra.views;

import com.opdar.framework.template.parser.ClassPathResourceLoader;
import com.opdar.framework.web.interfaces.View;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by Jeffrey on 2015/4/25.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class TemplateView implements View {
    String contentType = "text/html";

    String templatePath = null;
    Object dataModels = null;
    private static ClassLoader classLoader = TemplateView.class.getClassLoader();

    private static final  ClassPathResourceLoader loader = new ClassPathResourceLoader(classLoader);

    public TemplateView(String templatePath, Object dataModels) {
        this.templatePath = templatePath;
        this.dataModels = dataModels;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Map<String, String> headers() {
        return null;
    }

    public byte[] renderView() {
        try {
            return loader.parse(templatePath, dataModels).getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public String contentType() {
        return contentType;
    }

    public int getCode() {
        return 200;
    }
}
