package com.opdar.framework.web.views;

import com.opdar.framework.utils.yeson.YesonParser;
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
public class JSONView implements View {
    Object result;
    JSONView(Object result){
        this.result = result;
    }

    @Override
    public Map<String, String> headers() {
        return null;
    }

    public byte[] renderView() {
        YesonParser parser = new YesonParser();
        try {
            return parser.toJSONString(result).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public String contentType() {
        return "application/json";
    }

    public int getCode() {
        return HttpResponseCode.CODE_200.getCode();
    }
}
