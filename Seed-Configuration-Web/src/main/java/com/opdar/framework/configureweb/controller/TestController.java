package com.opdar.framework.configureweb.controller;

import com.opdar.framework.configureweb.beans.TestBean;
import com.opdar.framework.web.anotations.Controller;
import com.opdar.framework.web.anotations.Router;
import com.opdar.framework.web.views.HtmlView;

/**
 * Created by Jeffrey on 2015/4/10.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
@Controller("/test/")
public class TestController {

    private static int i=0;

    @Router
    public String router2(String testParam,TestBean bean){
        i++;
        return "this is /test/router2";
    }

    @Router("hehehe")
    public String routerRender(String testParam, TestBean bean){
        TestBean bean1 = new TestBean();
        return "hehe";
    }
}
