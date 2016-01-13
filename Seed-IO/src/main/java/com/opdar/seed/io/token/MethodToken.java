package com.opdar.seed.io.token;

import com.opdar.seed.io.protocol.MethodProtocol;
import com.opdar.seed.io.protocol.BaseProtocol;

/**
 * Created by 俊帆 on 2015/8/28.
 */
public class MethodToken implements Token{
    @Override
    public byte[] getToken() {
        return new byte[]{'M'};
    }

    @Override
    public BaseProtocol getProtocol() {
        return new MethodProtocol(this);
    }

}
