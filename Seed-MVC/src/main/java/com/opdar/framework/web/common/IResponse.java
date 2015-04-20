package com.opdar.framework.web.common;

/**
 * Created by jeffrey on 2015/4/15.
 */
public interface IResponse {
    public void write(final byte[] content,String contentType,int responseCode);
    public void writeAndFlush(final byte[] content,String contentType,int responseCode);
    public void flush();
}
