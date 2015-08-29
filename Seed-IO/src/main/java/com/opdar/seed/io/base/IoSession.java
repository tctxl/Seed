package com.opdar.seed.io.base;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by 俊帆 on 2015/8/28.
 */
public class IoSession {

    private ChannelHandlerContext ctx;

    public IoSession(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void write(byte[] bytes) {
        ctx.write(bytes);
    }

    public void flush() {
        ctx.flush();
    }

    public void writeAndFlush(byte[] bytes) {
        ctx.writeAndFlush(bytes);
    }
}
