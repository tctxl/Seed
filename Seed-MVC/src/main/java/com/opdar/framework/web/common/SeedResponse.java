package com.opdar.framework.web.common;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by Jeffrey on 2015/4/8.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class SeedResponse implements IResponse {
    private final IResponse response;

    private boolean isWrite = false;

    public SeedResponse(IResponse response){
        this.response = response;
    }

    public void addCookie(String key,String value){
        response.addCookie(key,value);
    }

    public boolean isWrite() {
        return isWrite;
    }

    public void setIsWrite(boolean isWrite) {
        this.isWrite = isWrite;
    }

    public void writeSuccess() {
        response.write("".getBytes(),"text/html",200);
    }

    public void write(String result) {
        response.write(result.getBytes(),"text/html",200);
    }

    public void write(String content, String contentType, int responseCode) {
        try {
            write(content.getBytes("utf-8"),contentType,responseCode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public void write(byte[] content, String contentType, int responseCode) {
        response.write(content,contentType,responseCode);
    }

    @Override
    public void writeAndFlush(byte[] content, String contentType, int responseCode) {
        response.writeAndFlush(content,contentType,responseCode);
    }

    @Override
    public void flush() {
        isWrite = true;
        response.flush();
    }

    @Override
    public void setHeader(String key, String value) {
        response.setHeader(key,value);
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        response.setHeaders(headers);
    }
}
