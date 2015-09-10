package com.opdar.seed.io.token;

import com.opdar.seed.io.protocol.ClusterProtocol;
import com.opdar.seed.io.protocol.Protocol;

/**
 * Created by 俊帆 on 2015/8/31.
 */
public class ClusterToken implements Token {

    @Override
    public Long getToken() {
        return 99l;
    }

    @Override
    public Protocol getProtocol() {
        return new ClusterProtocol();
    }
}
