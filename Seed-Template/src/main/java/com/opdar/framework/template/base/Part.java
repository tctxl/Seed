package com.opdar.framework.template.base;

import java.util.LinkedList;

/**
 * Created by 俊帆 on 2015/8/4.
 */
public class Part {
    LinkedList<Object> parts = new LinkedList<Object>();
    public void addPart(Object o){
        parts.add(o);
    }

    public LinkedList<Object> getParts() {
        return parts;
    }

    @Override
    public String toString() {
        return "Part{" +
                parts +
                '}';
    }
}
