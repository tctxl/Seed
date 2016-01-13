package com.opdar.seed.io.token;

import com.opdar.seed.io.protocol.Protocol;

/**
 * Created by 俊帆 on 2015/8/28.
 */
public interface Token {
    byte[] getToken();

    Protocol getProtocol();
}
