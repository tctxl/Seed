package com.opdar.framework.web.interfaces;

/**
 * Created by Jeffrey on 2015/4/11.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public interface View {
    public byte[] renderView();
    public String contentType();
    public int getCode();
}
