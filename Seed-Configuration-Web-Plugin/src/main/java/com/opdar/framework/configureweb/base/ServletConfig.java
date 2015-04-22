package com.opdar.framework.configureweb.base;

import com.opdar.framework.server.base.DefaultConfig;
import com.opdar.framework.server.supports.jetty.JettySupport;

import java.util.Properties;

/**
 * Created by jeffrey on 2015/4/19.
 */
public class ServletConfig extends DefaultConfig {

    Properties properties = new Properties();

    public ServletConfig(){
        load("seed.support.properties");
    }

    public void load(String propertiesFile){
        try {
            properties.load(Config.class.getResourceAsStream(propertiesFile));
            setProperties(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onCreate() {

    }

    public void onDestory() {

    }
    public static void main(String[] args) {
        new JettySupport(8080).config(new ServletConfig()).start();
    }
}
