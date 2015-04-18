package com.opdar.framework.db.convert;

/**
 * Created by Jeffrey on 2014/9/3
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class IntegerConvert implements Convert<Integer>  {
    @Override
    public String convert(Object... params) {
        Object value = params[0];
        return value==null?null:String.valueOf(value);
    }

    @Override
    public Integer reconvert(Object...params) {
        if(params == null || params[0] == null)return null;
        return Integer.valueOf(params[0].toString());
    }
}
