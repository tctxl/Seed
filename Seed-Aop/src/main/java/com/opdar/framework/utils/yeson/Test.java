package com.opdar.framework.utils.yeson;

import java.util.List;

/**
 * Created by 俊帆 on 2015/8/14.
 */
public class Test<T,V> {
    String a;
    private List<Test> list;
    T t2;

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public List<Test> getList() {
        return list;
    }

    public void setList(List<Test> list) {
        this.list = list;
    }

    public T getT2() {
        return t2;
    }

    public void setT2(T t2) {
        this.t2 = t2;
    }

    @Override
    public String toString() {
        return "Test{" +
                "a='" + a + '\'' +
                ", list=" + list +
                '}';
    }
}
