package com.opdar.framework.utils.yeson.convert;

/**
 * Created by 俊帆 on 2015/8/14.
 */
public class ByteConvert implements JSONConvert<Byte> {

    @Override
    public Object convert(Byte o) {
        if(o == null)return -1;
        return o;
    }

}
