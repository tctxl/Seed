package com.opdar.seed.io.base;

/**
 * Created by 俊帆 on 2015/12/17.
 */
public interface Dispatcher<T,R> {
    R doIt(T o);
    String getType();
}