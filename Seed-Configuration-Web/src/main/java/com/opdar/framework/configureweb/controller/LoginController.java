package com.opdar.framework.configureweb.controller;

import com.opdar.framework.configureweb.beans.TestBean;
import com.opdar.framework.configureweb.beans.UserEntity;
import com.opdar.framework.db.impl.BaseDatabase;
import com.opdar.framework.db.interfaces.IDao;
import com.opdar.framework.web.anotations.Controller;
import com.opdar.framework.web.anotations.Injection;
import com.opdar.framework.web.anotations.RequestBody;
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
        return new HtmlView("INDEX.HTML");
    }

    @Router(value = "users")
    public List<UserEntity> findUser() {
        IDao<UserEntity> dao = Context.get(BaseDatabase.class).getDao(UserEntity.class);
        List<UserEntity> array = dao.SELECT().END().findAll();
        return array;
    }
}