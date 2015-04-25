package com.opdar.framework.web.common;

import java.util.Map;

/**
 * Created by jeffrey on 2015/4/15.
 */
public interface IResponse {
    void write(final byte[] content,String contentType,int responseCode);
    void writeAndFlush(final byte[] content,String contentType,int responseCode);
    void flush();

    void setHeader(String key, String value);
    void setHeaders(Map<String,String> headers);
}
