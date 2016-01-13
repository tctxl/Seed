package com.opdar.seed.io.utils;

import com.opdar.framework.utils.Utils;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by 俊帆 on 2016/1/13.
 */
public class ByteArrayCompare {

    private byte[] prototype;
    private String compareName;

    public ByteArrayCompare(byte[] prototype, String compareName) {
        this.prototype = prototype;
        this.compareName = compareName;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            Class clz = obj.getClass();
            Method method = clz.getMethod("get" + Utils.testField(compareName));
            method.setAccessible(true);
            byte[] result = (byte[]) method.invoke(obj);
            return Arrays.equals(prototype, result);
        } catch (Exception ignored) {
        }
        return super.equals(obj);
    }
}
