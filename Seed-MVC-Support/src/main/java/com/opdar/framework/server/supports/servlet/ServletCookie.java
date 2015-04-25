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

    public void setComment(String purpose) {
        cookie.setComment(purpose);
    }

    public boolean getSecure() {
        return cookie.getSecure();
    }

    public void setPath(String uri) {
        cookie.setPath(uri);
    }

    public void setValue(String newValue) {
        cookie.setValue(newValue);
    }

    public String getValue() {
        return cookie.getValue();
    }

    public int getMaxAge() {
        return cookie.getMaxAge();
    }

    public String getComment() {
        return cookie.getComment();
    }

    public void setHttpOnly(boolean isHttpOnly) {
        cookie.setHttpOnly(isHttpOnly);
    }

    public String getPath() {
        return cookie.getPath();
    }

    public void setMaxAge(int expiry) {
        cookie.setMaxAge(expiry);
    }

    public void setDomain(String domain) {
        cookie.setDomain(domain);
    }

    public int getVersion() {
        return cookie.getVersion();
    }

    public void setSecure(boolean flag) {
        cookie.setSecure(flag);
    }

    public String getName() {
        return cookie.getName();
    }

    public boolean isHttpOnly() {
        return cookie.isHttpOnly();
    }

    public String getDomain() {
        return cookie.getDomain();
    }

    public void setVersion(int v) {
        cookie.setVersion(v);
    }
}
