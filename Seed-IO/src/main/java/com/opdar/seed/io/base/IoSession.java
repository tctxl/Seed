package com.opdar.seed.io.base;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by 俊帆 on 2015/8/28.
 */
public class IoSession {

    private Map<String,Object> attr = new HashMap<String, Object>();

    protected ChannelHandlerContext ctx;
    private String id;
    private Heartbeat heartbeat;

    public void addAttribute(String key,String name) {
        this.attr = attr;
    }

    public IoSession(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        id = UUID.randomUUID().toString();
    }

    public void setHeartbeat(Heartbeat heartbeat){
        this.heartbeat = heartbeat;
    }

    public Heartbeat getHeartbeat() {
        return heartbeat;
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
        if(heartbeat != null)heartbeat.clearHeartbeat();
    }

    public void writeAndFlush(byte[] bytes) {
        ctx.writeAndFlush(bytes).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if(channelFuture.isSuccess()){

                }
            }
        });
    }

    public void downline() {
        ctx.close();
    }
}
