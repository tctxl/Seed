package com.opdar.seed.io.utils;

import com.opdar.framework.utils.Utils;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by 俊帆 on 2016/1/13.
 */
public class ByteArrayStartWithCompare {

    private byte[] prototype;
    private String compareName;

    public ByteArrayStartWithCompare(byte[] prototype, String compareName) {
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
            if(prototype.length > result.length)return false;
            for(int i=0;i<prototype.length;i++){
                byte b = prototype[i];
                if(b != result[i]){
                    return false;
                }
            }
            return true;
        } catch (Exception ignored) {
        }
        return super.equals(obj);
    }
}
