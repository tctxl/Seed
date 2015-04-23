package com.opdar.framework.configureweb.beans;

import com.opdar.framework.db.anotations.Table;

/**
 * Created by Jeffrey on 2015/4/23.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
@Table("test.t_users")
public class UserEntity {
    private Integer tid;

    public Integer getTid() {
        return tid;
    }

    public void setTid(Integer tid) {
        this.tid = tid;
    }
}
