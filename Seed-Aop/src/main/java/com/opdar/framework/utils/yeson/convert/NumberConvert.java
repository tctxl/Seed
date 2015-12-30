package com.opdar.framework.utils.yeson.convert;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by 俊帆 on 2015/8/14.
 */
public class NumberConvert implements JSONConvert<Number> {

    @Override
    public Object convert(Number o) {
        if(o == null)return -1;
        return o;
    }

    @Override
    public Number reconvert(Object o) {
        try {
            return NumberFormat.getInstance().parse(o.toString());
        } catch (ParseException ignored) {
        }
        return null;
    }

}
