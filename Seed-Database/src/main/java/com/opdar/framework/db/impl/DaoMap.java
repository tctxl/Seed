package com.opdar.framework.db.impl;

import com.opdar.framework.db.interfaces.IDao;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Jeffrey on 2015/4/23.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class DaoMap {

    private static ConcurrentHashMap<Class<?>,IDao> concurrentHashMap = new ConcurrentHashMap<Class<?>,IDao>();

    public static IDao get(Class<?> cls){
        IDao dao = null;
        if(concurrentHashMap.containsKey(cls)){
            dao = concurrentHashMap.get(cls);
        }
        return dao;
    }

    public static IDao put(Class<?> cls,IDao dao){
        if(!concurrentHashMap.containsKey(cls)){
            dao = concurrentHashMap.put(cls, dao);
        }
        return dao;
    }
}
