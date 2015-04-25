package com.opdar.framework.db.impl;

import javax.sql.DataSource;

/**
 * Created by Jeffrey on 2015/4/25.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public interface OnDataSourceCloseListener {
    void close(DataSource dataSource);
}
