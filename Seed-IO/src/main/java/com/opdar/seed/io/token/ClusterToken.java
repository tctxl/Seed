package com.opdar.seed.io.token;

import com.opdar.seed.io.protocol.ClusterProtocol;
import com.opdar.seed.io.protocol.BaseProtocol;

/**
 * Created by 俊帆 on 2015/8/31.
 */
public class ClusterToken implements Token {

    @Override
    public byte[] getToken() {
        return new byte[]{'c'};
    }

    @Override
    public BaseProtocol getProtocol() {
        return new ClusterProtocol(this);
    }
}
