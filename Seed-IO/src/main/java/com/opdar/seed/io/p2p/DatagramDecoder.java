package com.opdar.seed.io.p2p;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

/**
 * Created by 俊帆 on 2015/9/10.
 */
@ChannelHandler.Sharable
public class DatagramDecoder extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        ByteBuf byteBuf = msg.copy().content();
        ctx.fireChannelRead(byteBuf);
    }
}
