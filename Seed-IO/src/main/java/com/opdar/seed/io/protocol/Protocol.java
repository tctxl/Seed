package com.opdar.seed.io.protocol;

/**
 * Created by 俊帆 on 2015/8/28.
 */
public interface Protocol {
    public <A> A execute(byte[] buf);
}
