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

    private static ConcurrentHashMap<Class<?>,ThreadLocal<IDao>> concurrentHashMap = new ConcurrentHashMap<Class<?>,ThreadLocal<IDao>>();

    public static IDao get(Class<?> cls){
        if(concurrentHashMap.containsKey(cls)){
            ThreadLocal<IDao> dao = concurrentHashMap.get(cls);
            return dao.get();
        }
        return null;
    }

    public static IDao put(Class<?> cls,IDao dao){
        if(!concurrentHashMap.containsKey(cls)){
            ThreadLocal tdao = new ThreadLocal<IDao>();
            tdao.set(dao);
            concurrentHashMap.put(cls, tdao);
        }
        return dao;
    }
}
