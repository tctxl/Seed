package com.opdar.seed.io.base;

/**
 * Created by 俊帆 on 2015/10/22.
 */
public interface SessionStateCallback {
    void regist(IoSession session);
    void unregist(IoSession session);
}
