package com.opdar.seed.io.protocol;

/**
 * Created by 俊帆 on 2016/1/13.
 */
public interface Protocol<A> {

    public abstract A execute(byte[] buf);
}
