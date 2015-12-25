package com.opdar.framework.db.parser;

/**
 * Created by 俊帆 on 2015/12/25.
 */
public interface Callback<T> {
    void call(T...vars);
}
