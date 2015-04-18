package com.opdar.framework.web.converts;

import com.opdar.framework.web.interfaces.HttpConvert;

import java.lang.reflect.Type;

/**
 * Created by Jeffrey on 2015/4/10.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class DefaultConvert implements HttpConvert{

    public HttpConvert setContentType(String contentType) {
        return this;
    }

    public String getContentType() {
        return "*";
    }

    public <T> T readBody(byte[] buffer, Type clz) {
        return null;
    }

}
