package com.opdar.framework.template.base;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.math.BigDecimal;
import java.nio.CharBuffer;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by 俊帆 on 2015/8/4.
 */
public class Variable {
    String name;
    Object exp;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getExp() {
        return exp;
    }

    public void setExp(Object exp) {
        this.exp = exp;
    }

    public Variable(String var) {
        var = var.substring(4);
        String[] t = var.split("=");
        name = t[0].trim();
        exp = t[1].trim();
    }

    @Override
    public String toString() {
        return exp+"";
    }
}
