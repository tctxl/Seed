package com.opdar.framework.template.base;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by 俊帆 on 2015/8/4.
 */
public class Variable {
    String name, value;
    String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Variable(String var) {
        var = var.substring(4);
        String[] p = var.split("=");
        if (p.length > 1) {
            name = p[0];
            value = p[1].trim();
            try {
                NumberFormat.getInstance().parse(value);
                type = "number";
            } catch (ParseException e) {
                if (value.equals("true") || value.equals("false")) {
                    type = "bool";
                } else if ((value.indexOf("\"") == 0 && value.lastIndexOf("\"") == value.length() - 1) || (value.indexOf("'") == 0 && value.lastIndexOf("'") == value.length() - 1)) {
                    type = "string";
                    value = value.substring(1, value.length() - 1);
                }else{
                    //error
                }
            }
        }
    }

    public static void main(String[] args) {
    }

    @Override
    public String toString() {
        return type+"["+value+"]";
    }
}
