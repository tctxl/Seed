package com.opdar.seed.io.token;

import com.opdar.seed.io.protocol.P2pProtocol;
import com.opdar.seed.io.protocol.Protocol;

/**
 * Created by 俊帆 on 2015/12/15.
 */
public class P2pToken implements Token{
    @Override
    public Long getToken() {
        return 112l;
    }

    @Override
    public Protocol getProtocol() {
        return new P2pProtocol();
    }
}
