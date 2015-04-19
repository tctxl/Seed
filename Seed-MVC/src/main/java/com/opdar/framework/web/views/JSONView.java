package com.opdar.framework.web.views;

import com.alibaba.fastjson.JSON;
import com.opdar.framework.web.common.HttpResponseCode;
import com.opdar.framework.web.interfaces.View;

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
    public byte[] renderView() {
        return JSON.toJSONBytes(result);
    }

    public String contentType() {
        return "application/json";
    }

    public int getCode() {
        return HttpResponseCode.CODE_200.getCode();
    }
}
