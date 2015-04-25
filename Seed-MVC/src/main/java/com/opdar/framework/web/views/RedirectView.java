package com.opdar.framework.web.views;

import com.opdar.framework.web.interfaces.View;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jeffrey on 2015/4/25.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class RedirectView implements View {

    private final String rediretUrl;
    private Map<String,String> headers = new HashMap<String, String>();

    public RedirectView(String rediretUrl){
        this.rediretUrl = rediretUrl;
        headers.put("location",rediretUrl);
    }

    @Override
    public Map<String, String> headers() {
        return headers;
    }

    @java.lang.Override
    public byte[] renderView() {
        return new byte[0];
    }

    @java.lang.Override
    public String contentType() {
        return "text/html";
    }

    @java.lang.Override
    public int getCode() {
        return 0;
    }
}
