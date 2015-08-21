package com.opdar.seed.extra.utils;

import java.util.List;
import java.util.Set;

/**
 * Created by 俊帆 on 2015/8/21.
 */
public class StringUtils {
    public static boolean isBlank(final Object obj) {
        if(obj == null){
            return true;
        }else if (obj instanceof String) {
            String str = obj.toString();
            if (str == null || str.trim().equals("")
                    || str.trim().equalsIgnoreCase("null")) {
                return true;
            } else {
                return false;
            }
        }else if(obj instanceof List){
            List list = (List) obj;
            if (list == null || list.size() == 0) {
                return true;
            } else {
                return false;
            }
        }else if(obj instanceof Set){
            Set set = (Set) obj;
            if (set == null || set.size() == 0) {
                return true;
            } else {
                return false;
            }
        }else if(obj instanceof Integer){
            Integer integer = (Integer) obj;
            if (integer == null) {
                return true;
            } else {
                return false;
            }
        }else if(obj instanceof String[]){
            String[] o = (String[]) obj;
            if (o == null || o.length == 0) {
                return true;
            } else {
                return false;
            }
        }else if(obj instanceof Double){
            Double integer = (Double) obj;
            if (integer == null) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
