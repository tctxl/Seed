package com.opdar.framework.utils.yeson.convert;

/**
 * Created by 俊帆 on 2015/8/14.
 */
public class NumberConvert implements JSONConvert<Number> {

    @Override
    public Object convert(Number o) {
        if(o == null)return -1;
        return o;
    }

}
