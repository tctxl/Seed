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

import javax.servlet.ServletContextEvent;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;

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

                web.setClassLoader(loader);
                Class clz = loader.defineCls(className, context);
                if(clz != null){
                    Method method = clz.getMethod("setCloseCallback");
                    method.invoke(null);
                }

                IConfig config = entry.getConfig();
                if(config !=null){
                    loadConfig(config,web,false, module);
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
