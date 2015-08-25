package com.opdar.framework.server.supports.servlet;

import com.opdar.framework.web.common.IResponse;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Jeffrey on 2015/4/27.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class SSResponse implements IResponse {
    private HttpServletResponse res;
    SSResponse(HttpServletResponse res){
        this.res = res;
    }

    @Override
    public void write(byte[] content, String contentType, int responseCode) {
        try {
            res.setContentType(contentType);
            res.setStatus(responseCode);
            res.getOutputStream().write(content, 0, content.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeAndFlush(byte[] content, String contentType, int responseCode) {
        write(content,contentType,responseCode);
        flush();
    }

    @Override
    public void flush() {
        try {
            res.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setHeader(String key, String value) {
        res.setHeader(key,value);
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        for(Iterator<Map.Entry<String, String>> it = headers.entrySet().iterator();it.hasNext();){
            Map.Entry<String, String> entry = it.next();
            res.setHeader(entry.getKey(),entry.getValue());
        }
    }

    @Override
    public void addCookie(String key, String value) {
        res.addCookie(new Cookie(key,value));
    }
}
