package com.opdar.framework.web.parser;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by Jeffrey on 2015/4/24.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class FormParser implements HttpParser {

    @Override
    public Object execute(byte[] body) {
        Map<String,Object> map = new HashMap<String, Object>();
        try {
            String params = new String(body,"utf-8");
            String[] pairs = params.indexOf("&") != -1?params.split("&"):new String[]{params};
            for(String p:pairs){
                if(p.indexOf("=") != -1){
                    String[] param = p.split("=");
                    if(param.length >1){
                        String p1 = param[0].trim();
                        String p2 = param[1].trim();
                        if(p1.length()>0&&p2.length()>0){
                            if(map.containsKey(p1)){
                                Object o = map.get(p1);
                                if(o instanceof Collection){
                                    ((Collection) o).add(URLDecoder.decode(p2, "utf-8"));
                                }else{
                                    LinkedList<? super Object> list= new LinkedList<Object>();
                                    map.put(p1,list);
                                    list.add(o);
                                    list.add(URLDecoder.decode(p2, "utf-8"));
                                }
                            }else{
                                map.put(p1,URLDecoder.decode(p2, "utf-8") );
                            }
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public String getContentType() {
        return "application/x-www-form-urlencoded";
    }

}
