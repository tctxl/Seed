package com.opdar.framework.configureweb.tomcat;

import com.opdar.framework.server.base.DefaultConfig;
import com.opdar.framework.utils.Utils;

import java.util.Properties;

/**
 * Created by Jeffrey on 2015/4/22.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class TomcatConfig extends DefaultConfig {

    Properties properties = new Properties();

    public TomcatConfig(){
        load("seed.support.properties");
    }

    public void load(String propertiesFile){
        try {
            properties.load(TomcatConfig.class.getResourceAsStream(Utils.testRouter(propertiesFile)));
            setProperties(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestory() {

    }
}
