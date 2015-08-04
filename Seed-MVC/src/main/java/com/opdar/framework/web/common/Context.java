package com.opdar.framework.web.common;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 功   能:
 * 创建者: 施俊帆
 * 日   期: 2015/3/15 2:00
 * Q  Q: 362116120
 */
public class Context {
    private static ConcurrentHashMap<String,Object> pObjects = new ConcurrentHashMap<String,Object>();

    public static void print(){
        System.out.println(pObjects);
        System.out.println(pObjects.hashCode());
    }

    public static void add(Class clz){
        String typeName = clz.getName();
        Object o = null;
        try {
            o = clz.newInstance();
            pObjects.put(typeName,o);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void add(Object o){
        String typeName = o.getClass().getName();
        pObjects.put(typeName,o);
    }

    public static void add(String key,Object o){
        pObjects.put(key,o);
    }

    public static boolean constains(Class<?> type){
        String typeName = type.getName();
        return pObjects.containsKey(typeName);
    }

    public static <T>T get(Class<T> type){
        String typeName = type.getName();
        return get(typeName);
    }

    public static <T>T get(String typeName){
        return (T) pObjects.get(typeName);
    }

}
