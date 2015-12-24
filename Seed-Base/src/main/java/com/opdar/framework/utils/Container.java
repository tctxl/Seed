package com.opdar.framework.utils;

import java.util.LinkedHashMap;

/**
 * Created by 俊帆 on 2015/12/19.
 */
public class Container {
    private LinkedHashMap<Class<? extends Plugin>,Plugin> plugins = new LinkedHashMap<Class<? extends Plugin>,Plugin>();

    public void load(Plugin plugin) {
        plugins.put(plugin.getClass(), plugin);
        try {
            plugin.install();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends Plugin>Plugin get(Class<T> clz){
        return plugins.get(clz);
    }
}
