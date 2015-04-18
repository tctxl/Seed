package com.opdar.framework.web.converts;

import com.alibaba.fastjson.JSON;
import com.opdar.framework.web.interfaces.HttpConvert;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Created by Jeffrey on 2015/4/12.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class JSONConvert implements HttpConvert {
    public HttpConvert setContentType(String contentType) {
        return this;
    }

    public String getContentType() {
        return "application/json";
    }

    public Object readBody(byte[] buffer, Type type) {
        if(type instanceof ParameterizedType){
            ParameterizedTypeImpl clz = (ParameterizedTypeImpl) type;
            if(Collection.class.isAssignableFrom(clz.getRawType())){
                try {
                    return JSON.parseArray(new String(buffer,"utf-8"), clz.getActualTypeArguments());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return JSON.parseObject(buffer,type);
    }
}
