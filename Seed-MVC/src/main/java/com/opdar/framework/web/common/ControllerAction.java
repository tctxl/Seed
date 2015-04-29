package com.opdar.framework.web.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeffrey on 2015/4/29.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class ControllerAction {
    private List<Router> routers = new ArrayList<Router>();
    private Class<?> after;
    private Class<?> before;

    public List<Router> getRouters() {
        return routers;
    }

    public void setRouters(List<Router> routers) {
        this.routers = routers;
    }

    public Class<?> getAfter() {
        return after;
    }

    public void setAfter(Class<?> after) {
        this.after = after;
    }

    public Class<?> getBefore() {
        return before;
    }

    public void setBefore(Class<?> before) {
        this.before = before;
    }
}
