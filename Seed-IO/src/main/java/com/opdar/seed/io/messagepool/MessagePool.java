package com.opdar.seed.io.messagepool;

/**
 * Created by 俊帆 on 2015/9/10.
 */
public interface MessagePool<T> {
    boolean set(T object);
    T get(String id);
    boolean del(String id);
}
