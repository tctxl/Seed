package com.opdar.cplan.plugins;

import com.opdar.cplan.base.Entry;
import com.opdar.cplan.utils.CPLoader;
import com.opdar.cplan.utils.CPlanUtils;
import com.opdar.framework.server.base.IConfig;
import com.opdar.framework.server.supports.servlet.SeedServlet;
import com.opdar.framework.server.supports.servlet.ServletSupport;
import com.opdar.framework.utils.yeson.JSONObject;
import com.opdar.framework.web.SeedWeb;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContextEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

/**
 * Created by 俊帆 on 2015/7/24.
 */
public class CPServletSupport extends ServletSupport {

    public HashMap<String,SeedWeb> webs = new HashMap<String, SeedWeb>();

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        this.config(new CPConfig());
        super.contextInitialized(servletContextEvent);
        CPlanUtils.initModule();

        for(Iterator<String> it = CPLoader.packages.keySet().iterator();it.hasNext();){
            try {
                String key = it.next();
                JSONObject object = CPLoader.packages.get(key);
                String module = object.get("module-name").toString();
                CPLoader loader = CPLoader.getLoader(module);
                Class cls = loader.findClass(object.get("main").toString());
                System.out.println(cls);
                Entry entry = (Entry) cls.newInstance();
                if(object.containsKey("controllers")){
                    String controllers = object.get("controllers").toString();
                    SeedWeb web = SeedServlet.getWeb();
                    web.setClassLoader(loader);
                    web.scanController(controllers, false, module);
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
