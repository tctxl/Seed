package com.opdar.seed.io.base;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.ReferenceCountUtil;

import java.util.Arrays;
import java.util.List;

import static io.netty.buffer.Unpooled.wrappedBuffer;

/**
 * Created by 俊帆 on 2015/12/30.
 */
public class FlashDecoder extends MessageToMessageDecoder<ByteBuf> {

    private byte[] xmlsocket = new byte[]{60, 112, 111, 108, 105, 99, 121, 45, 102, 105, 108, 101, 45, 114, 101, 113, 117, 101, 115, 116, 47, 62, 0};
    private String xml = "<?xml version=\"1.0\"?><cross-domain-policy><site-control permitted-cross-domain-policies=\"all\"/><allow-access-from domain=\"*\" to-ports=\"*\"/></cross-domain-policy>\0";

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        if (Arrays.equals(bytes, xmlsocket)) {
            channelHandlerContext.writeAndFlush(wrappedBuffer(xml.getBytes()));
        }
    }
}
