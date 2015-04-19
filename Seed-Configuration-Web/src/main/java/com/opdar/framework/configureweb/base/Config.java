package com.opdar.framework.configureweb.base;

import com.opdar.framework.server.base.DefaultConfig;
import com.opdar.framework.server.supports.jetty.JettySupport;
import com.opdar.framework.server.supports.netty.NettySupport;
import com.opdar.framework.web.common.Constant;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by 俊帆 on 2015/4/16.
 */
public class Config extends DefaultConfig {
    Properties properties = new Properties();
    public Config() {
        try {
            properties.load(Config.class.getResourceAsStream("netty.support.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setProperties(properties);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestory() {

    }

    public static void main(String[] args) {
        new NettySupport(8080).config(new Config()).start();
    }
}
