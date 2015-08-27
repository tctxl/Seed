package com.opdar.framework.web.common;

import com.opdar.framework.utils.CloseCallback;
import com.opdar.framework.utils.ThreadLocalUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 功   能:
 * 创建者: 施俊帆
 * 日   期: 2015/3/15 2:00
 * Q  Q: 362116120
 */
public class Context {
    private static ConcurrentHashMap<String, Object> pObjects = new ConcurrentHashMap<String, Object>();
    private static Map<String, ComponentInit> components = new HashMap<String, ComponentInit>();

    static {
        ThreadLocalUtils.addCloseCallback(new CloseCallback() {
            @Override
            public void close() {
                for (Map.Entry<String, ComponentInit> entry : components.entrySet()) {
                    ComponentInit component = entry.getValue();
                    component.remove();
                }
            }
        });
    }

    public static void print() {
        System.out.println(pObjects);
        System.out.println(pObjects.hashCode());
    }

    public static void add(Class clz) {
        String typeName = clz.getName();
        Object o = null;
        try {
            o = clz.newInstance();
            add(o);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void add(Object o) {
        Class clz = o.getClass();
        String typeName = o.getClass().getName();
        pObjects.put(typeName, o);
        for (Class inter : clz.getInterfaces()) {
            typeName = inter.getName();
            pObjects.put(typeName, o);
        }
    }

    public static void add(String key, Object o) {
        pObjects.put(key, o);
    }

    public static void addComponent(Class clz) {
        addComponent(new ComponentInit(clz));
    }

    public static void addComponent(ComponentInit component) {
        for (String name : component.getComponentName()) {
            components.put(name, component);
        }
    }

    public static Object getComponent(String componentName) {
        ComponentInit init = components.get(componentName);
        if (init == null) return null;
        return init.getComponentObject();
    }

    public static boolean constains(Class<?> type) {
        String typeName = type.getName();
        return pObjects.containsKey(typeName);
    }

    public static <T> T get(Class<T> type) {
        String typeName = type.getName();
        return get(typeName);
    }

    public static <T> T get(String typeName) {
        return (T) pObjects.get(typeName);
    }

}
