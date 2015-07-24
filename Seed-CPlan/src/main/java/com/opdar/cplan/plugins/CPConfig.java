package com.opdar.cplan.plugins;

import com.opdar.framework.server.base.DefaultConfig;
import com.opdar.framework.server.base.IConfig;

import java.util.Properties;

/**
 * Created by 俊帆 on 2015/7/24.
 */
public class CPConfig extends DefaultConfig{

    Properties properties = new Properties();

    public CPConfig() {
        load("/cplan.properties");
    }

    public void load(String propertiesFile) {
        try {
            properties.load(CPConfig.class.getResourceAsStream(propertiesFile));
            setProperties(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onCreate() {

    }

    public void onDestory() {

    }

}
