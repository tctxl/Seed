package com.opdar.seed.io.base;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

import static io.netty.buffer.Unpooled.wrappedBuffer;

/**
 * Created by 俊帆 on 2015/8/27.
 */
@ChannelHandler.Sharable
public class Encoder extends MessageToMessageEncoder<Object> {


    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        if(msg instanceof DatagramPacket){
            ReferenceCountUtil.retain(msg);
            out.add(msg);
        }else{
            out.add(wrappedBuffer((byte[]) msg));
        }
    }
}
