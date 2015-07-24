package com.opdar.cplan.plugins;

import com.opdar.cplan.base.Entry;
import com.opdar.cplan.utils.CPlanUtils;
import com.opdar.framework.server.base.IConfig;
import com.opdar.framework.server.supports.servlet.SeedServlet;
import com.opdar.framework.server.supports.servlet.ServletSupport;
import com.opdar.framework.utils.yeson.JSONObject;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContextEvent;
import java.util.Iterator;
import java.util.Properties;

/**
 * Created by 俊帆 on 2015/7/24.
 */
public class CPServletSupport extends ServletSupport {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        this.config(new CPConfig());
        super.contextInitialized(servletContextEvent);
        CPlanUtils.initModule();

        for(Iterator<String> it = CPlanUtils.packages.keySet().iterator();it.hasNext();){
            try {
                String key = it.next();
                JSONObject object = CPlanUtils.packages.get(key);
                String module = object.get("module-name").toString();
//                Class cls = Class.forName(object.get("main").toString());
//                Entry entry = (Entry) cls.newInstance();
                if(object.containsKey("controllers")){
                    String controllers = object.get("controllers").toString();
                    scanController(controllers,false,module);
                }
//                entry.entry();
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
