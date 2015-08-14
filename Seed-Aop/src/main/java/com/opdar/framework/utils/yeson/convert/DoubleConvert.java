package com.opdar.framework.utils.yeson.convert;

/**
 * Created by 俊帆 on 2015/8/14.
 */
public class DoubleConvert implements JSONConvert<Double> {

    @Override
    public Object convert(Double o) {
        if(o == null)return "";
        return o;
    }

}
