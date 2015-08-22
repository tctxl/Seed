package com.opdar.framework.utils.yeson.convert;

/**
 * Created by 俊帆 on 2015/8/14.
 */
public class StringConvert implements JSONConvert<String> {

    @Override
    public Object convert(String o) {
        if(o == null)return "\"\"";
        return "\"".concat(o.replaceAll("\"","\\\\\\\"")).concat("\"");
    }

    public static void main(String[] args) {
        System.out.println(new StringConvert().convert("\"a"));;
    }

}
