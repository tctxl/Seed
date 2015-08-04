package com.opdar.framework.template.base;

/**
 * Created by 俊帆 on 2015/8/4.
 */
public class Printf {
    String value;
    public Printf(String value) {
        this.value = value.substring(7,value.lastIndexOf(")"));
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Printf{" +
                "value='" + value + '\'' +
                '}';
    }
}
