package com.opdar.framework.web.views;

import com.opdar.framework.web.common.HttpResponseCode;
import com.opdar.framework.web.interfaces.View;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by Jeffrey on 2015/4/11.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class ErrorView implements View {
    HttpResponseCode responseCode = HttpResponseCode.CODE_100;
    public ErrorView(HttpResponseCode responseCode){
        this.responseCode = responseCode;
    }

    @Override
    public Map<String, String> headers() {
        return null;
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
