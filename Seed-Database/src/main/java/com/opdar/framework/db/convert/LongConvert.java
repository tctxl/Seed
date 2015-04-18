package com.opdar.framework.db.convert;

/**
 * Created by Jeffrey on 2014/9/3
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class LongConvert implements Convert<Long>  {
    @Override
    public String convert(Object... params) {
        Object value = params[0];
        return String.valueOf(value);
    }

    @Override
    public Long reconvert(Object...params) {
        if(params == null)return null;
        return Long.valueOf(params[0].toString());
    }
}
