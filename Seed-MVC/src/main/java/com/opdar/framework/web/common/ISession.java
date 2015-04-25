package com.opdar.framework.web.common;

/**
 * Created by Jeffrey on 2015/4/25.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public interface ISession {
    void setValue(String key,Object value);
    Object getValue(String key);
    Object getValue(String key, String defaultValue);
}
