package com.opdar.framework.configureweb.controller;

import com.opdar.framework.configureweb.beans.TestBean;
import com.opdar.framework.configureweb.beans.ConfigureEntity;
import com.opdar.framework.db.impl.BaseDatabase;
import com.opdar.framework.db.interfaces.IDao;
import com.opdar.framework.web.anotations.Controller;
import com.opdar.framework.web.anotations.Router;
import com.opdar.framework.web.common.Context;
import com.opdar.framework.web.views.HtmlView;

import java.util.List;

/**
 * Created by Jeffrey on 2015/4/19.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
@Controller(value = "/login/", prefix = "html")
public class LoginController {

    @Router
    public HtmlView index(String test1,TestBean test) {
        return new HtmlView("index.html");
    }

    @Router(value = "users")
    public List<ConfigureEntity> findUser() {
        IDao<ConfigureEntity> dao = Context.get(BaseDatabase.class).getDao(ConfigureEntity.class);
        List<ConfigureEntity> array = dao.select().end().findAll();
        return array;
    }
}