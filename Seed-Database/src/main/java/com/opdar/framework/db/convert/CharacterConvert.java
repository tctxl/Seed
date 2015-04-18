package com.opdar.framework.db.convert;

/**
 * Created by Jeffrey on 2014/9/3
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class CharacterConvert implements Convert<Character>  {
    @Override
    public String convert(Object... params) {
        Object value = params[0];
        return String.valueOf(value);
    }

    @Override
    public Character reconvert(Object... params) {
        if(params == null || params[0].toString().length() == 0)return null;
        return Character.valueOf(params[0].toString().charAt(0));
    }
}
