package com.opdar.framework.server.supports.servlet;

import com.opdar.framework.web.common.ISession;

import javax.servlet.http.HttpSession;

/**
 * Created by Jeffrey on 2015/4/25.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class ServletSession implements ISession {
    private HttpSession session;
    public ServletSession(HttpSession session){
        this.session = session;
    }
    @Override
    public void setValue(String key, Object value) {
        session.setAttribute(key,value);
    }

    @Override
    public Object getValue(String key) {
        return getValue(key,null);
    }

    @Override
    public Object getValue(String key, String defaultValue) {
        Object value = session.getAttribute(key);
        return value == null?defaultValue:value;
    }
}
