package com.opdar.framework.utils.yeson.convert;

/**
 * Created by 俊帆 on 2015/8/14.
 */
public class FloatConvert implements JSONConvert<Float> {

    @Override
    public Object convert(Float o) {
        if(o == null)return "";
        return o;
    }

}
