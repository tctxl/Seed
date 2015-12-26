package com.opdar.seed.io.utils;

import com.opdar.framework.utils.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by 俊帆 on 2015/12/26.
 */
public class StringCompare {
    private String compareName;
    private String value;
    public StringCompare(String compareName,String value) {
        this.compareName = compareName;
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            Class clz = obj.getClass();
            Method method = clz.getMethod("get"+Utils.testField(compareName));
            method.setAccessible(true);
            String result = (String) method.invoke(obj);
            return result.equals(value);
        } catch (Exception ignored) {
        }
        return super.equals(obj);
    }
}
