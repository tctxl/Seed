package com.opdar.framework.db.interfaces;


import com.opdar.framework.db.convert.Convert;

import javax.sql.DataSource;

/**
 * Created by Jeffrey on 2014/9/3
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public interface IDatabase {
    public void setDataSource(DataSource dataSource);

    public <T>IDao<T> getDao(Class<T> cls);

    void addConvert(Class<?> clz, Convert convert);
}
