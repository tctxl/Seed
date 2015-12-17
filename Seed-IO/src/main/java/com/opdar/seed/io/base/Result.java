package com.opdar.seed.io.base;

import java.net.SocketAddress;

/**
 * Created by 俊帆 on 2015/12/15.
 */
public class Result {
    Object result;
    SocketAddress address;

    public Result(Object result, SocketAddress address) {
        this.result = result;
        this.address = address;
    }

    public Object get() {
        return result;
    }

    public SocketAddress getAddress() {
        return address;
    }

}
