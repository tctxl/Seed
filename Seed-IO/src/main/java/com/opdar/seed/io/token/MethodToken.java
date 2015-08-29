package com.opdar.seed.io.token;

import com.opdar.seed.io.protocol.MethodProtocol;
import com.opdar.seed.io.protocol.Protocol;

/**
 * Created by 俊帆 on 2015/8/28.
 */
public class MethodToken implements Token{
    @Override
    public Long getToken() {
        return 77l;
    }

    @Override
    public Protocol getProtocol() {
        return new MethodProtocol();
    }

}
