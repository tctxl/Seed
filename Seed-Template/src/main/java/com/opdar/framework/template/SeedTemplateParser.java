package com.opdar.framework.template;

import com.opdar.framework.utils.Utils;

import java.io.*;
import java.nio.CharBuffer;
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
public class SeedTemplateParser {
    private static final char LEFT_BUCKET = '{';
    private static final char RIGHT_BUCKET = '}';
    private static final char NUMBER_SIGN = '#';
    public SeedTemplateParser(File file,Map<String,Object> dataModels) throws IOException {
        this(new FileInputStream(file),dataModels);
    }

    public SeedTemplateParser(InputStream file,Map<String,Object> dataModels) throws IOException {
        this(Utils.is2byte(file),dataModels);
    }

    public SeedTemplateParser(byte[] content,Map<String,Object> dataModels) throws UnsupportedEncodingException {
        this(new String(content,"UTF-8"),dataModels);
    }

    public SeedTemplateParser(String content,Map<String,Object> dataModels){
        parse(content,dataModels);
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("test","hehehe");
        new SeedTemplateParser("<div>{{test}}</div>",map);
    }

    private static final class Status{
        private static final int EMPTY = 0;
        private static final int FIND_OPEN_BUCKET = 1;
    }

    private void parse(String content, Map<String, Object> dataModels){
        String result = new Resolver(content,dataModels).parse();
        System.out.println(result);
    }

    class Resolver{

        private final CharBuffer content;
        private final Map<String, Object> dataModels;
        List<String> contentTable = new LinkedList<String>();
        StringBuilder stringBuilder = new StringBuilder();
        int index = 0;
        int status = 0;
        int seq = 0;
        int openIndex = 0;

        Resolver(String content,Map<String,Object> dataModels){
            this.content = CharBuffer.wrap(content);
            this.dataModels = dataModels;
        }

        public String parse(){
            while (true){
                if(content.length() == index)break;
                char c = content.charAt(index);
                stringBuilder.append(c);
                if(c == LEFT_BUCKET){
                    addToSeq();
                    stringBuilder.append(c);
                    if(!next())break;
                    c = content.charAt(index);
                    stringBuilder.append(c);
                    switch (c){
                        case LEFT_BUCKET:
                        {
                            status = Status.FIND_OPEN_BUCKET;
                            openIndex = index;
                            break;
                        }
                        case NUMBER_SIGN:{
                            break;
                        }
                    }
                }
                if(c == RIGHT_BUCKET){
                    if(!next())break;
                    c = content.charAt(index);
                    switch (c){
                        case RIGHT_BUCKET:
                        {
                            status = Status.EMPTY;
                            String params = paramName().toString();
                            Object value = dataModels.get(params);
                            contentTable.add(String.valueOf(value));
                            break;
                        }
                        case NUMBER_SIGN:{
                            break;
                        }
                        default:
                            stringBuilder.append(c);
                            break;
                    }
                }
                next();
            }
            contentTable.add(stringBuilder.toString());
            String html = mergeSeq();
            return html;
        }

        public CharSequence paramName(){
            CharSequence cs = stringBuilder.subSequence(2, stringBuilder.length() - 1);
            stringBuilder.delete(0,stringBuilder.length());
            return cs;
        }

        public boolean next(){
            if(index<content.length()){
                index++;
                return true;
            }
            return false;
        }

        public void addToSeq(){
            contentTable.add(seq, stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString());
            seq++;
            stringBuilder.delete(0,stringBuilder.length());
        }

        public void subSeq(){
            if(seq<contentTable.size())
            contentTable.remove(seq);
            seq--;
        }

        public String mergeSeq(){
            StringBuilder tempContent = new StringBuilder();
            for(String content:contentTable){
                tempContent.append(content);
            }
            String result = tempContent.toString();
            clear();
            return result;
        }

        public void clear(){
            stringBuilder.delete(0,stringBuilder.length());
            contentTable.clear();
            index = 0;
            status = 0;
            seq = 0;
            openIndex = 0;
        }
    }
}
