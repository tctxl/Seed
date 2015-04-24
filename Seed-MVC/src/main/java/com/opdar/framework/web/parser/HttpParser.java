package com.opdar.framework.web.parser;

/**
 * Created by Jeffrey on 2015/4/24.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public interface HttpParser {
    Object execute(byte[] body);
    String getContentType();
}
