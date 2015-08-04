package com.opdar.cplan.plugins;

import com.opdar.cplan.base.Entry;
import com.opdar.cplan.utils.CPLoader;
import com.opdar.cplan.utils.CPlanUtils;
import com.opdar.cplan.utils.Utils;
import com.opdar.framework.server.base.IConfig;
import com.opdar.framework.server.supports.servlet.SeedServlet;
import com.opdar.framework.server.supports.servlet.ServletSupport;
import com.opdar.framework.utils.yeson.JSONObject;
import com.opdar.framework.web.SeedWeb;
import com.opdar.framework.web.common.Context;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContextEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

/**
 * Created by 俊帆 on 2015/7/24.
 */
public class CPServletSupport extends ServletSupport {

    public HashMap<String, SeedWeb> webs = new HashMap<String, SeedWeb>();

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        this.config(new CPConfig());
        super.contextInitialized(servletContextEvent);
        CPlanUtils.initModule();
        String className = Context.class.getName();
        byte[] context = new byte[0];
        try {
            context = Utils.read(Thread.currentThread().getContextClassLoader().getResourceAsStream(className.replace(".", "/").concat(".class")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Iterator<String> it = CPLoader.packages.keySet().iterator(); it.hasNext(); ) {
            try {
                String key = it.next();
                JSONObject object = CPLoader.packages.get(key);
                String module = object.get("module-name").toString();
                CPLoader loader = CPLoader.getLoader(module);
                Class cls = loader.findClass(object.get("main").toString());
                Entry entry = (Entry) cls.newInstance();
                SeedWeb web = SeedServlet.getWeb();

                if (object.containsKey("controllers")) {
                    String controllers = object.get("controllers").toString();
                    web.setClassLoader(loader);
                    loader.defineCls(className, context);
                    web.scanController(controllers, false, module);
                }
                IConfig config = entry.getConfig();
                if(config !=null){
                    String activeRecord = config.get(IConfig.ACTIVE_RECORD);
                    String jdbcUrl = config.get(IConfig.JDBC_URL);
                    String userName = config.get(IConfig.JDBC_USERNAME);
                    String passWord = config.get(IConfig.JDBC_PASSWORD);
                    String driver = config.get(IConfig.JDBC_DRIVER);
                    String database = config.get(IConfig.JDBC_DATABASE);
                    String datasource = config.get(IConfig.JDBC_DATASOURCE);
                    String host = config.get(IConfig.JDBC_HOST);
                    String openurl = config.get(IConfig.JDBC_OPENURL);
                    if(activeRecord == null)activeRecord = "0";
                    if(openurl == null)openurl = "0";
                    web.setDatabase(activeRecord, driver, jdbcUrl, userName, passWord,database,datasource,host,openurl);
                }
                entry.entry();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        super.contextDestroyed(servletContextEvent);
    }

}
