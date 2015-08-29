package com.opdar.seed.io.token;

import com.opdar.seed.io.protocol.Protocol;
import com.opdar.seed.io.protocol.ActionProtocol;

/**
 * Created by 俊帆 on 2015/8/28.
 */
public class ActionToken implements Token{

    /**
     * Token is '-'
     * @return
     */
    @Override
    public Long getToken() {
        return 45l;
    }

    @Override
    public Protocol getProtocol() {
        return new ActionProtocol();
    }
}
