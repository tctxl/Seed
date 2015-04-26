package com.opdar.framework.db.impl;

import java.util.LinkedList;

/**
 * Created by Jeffrey on 2015/4/26.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class ChildSql {
    String sql;
    LinkedList<String> parentMapping = new LinkedList<String>();
    Class<?> type;
}
