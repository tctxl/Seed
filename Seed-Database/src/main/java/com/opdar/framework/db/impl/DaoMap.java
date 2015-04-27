package com.opdar.framework.db.impl;

import com.opdar.framework.db.interfaces.IDao;
import com.opdar.framework.utils.ThreadLocalUtils;

import java.util.HashMap;
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

    private static Map<String,ThreadLocal<IDao>> map = new HashMap<String, ThreadLocal<IDao>>();


    public static void clear(){
        for(Iterator<Map.Entry<String, ThreadLocal<IDao>>> it = map.entrySet().iterator();it.hasNext();){
            Map.Entry<String, ThreadLocal<IDao>> entry = it.next();
            ThreadLocal local = entry.getValue();
            ThreadLocalUtils.clearThreadLocals(DATABASE_THREAD_KEY,local);
        }
        map.clear();
    }

    public static IDao get(Class<?> cls){
        if(map.containsKey(cls.getName())){
            ThreadLocal<IDao> dao = map.get(cls.getName());
            return dao.get();
        }
        return null;
    }

    public static IDao put(Class<?> cls,IDao dao){
        ThreadLocalUtils.record(DATABASE_THREAD_KEY);
        if(!map.containsKey(cls.getName())){
            ThreadLocal tdao = new ThreadLocal<IDao>();
            tdao.set(dao);
            map.put(cls.getName(), tdao);
        }
        return dao;
    }
}
