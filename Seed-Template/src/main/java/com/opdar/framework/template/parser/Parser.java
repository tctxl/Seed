package com.opdar.framework.template.parser;

import com.opdar.framework.template.utils.ValueUtil;

import java.io.StringWriter;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by 俊帆 on 2015/8/4.
 */
public class Parser {
    private String open,close;
    List<String> contentTable = new LinkedList<String>();
    List<String> params = new LinkedList<String>();
    public Parser(String open,String close){
        this.open = open;
        this.close = close;
    }

    public void get(String content){
        get(content, CharBuffer.wrap(content));
    }

    public void get(String content,CharBuffer contentBuffer){
        int start = contentBuffer.position();
        int i1 = content.indexOf(open, start);
        boolean flag = false;
        if (i1 != -1) {
            int i2 = content.indexOf(close, i1);
            if (i2 != -1) {
                flag = true;
                //匹配程序，开始解析
                //开始获取标记符前半段内容
                char[] str = new char[i1 - start];
                contentBuffer.get(str, 0, i1 - start);
                contentTable.add(String.valueOf(str));
                //开始获取变量
                int varlen = i2 - (start + str.length + open.length());
                char[] v = new char[varlen];
                contentBuffer.position(i1 + open.length());
                contentBuffer.get(v, 0, v.length);
//                System.out.println(String.valueOf(v));
                params.add(String.valueOf(v));
                contentBuffer.position(contentBuffer.position() + close.length());
                get(content, contentBuffer);
            }
        }
        if (!flag) {
            contentTable.add(contentBuffer.toString());
        }
    }

    public void parse(Object object, StringWriter sw){
        parse(object,sw,new HashMap<String, Object>());
    }

    public void parse(Object object, StringWriter sw, Map<String, Object> vars){
        for(int i=0;i<contentTable.size();i++){
            sw.write(contentTable.get(i));
            if(i!=contentTable.size()-1){
                String param = params.get(i);
                if(param.indexOf(".") > 0){
                    String[] par = param.split("\\.");
                    Object o = ValueUtil.get(vars, param);
                    if(o == null)
                    o = object;
                    for(String s:par){
                        o = ValueUtil.get(o,s);
                    }
                    if(o == null)o = "";
                    sw.write(o.toString());
                }else{
                    Object o = ValueUtil.get(vars,param);
                    if(o == null)
                    o = ValueUtil.get(object,param);
                    if(o == null)o = "";
                    sw.write(o.toString());
                }
            }
        }
    }
}
