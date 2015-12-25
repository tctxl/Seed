package com.opdar.framework.db.parser;

import java.util.LinkedList;
import java.util.Map;

/**
 * Created by 俊帆 on 2015/12/25.
 */
public class Mapper {
    private LinkedList<Object> builder = new LinkedList<Object>();
    protected String namespace = null;
    protected String method = null;
    protected String prototype = null;
    protected static class Parameter{
        String key;
    }

    protected void add(Object object){
        builder.add(object);
    }

    public String create(Map<String, String> dataModels){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<builder.size();i++){
            Object o = builder.get(i);
            if(o instanceof String){
                stringBuilder.append(o);
            }else if(o instanceof Parameter){
                stringBuilder.append(dataModels.get(((Parameter) o).key));
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return "Mapper{" +
                "namespace='" + namespace + '\'' +
                ", method='" + method + '\'' +
                ", prototype='" + prototype + '\'' +
                '}';
    }
}
