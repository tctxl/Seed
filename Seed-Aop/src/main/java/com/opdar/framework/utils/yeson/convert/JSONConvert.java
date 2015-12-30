package com.opdar.framework.utils.yeson.convert;

/**
 * Created by 俊帆 on 2015/8/14.
 */
public interface JSONConvert<T> {
    Object convert(T o);
    T reconvert(Object o);
}
