package com.opdar.framework.utils.yeson.convert;

/**
 * Created by 俊帆 on 2015/8/14.
 */
public class CharConvert implements JSONConvert<Character> {

    @Override
    public Object convert(Character o) {
        if(o == null)return "";
        return o;
    }

}
