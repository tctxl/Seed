package com.opdar.framework.configureweb;

import com.opdar.framework.configureweb.base.Config;
import com.opdar.framework.server.base.DefaultConfig;
import com.opdar.framework.server.supports.jetty.JettySupport;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by jeffrey on 2015/4/19.
 */
public class ServletConfig extends DefaultConfig {

    Properties properties = new Properties();

    public ServletConfig(){
        try {
            properties.load(Config.class.getResourceAsStream("netty.support.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setProperties(properties);
    }

    public void onCreate() {

    }

    public void onDestory() {

    }
    public static void main(String[] args) {
        new JettySupport(8080).config(new ServletConfig()).start();
    }
}
