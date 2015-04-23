package com.opdar.framework.configureweb.beans;

import com.opdar.framework.db.anotations.Table;

/**
 * Created by Jeffrey on 2015/4/23.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
@Table("test_ihygeia_login.t_users")
public class UserEntity {
    private String tid;

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }
}
