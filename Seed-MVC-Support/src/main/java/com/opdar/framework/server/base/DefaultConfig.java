package com.opdar.framework.server.base;

import java.util.Properties;

/**
 * Created by 俊帆 on 2015/4/16.
 */
public abstract class DefaultConfig implements IConfig {
    private Properties properties;
    public DefaultConfig(){
    }

    public DefaultConfig(Properties properties){
        this();
        this.properties = properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }


    public String get(String key){
        if(properties != null && properties.containsKey(key))
            return properties.getProperty(key);
        return null;
    }
}
