package com.opdar.framework.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Jeffrey on 2015/4/10.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class Utils {
    public static byte[] is2byte(InputStream inputStream) throws IOException {
        ByteArrayOutputStream arrayBuffer = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int len = -1;
        while ((len = inputStream.read(b)) != -1) {
            arrayBuffer.write(b, 0, len);
        }
        inputStream.close();
        arrayBuffer.close();
        return arrayBuffer.toByteArray();
    }

    public static char byte2Char(byte[] b) {
        char c = (char) (((b[0] & 0xFF) << 8) | (b[1] & 0xFF));
        return c;
    }


    public static boolean isClassPath(String params){
        if(params != null && params.indexOf("classpath:") == 0 && params.trim().length() > 10){
            return true;
        }
        return false;
    }

    public static String getClassPath(String params){
        if(isClassPath(params)){
            return params.substring(10);
        }
        return null;
    }

    public static String parseSignFactor(String factor,List<String> parentMapping){
        StringBuilder stringBuilder = new StringBuilder();
        int index = factor.indexOf("#{");
        if(index != -1){
            stringBuilder.append(factor.substring(0,index));
            int index2 = factor.indexOf("}");
            if(index2 != -1){
                String key = factor.substring(index+2,index2);

                parentMapping.add(key);
                stringBuilder.append(" %s ");
                stringBuilder.append(factor.substring(index2+1,factor.length()));
            }else{
                stringBuilder.append(factor.substring(index));
            }
            factor = parseSignFactor(stringBuilder.toString(), parentMapping);
        }
        return factor;
    }

    public static String testField(String field) {
        if(field.indexOf("is") == 0){
            field = field.replaceFirst("is","");
        }
        if(field.length() < 3 && field.length()>1){
            return Character.toUpperCase(field.charAt(0))+field.substring(1);
        }
        char c1 = field.charAt(0);
        char c2 = field.charAt(1);
        if(Character.isUpperCase(c1)){
            return field;
        }
        if(Character.isLowerCase(c1)&& Character.isUpperCase(c2)){
            return field;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Character.toUpperCase(c1)).append(field.substring(1));
        return stringBuilder.toString();
    }

    public static String testRouter(String router) {
        int i = router.indexOf("/");
        if (i == -1) {
            return "/".concat(router);
        }

        StringBuilder _router = new StringBuilder();
        for (String r : router.split("/")) {
            if (!r.trim().equals("")) {
                _router.append(r).append("/");
            }
        }
        if (_router.length() > 0) {
            _router.deleteCharAt(_router.length() - 1);
            _router.insert(0, "/");
        }
        return _router.toString();
    }

    public static Map<String, String> spliteParams(String params){
        Map<String,String> values = new HashMap<String, String>();
        if(params == null)return values;
        String[] ps = null;
        if(params.indexOf(";") != -1){
            ps = params.split(";");
        }else{
            ps = new String[]{params};
        }
        for(String s:ps){
            if(s.indexOf(",") != -1){
                String[] ss = s.split(",");
                if(ss.length >1 && ss[0].trim().length() >0){
                    values.put(ss[0],ss[1]);
                }
            }
        }
        return values;
    }
}
