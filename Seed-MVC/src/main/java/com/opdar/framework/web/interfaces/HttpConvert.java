package com.opdar.framework.web.interfaces;

import java.lang.reflect.Type;

/**
 * Created by Jeffrey on 2015/4/10.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public interface HttpConvert {
    public HttpConvert setContentType(String contentType);

    public String getContentType();

    public <T> T readBody(byte[] buffer, Type clz);
}
