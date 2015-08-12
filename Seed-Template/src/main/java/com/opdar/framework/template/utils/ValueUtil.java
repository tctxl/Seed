package com.opdar.framework.template.utils;

import com.opdar.framework.aop.interfaces.SeedExcuteItrf;
import com.opdar.framework.utils.Utils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by 俊帆 on 2015/8/4.
 */
public class ValueUtil {
    public static Object get(Object values,String fieldName){
        if(values instanceof Map){
            if(!((Map) values).containsKey(fieldName))return null;
            return ((Map) values).get(fieldName);
        }else{
            if(values instanceof SeedExcuteItrf){
                Object value = ((SeedExcuteItrf) values).invokeMethod("get".concat(Utils.testField(fieldName)));
                return value;
            }else{
                Class clz = values.getClass();
                try {
                    Field field = clz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Object o = field.get(values);
                    return o;
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }
}
