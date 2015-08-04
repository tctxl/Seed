package com.opdar.framework.template.expressions;

/**
 * Created by 俊帆 on 2015/8/4.
 */
public class Include {
    String value;
    public Include(String condition) {
        value = condition.substring(1,condition.length()-1);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Include{" +
                "value='" + value + '\'' +
                '}';
    }
}
