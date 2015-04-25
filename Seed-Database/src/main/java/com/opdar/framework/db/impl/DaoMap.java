package com.opdar.framework.db.impl;

import com.opdar.framework.db.interfaces.IDao;
import com.opdar.framework.utils.ThreadLocalUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Jeffrey on 2015/4/23.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class DaoMap {
    private static final String DATABASE_THREAD_KEY = "DATABASE_THREAD_KEY";

    private static ConcurrentHashMap<Class<?>,ThreadLocal<IDao>> concurrentHashMap = new ConcurrentHashMap<Class<?>,ThreadLocal<IDao>>();


    public static void clear(){
        for(Iterator<Map.Entry<Class<?>, ThreadLocal<IDao>>> it = concurrentHashMap.entrySet().iterator();it.hasNext();){
            Map.Entry<Class<?>, ThreadLocal<IDao>> entry = it.next();
            ThreadLocal local = entry.getValue();
            ThreadLocalUtils.clearThreadLocals(DATABASE_THREAD_KEY,local);
        }
        concurrentHashMap.clear();
    }

    public static IDao get(Class<?> cls){
        if(concurrentHashMap.containsKey(cls)){
            ThreadLocal<IDao> dao = concurrentHashMap.get(cls);
            return dao.get();
        }
        return null;
    }

    public static IDao put(Class<?> cls,IDao dao){
        ThreadLocalUtils.record(DATABASE_THREAD_KEY);
        if(!concurrentHashMap.containsKey(cls)){
            ThreadLocal tdao = new ThreadLocal<IDao>();
            tdao.set(dao);
            concurrentHashMap.put(cls, tdao);
        }
        return dao;
    }
}
