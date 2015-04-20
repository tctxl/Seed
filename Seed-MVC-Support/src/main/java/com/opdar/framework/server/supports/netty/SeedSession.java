package com.opdar.framework.server.supports.netty;

import com.opdar.framework.web.common.IResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.util.UUID;

public class SeedSession implements IResponse {
	private ChannelHandlerContext ctx;
	private String socketId;

    public SeedSession(){}
	public SeedSession(ChannelHandlerContext ctx) {
		this.ctx = ctx;
        socketId = UUID.randomUUID().toString();
	}

    public ChannelFuture writeAndAddListener(Object msg,ChannelFutureListener listener) {
        return ctx.writeAndFlush(msg).addListener(listener);
    }


    public ChannelFuture writeResult(String content,String contentType,int responseCode){
        return writeResult(content.getBytes(), contentType, responseCode);
    }

    public ChannelFuture writeResult(final byte[] content,String contentType,int responseCode){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(responseCode), Unpooled.wrappedBuffer(content));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, contentType);
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
        return writeAndAddListener(response, ChannelFutureListener.CLOSE);
    }

    public void write(byte[] content, String contentType, int responseCode) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(responseCode), Unpooled.wrappedBuffer(content));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, contentType);
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
        ctx.write(content);
    }

    public void flush() {
        ctx.flush();
        ctx.close();
    }


}
