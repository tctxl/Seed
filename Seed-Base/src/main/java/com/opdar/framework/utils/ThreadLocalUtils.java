package com.opdar.framework.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Jeffrey on 2015/4/25.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class ThreadLocalUtils {
    private static Map<String,HashSet<Thread>> map = new HashMap<String, HashSet<Thread>>();
    public static void record(String threadsKey){
        HashSet<Thread> threads = null;
        if(map.containsKey(threadsKey)){
             threads = map.get(threadsKey);
        }else{
            map.put(threadsKey,threads = new HashSet<Thread>());
        }
        threads.add(Thread.currentThread());
    }
    public static void clearThreadLocals(String key,ThreadLocal threadLocal){
        if(map.containsKey(key)){
            HashSet<Thread> threads = map.get(key);
            for (Thread thread : threads) {
                try {
                    java.lang.reflect.Field field = Thread.class.getDeclaredField("threadLocals");
                    field.setAccessible(true);
                    Object map = field.get(thread);
                    Method method = map.getClass().getDeclaredMethod("remove",new Class[]{ThreadLocal.class});
                    method.setAccessible(true);
                    method.invoke(map,threadLocal);
                    thread.interrupt();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
