package com.opdar.framework.configureweb.controller;

import com.opdar.framework.configureweb.beans.TestBean;
import com.opdar.framework.configureweb.beans.ConfigureEntity;
import com.opdar.framework.db.impl.BaseDatabase;
import com.opdar.framework.web.anotations.After;
import com.opdar.framework.web.anotations.Before;
import com.opdar.framework.web.anotations.Controller;
import com.opdar.framework.web.anotations.Router;
import com.opdar.framework.web.common.Context;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Jeffrey on 2015/4/10.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
@Controller(value = "/test/",prefix = "html")
public class TestController {

    private static int i=0;
    private static Object object = new Object();
    @Router
    @After(SeedInterceptor.class)
    @Before(SeedInterceptor.class)
    public String router2(String testParam,TestBean bean){
        synchronized (object){
            i++;
        }
        return "this is /test/router2  testParam : "+testParam;
    }

    @Router("index/#{haha}")
    public List<ConfigureEntity> routerRender(String testParam, TestBean bean,String haha){
        BaseDatabase database = Context.get(BaseDatabase.class);
        List<ConfigureEntity> users = database.getDao(ConfigureEntity.class).select().end().findAll();
        return users;
    }
}
