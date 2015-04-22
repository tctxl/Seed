package com.opdar.framework.server.supports.servlet;

import com.opdar.framework.aop.SeedInvoke;
import com.opdar.framework.server.base.IConfig;
import com.opdar.framework.server.base.ISupport;
import com.opdar.framework.server.exceptions.NoConfigException;
import com.opdar.framework.server.supports.DefaultSupport;
import com.opdar.framework.web.converts.JSONConvert;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

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
        }
        SeedServlet.web.setHttpConvert(JSONConvert.class);

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        SeedServlet.web.destory();
    }
}
