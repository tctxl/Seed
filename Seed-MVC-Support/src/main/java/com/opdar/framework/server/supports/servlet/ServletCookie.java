package com.opdar.framework.server.supports.servlet;

import com.opdar.framework.web.common.ICookie;

import javax.servlet.http.Cookie;

/**
 * Created by Jeffrey on 2015/4/25.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class ServletCookie implements ICookie {
    private Cookie cookie;
    public ServletCookie(Cookie cookie){
        this.cookie = cookie;
    }

    @Override
    public void setComment(String purpose) {
        cookie.setComment(purpose);
    }
    @Override
    public boolean getSecure() {
        return cookie.getSecure();
    }

    @Override
    public void setPath(String uri) {
        cookie.setPath(uri);
    }

    @Override
    public void setValue(String newValue) {
        cookie.setValue(newValue);
    }

    @Override
    public String getValue() {
        return cookie.getValue();
    }

    @Override
    public int getMaxAge() {
        return cookie.getMaxAge();
    }

    @Override
    public String getComment() {
        return cookie.getComment();
    }

    @Override
    public void setHttpOnly(boolean isHttpOnly) {
        cookie.setHttpOnly(isHttpOnly);
    }

    @Override
    public String getPath() {
        return cookie.getPath();
    }

    @Override
    public void setMaxAge(int expiry) {
        cookie.setMaxAge(expiry);
    }

    @Override
    public void setDomain(String domain) {
        cookie.setDomain(domain);
    }

    @Override
    public int getVersion() {
        return cookie.getVersion();
    }

    @Override
    public void setSecure(boolean flag) {
        cookie.setSecure(flag);
    }

    @Override
    public String getName() {
        return cookie.getName();
    }

    @Override
    public boolean isHttpOnly() {
        return cookie.isHttpOnly();
    }

    @Override
    public String getDomain() {
        return cookie.getDomain();
    }

    @Override
    public void setVersion(int v) {
        cookie.setVersion(v);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Cookie){
            return cookie.getName().equals(obj);
        }
        return super.equals(obj);
    }
}
