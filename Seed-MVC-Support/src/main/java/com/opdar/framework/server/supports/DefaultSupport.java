package com.opdar.framework.server.supports;

import com.opdar.framework.server.base.IConfig;
import com.opdar.framework.server.base.ISupport;
import com.opdar.framework.server.supports.servlet.SeedServlet;
import com.opdar.framework.utils.CloseCallback;
import com.opdar.framework.web.SeedWeb;
import com.opdar.framework.web.converts.JSONConvert;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by 俊帆 on 2015/4/16.
 */
public abstract class DefaultSupport implements ISupport {
    public static IConfig config = null;
    public ISupport config(IConfig config) {
        this.config = config;
        return this;
    }

    protected void loadConfig(IConfig config,SeedWeb web) {
        web.setHttpConvert(JSONConvert.class);
        web.loadComponent(config.get(IConfig.CONTROLLER_PATH));
        web.setWebHtml(config.get(IConfig.PAGES));
        web.setWebPublic(config.get(IConfig.PUBLIC));
        web.setDefaultPages(config.get(IConfig.DEFAULT_PAGES));
        String activeRecord = config.get(IConfig.ACTIVE_RECORD);
        String jdbcUrl = config.get(IConfig.JDBC_URL);
        String userName = config.get(IConfig.JDBC_USERNAME);
        String passWord = config.get(IConfig.JDBC_PASSWORD);
        String driver = config.get(IConfig.JDBC_DRIVER);
        String database = config.get(IConfig.JDBC_DATABASE);
        String datasource = config.get(IConfig.JDBC_DATASOURCE);
        String host = config.get(IConfig.JDBC_HOST);
        String openurl = config.get(IConfig.JDBC_OPENURL);
        if (activeRecord == null) activeRecord = "0";
        if (openurl == null) openurl = "0";
        web.setDatabase(activeRecord, driver, jdbcUrl, userName, passWord, database, datasource, host, openurl);
        config.onCreate();
    }

}
