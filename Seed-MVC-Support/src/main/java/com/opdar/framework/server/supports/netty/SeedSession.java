package com.opdar.framework.server.supports.netty;

import com.opdar.framework.utils.yeson.convert.StringConvert;
import com.opdar.framework.web.common.IResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class SeedSession implements IResponse {
	private ChannelHandlerContext ctx;
	private String socketId;
    private Map<String,String> headers = new HashMap<String, String>();
    private Map<String,String> cookies = new HashMap<String, String>();

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
        for(Iterator<Map.Entry<String, String>> it = headers.entrySet().iterator();it.hasNext();){
            Map.Entry<String, String> entry = it.next();
            response.headers().set(entry.getKey(),entry.getValue());
        }
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, contentType);
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());

        for (Iterator<String> it = cookies.keySet().iterator();it.hasNext();) {
            String key = it.next();
            String value = cookies.get(key);
            Cookie cookie = new DefaultCookie(key,value);
            response.headers().add(HttpHeaders.Names.SET_COOKIE, ServerCookieEncoder.encode(cookie));
        }
        return writeAndAddListener(response, ChannelFutureListener.CLOSE);
    }

    ByteArrayOutputStream baos = null;
    private String contentType;
    private int responseCode;
    public void write(byte[] content, String contentType, int responseCode) {
        if(baos == null){
            baos = new ByteArrayOutputStream();
            this.contentType = contentType;
            this.responseCode = responseCode;
        }
        try {
            baos.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeAndFlush(byte[] content, String contentType, int responseCode) {
        writeResult(content,contentType,responseCode);
    }

    public void flush() {
        writeResult(baos.toByteArray(),contentType,responseCode);
        try {
            if(baos !=null)
                baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        baos = null;
        contentType = null;
        responseCode = 0;
    }

    @Override
    public void setHeader(String key, String value) {
        headers.put(key,value);
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    @Override
    public void addCookie(String key, String value) {
        cookies.put(key,value);
    }


}
