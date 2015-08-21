package com.opdar.framework.template.parser;

import com.opdar.framework.template.res.Loader;

import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by 俊帆 on 2015/8/3.
 */
public class Resolver {

    BaseTemplate template;
    private ClassPathResourceLoader loader;
    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("test", "aaa");
        map.put("test111", "2222");
        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("test", "aaa");
        map2.put("test111", "2222");
        Map<String, Object> map3 = new HashMap<String, Object>();
        map3.put("test", "2222111111111");
        map3.put("test111", "1aa");
        List<Map<String, Object>> asi = new LinkedList<Map<String, Object>>();
        map.put("asi", asi);
        asi.add(map2);
        asi.add(map3);
        asi.add(map2);
        ClassPathResourceLoader loader = new ClassPathResourceLoader();
        long time = System.currentTimeMillis();
        System.out.println(loader.parse("template/index.html",map));
        System.out.println(System.currentTimeMillis() - time);
    }

    public Resolver(String content, Loader loader) {
        CharBuffer contentBuffer = CharBuffer.wrap(content);
        template = new BaseTemplate(content, contentBuffer ,loader);
    }

    public void setPath(String path){
        template.setCurrentPath(path);
    }

    public String parse(Object object){
        return template.parse(object);
    }

}
