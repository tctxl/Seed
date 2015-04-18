package com.opdar.framework.db.convert;

/**
 * Created by Jeffrey on 2014/9/3
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class EnumConvert implements Convert<Enum>  {
    @Override
    public String convert(Object... params) {
        Enum value = (Enum) params[0];
        return String.valueOf(value.name());
    }

    @Override
    public Enum reconvert(Object...params) {
        if(params == null)return null;
        Class clz = (Class) params[0];
        String enumName = params[1].toString();
        return Enum.valueOf(clz,enumName);
    }
}
