package com.opdar.framework.configureweb.controller;

import com.opdar.framework.web.anotations.Controller;
import com.opdar.framework.web.anotations.Injection;
import com.opdar.framework.web.anotations.Router;

/**
 * Created by Jeffrey on 2015/4/19.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
@Controller(value = "/login/", prefix = "html")
public class LoginController {

    @Router
    public String index(String test) {
        return "true";
    }

}