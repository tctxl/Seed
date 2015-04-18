package com.opdar.framework.db.convert;

/**
 * Created by Jeffrey on 2014/9/3
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public interface Convert<T> {
    public String convert(Object...params);
    public T reconvert(Object...params);
}
