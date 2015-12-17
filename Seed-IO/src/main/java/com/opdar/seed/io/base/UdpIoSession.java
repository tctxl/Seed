package com.opdar.seed.io.base;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Created by 俊帆 on 2015/12/14.
 */
public class UdpIoSession extends IoSession {
    boolean isP2P = true;
    SocketAddress address;
    private boolean isWrite = false;

    public UdpIoSession(ChannelHandlerContext ctx, SocketAddress address) {
        super(ctx);
        this.address = address;
    }

    public boolean isP2P() {
        return isP2P;
    }

    @Override
    public void write(byte[] bytes) {
        ctx.write(new DatagramPacket(Unpooled.wrappedBuffer(bytes), (InetSocketAddress) address));
    }

    @Override
    public void flush() {
        super.flush();
        isWrite = true;
    }

    @Override
    public void writeAndFlush(byte[] bytes) {
        ctx.writeAndFlush(new DatagramPacket(Unpooled.wrappedBuffer(bytes), (InetSocketAddress) address));
        isWrite = true;
    }

    public boolean isWrite() {
        return isWrite;
    }
}
