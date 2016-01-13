package com.opdar.seed.io.token;

import com.opdar.seed.io.protocol.BaseProtocol;
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
    public byte[] getToken() {
        return new byte[]{'-'};
    }

    @Override
    public BaseProtocol getProtocol() {
        return new ActionProtocol(this);
    }
}
