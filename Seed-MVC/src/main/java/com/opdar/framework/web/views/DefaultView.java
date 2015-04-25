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
public class DefaultView implements View {
    private final Object result;

    public DefaultView(Object result) {
        this.result = result;
    }

    @Override
    public Map<String, String> headers() {
        return null;
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
        return HttpResponseCode.CODE_200.getCode();
    }
}
