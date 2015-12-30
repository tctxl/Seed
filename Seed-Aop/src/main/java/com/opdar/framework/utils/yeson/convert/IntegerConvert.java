package com.opdar.framework.utils.yeson.convert;

/**
 * Created by 俊帆 on 2015/8/14.
 */
public class IntegerConvert implements JSONConvert<Integer> {

    @Override
    public Object convert(Integer o) {
        if(o == null)return -1;
        return o;
    }

    @Override
    public Integer reconvert(Object o) {
        return Integer.parseInt(o.toString());
    }

}
