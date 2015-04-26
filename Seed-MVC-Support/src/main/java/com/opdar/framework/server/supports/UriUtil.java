package com.opdar.framework.server.supports;

import com.opdar.framework.web.common.SeedRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by jeffrey on 2015/4/19.
 */
public class UriUtil {

    public static void executeUri(SeedRequest request,String query){
        if(query != null){
            int i = query.indexOf("&");
            String[] p1s = null;
            if(i != -1){
                p1s = query.split("&");
            }else{
                p1s = new String[]{query};
            }
            for(String s :p1s){
                i = s.indexOf("=");
                String[] p2s = null;
                if(i!=-1){
                    p2s = s.split("=");
                    if(p2s.length >1){
                        String key = p2s[0];
                        String value = p2s[1];
                        try {
                            request.setValue(key, URLDecoder.decode(value,"utf-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
