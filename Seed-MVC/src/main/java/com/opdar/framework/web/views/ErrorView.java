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
public class ErrorView implements View {
    HTTP_RESPONSE responseCode = HTTP_RESPONSE.CODE_100;
    public ErrorView(HTTP_RESPONSE responseCode){
        this.responseCode = responseCode;
    }
    public byte[] renderView() {
        try {
            return responseCode.getContent().getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String contentType() {
        return "text/html";
    }

    public int getCode() {
        return responseCode.getCode();
    }
}
