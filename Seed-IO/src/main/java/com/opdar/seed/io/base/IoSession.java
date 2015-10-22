package com.opdar.seed.io.base;

import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

/**
 * Created by 俊帆 on 2015/8/28.
 */
public class IoSession {

    private ChannelHandlerContext ctx;
    private String id;
    public IoSession(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        id = UUID.randomUUID().toString();
    }

    public ChannelHandlerContext getContext() {
        return ctx;
    }

    public String getId() {
        return id;
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

    public void downline() {
        ctx.close();
    }
}
