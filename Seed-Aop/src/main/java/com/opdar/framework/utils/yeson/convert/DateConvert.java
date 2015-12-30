package com.opdar.framework.utils.yeson.convert;

import java.util.Date;

/**
 * Created by 俊帆 on 2015/8/14.
 */
public class DateConvert implements JSONConvert<Date> {

    @Override
    public Object convert(Date o) {
        if(o == null)return "\"\"";
        return o.getTime();
    }

    @Override
    public Date reconvert(Object o) {
        return new Date(Long.parseLong(o.toString()));
    }

}
