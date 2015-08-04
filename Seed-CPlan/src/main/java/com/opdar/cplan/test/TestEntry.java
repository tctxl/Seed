package com.opdar.cplan.test;

import com.opdar.cplan.base.Entry;
import com.opdar.cplan.utils.Utils;
import com.opdar.framework.server.base.IConfig;
import com.opdar.framework.utils.yeson.JSONObject;
import com.opdar.framework.utils.yeson.YesonParser;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.StringTemplateResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 俊帆 on 2015/7/23.
 */
public class TestEntry implements Entry {

    private static GroupTemplate gt;

    @Override
    public void entry() {
        if(gt == null){
            try {
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("package.json");
                if(is != null){
                    String packageJSON = new String(Utils.read(is),"utf-8");

                    YesonParser parser = new YesonParser();
                    JSONObject object = parser.parse(packageJSON);
                    object.containsKey("main");

                    StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
                    Configuration cfg = null;
                    try {
                        cfg = Configuration.defaultConfiguration();
                        gt = new GroupTemplate(resourceLoader, cfg);
                        Map<String, Object> vars = new HashMap<String, Object>();
//                        vars.put("projectName",Configure.PROJECT_NAME.getValue());
//                        vars.put("baseurl",Configure.BASE_URL.getValue());
                        gt.setSharedVars(vars);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public IConfig getConfig() {
        return null;
    }

}
