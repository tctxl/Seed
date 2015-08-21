package com.opdar.framework.web.common;

/**
 * Created by 俊帆 on 2015/8/21.
 */
public class ComponentMethod {
    private String name;
    private Object[] args;

    public ComponentMethod(String name, Object[] args) {
        this.name = name;
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
