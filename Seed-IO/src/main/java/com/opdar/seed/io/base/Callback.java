package com.opdar.seed.io.base;

/**
 * Created by 俊帆 on 2015/12/15.
 */
public interface Callback<T,V> {
    V call(T object);
}
