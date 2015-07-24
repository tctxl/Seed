package com.opdar.framework.server.supports.servlet;

import com.opdar.framework.server.base.IConfig;
import com.opdar.framework.server.base.ISupport;
import com.opdar.framework.server.exceptions.NoConfigException;
import com.opdar.framework.server.supports.DefaultSupport;
import com.opdar.framework.web.converts.JSONConvert;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;

/**
 * Created by shiju_000 on 2015/4/8.
 */
public class ServletSupport extends DefaultSupport implements ServletContextListener{
    public ServletSupport(){
    }

    public ISupport start() {
        return this;
    }


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        if(ServletSupport.config == null){
            System.setProperty("seed.root",new File(servletContextEvent.getServletContext().getRealPath("/")).getAbsolutePath());
            String config = servletContextEvent.getServletContext().getInitParameter("config");
            try {
                IConfig tempConfig = (IConfig) Class.forName(config).newInstance();
                ServletSupport.config = tempConfig;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        if(ServletSupport.config == null){
            try {
                throw new NoConfigException();
            } catch (NoConfigException e) {
                e.printStackTrace();
            }
        }else{
            SeedServlet.web.scanController(ServletSupport.config.get(IConfig.CONTROLLER_PATH));
            SeedServlet.web.setWebHtml(ServletSupport.config.get(IConfig.PAGES));
            SeedServlet.web.setWebPublic(ServletSupport.config.get(IConfig.PUBLIC));
            SeedServlet.web.setDefaultPages(ServletSupport.config.get(IConfig.DEFAULT_PAGES));
            String activeRecord = ServletSupport.config.get(IConfig.ACTIVE_RECORD);
            String jdbcUrl = ServletSupport.config.get(IConfig.JDBC_URL);
            String userName = ServletSupport.config.get(IConfig.JDBC_USERNAME);
            String passWord = ServletSupport.config.get(IConfig.JDBC_PASSWORD);
            String driver = ServletSupport.config.get(IConfig.JDBC_DRIVER);
            String database = ServletSupport.config.get(IConfig.JDBC_DATABASE);
            String datasource = ServletSupport.config.get(IConfig.JDBC_DATASOURCE);
            String host = ServletSupport.config.get(IConfig.JDBC_HOST);
            String openurl = ServletSupport.config.get(IConfig.JDBC_OPENURL);
            if(activeRecord == null)activeRecord = "0";
            if(openurl == null)openurl = "0";
            SeedServlet.web.setDatabase(activeRecord, driver, jdbcUrl, userName, passWord,database,datasource,host,openurl);
        }
        SeedServlet.web.setHttpConvert(JSONConvert.class);
        ServletSupport.config.onCreate();
    }

    @Override
    public void scanController(String path,boolean isClear,String perfix){
        SeedServlet.web.scanController(path,isClear,perfix);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if(ServletSupport.config !=null)
            ServletSupport.config.onDestory();
        SeedServlet.web.destory();
    }
}
