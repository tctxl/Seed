package com.opdar.framework.web.views;

import com.opdar.framework.web.common.HTTP_RESPONSE;
import com.opdar.framework.web.interfaces.View;

import java.io.UnsupportedEncodingException;

/**
 * Created by Jeffrey on 2015/4/11.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class DefaultView implements View {
    private final Object result;

    public DefaultView(Object result) {
        this.result = result;
    }

    public byte[] renderView() {
        if(result instanceof String){
            try {
                return result.toString().getBytes("utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return new JSONView(result).renderView();
    }

    public String contentType() {
        return "text/html";
    }

    public int getCode() {
        return HTTP_RESPONSE.CODE_200.getCode();
    }
}
