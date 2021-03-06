package com.opdar.framework.utils.yeson.convert;

/**
 * Created by 俊帆 on 2015/8/14.
 */
public class LongConvert implements JSONConvert<Long> {

    @Override
    public Object convert(Long o) {
        if(o == null)return -1;
        return o;
    }

    @Override
    public Long reconvert(Object o) {
        return Long.valueOf(o.toString());
    }

}
