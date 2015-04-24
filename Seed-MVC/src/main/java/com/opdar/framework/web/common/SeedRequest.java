package com.opdar.framework.web.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jeffrey on 2015/4/8.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class SeedRequest {
    private Map<String, Object> values = new HashMap<String, Object>();
    private Map<String, String> headers = new HashMap<String, String>();
    private byte[] body;

    private String contentType;
    private String method;

    public Map<String, Object> getValues() {
        return values;
    }

    public void setValues(Map<String, Object> values) {
        this.values = values;
    }
    public void putValues(Map<String, Object> values) {
        this.values.putAll(values);
    }
    public void setValue(String key,String value) {
        this.values.put(key,value);
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public String getContentType() {
        return contentType;
    }

    public void setHeader(String key,String value) {
        if(key.toUpperCase().equals("CONTENT-TYPE")){
            contentType = value;
        }
        this.headers.put(key,value);
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}
