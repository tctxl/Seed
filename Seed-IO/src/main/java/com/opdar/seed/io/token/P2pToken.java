package com.opdar.seed.io.token;

import com.opdar.seed.io.protocol.P2pProtocol;
import com.opdar.seed.io.protocol.BaseProtocol;

/**
 * Created by 俊帆 on 2015/12/15.
 */
public class P2pToken implements Token{
    @Override
    public byte[] getToken() {
        return new byte[]{'p'};
    }

    @Override
    public BaseProtocol getProtocol() {
        return new P2pProtocol(this);
    }
}
