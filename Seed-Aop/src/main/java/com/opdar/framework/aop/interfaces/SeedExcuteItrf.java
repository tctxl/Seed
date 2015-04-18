package com.opdar.framework.aop.interfaces;

/**
 * Created by Jeffrey on 2015/4/8.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public interface SeedExcuteItrf {
    public void invokeField(String fieldName,Object value);
    public Object invokeMethod(String methodName,Object...value);
}
