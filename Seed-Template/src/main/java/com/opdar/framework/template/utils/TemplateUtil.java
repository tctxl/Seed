package com.opdar.framework.template.utils;

import com.opdar.framework.template.base.Expression;
import com.opdar.framework.template.base.Part;
import com.opdar.framework.template.base.Printf;
import com.opdar.framework.template.base.Variable;
import com.opdar.framework.template.expressions.Foreach;
import com.opdar.framework.template.expressions.Include;
import com.opdar.framework.template.parser.BaseTemplate;
import com.opdar.framework.template.parser.Parser;
import com.opdar.framework.template.parser.Resolver;
import com.opdar.framework.template.res.Loader;
import com.opdar.framework.utils.Utils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

/**
 * Created by 俊帆 on 2015/8/4.
 */
public class TemplateUtil {

    public static void part(Part part, Object object, StringWriter sw, String open, String close, Loader loader, HashMap<String,Object> vars,BaseTemplate template){
        if(part == null)return;
        ReversePolish polish = new ReversePolish();
        for (Object o : part.getParts()) {
            if(o instanceof Printf){
                Parser parser = new Parser(open, close);
                parser.get(((Printf) o).getValue());
                parser.parse(object, sw ,vars);
            }else if (o instanceof Variable) {
                vars.put(((Variable) o).getName(),polish.execute((String) ((Variable) o).getExp(object,vars)));
            }else if(o instanceof Expression){
                if(((Expression) o).getName().equals("for")){
                    try {
                        Foreach foreach = new Foreach(((Expression) o).getCondition());
                        Collection collection = (Collection) ValueUtil.get(object, foreach.getParamName());
                        if(collection == null) System.out.println(foreach.getParamName()+" is null");
                        for(Iterator it = collection.iterator();it.hasNext();){
                            Object o1 = it.next();
                            Map<String,Object> data = new HashMap<String,Object>();
                            data.put(foreach.getValueName(), o1);
                            HashMap map = new HashMap();
                            map.putAll(vars);
                            part(((Expression) o).getProgram(), data, sw, open, close, loader, map,template);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if(((Expression) o).getName().equals("include")){
                    Include include = new Include(((Expression) o).getCondition());
                    try {
                        String str = new String(Utils.is2byte(loader.load(template.getCurrentPath() + include.getValue())),loader.getCharsetName());
                        Resolver resolver = new Resolver(str,loader);
                        str = resolver.parse(object);
                        sw.write(str);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(((Expression) o).getName().equals("switch")){
                    String condition = ((Expression) o).getCondition();
                    Integer gotoInt = -1;
                    if(condition.matches("[0-9]")){
                        gotoInt = Integer.valueOf(condition);
                    }else{
                        Object oi = getParamValue(object, vars, ((Expression) o).getCondition());
                        gotoInt = Integer.valueOf(oi.toString());
                    }
                    HashMap map = new HashMap();
                    map.putAll(vars);
                    part(((Expression) o).getProgram(gotoInt), object, sw, open, close, loader, map,template);
                }else if(((Expression) o).getName().equals("if")){
                    HashMap map = new HashMap();
                    map.putAll(vars);

                    if(Boolean.parseBoolean(polish.execute(getExp(o,vars,((Expression) o).getCondition()).toString()))){
                        part(((Expression) o).getProgram(), object, sw, open, close, loader, map,template);
                    }
                }
            }
        }
    }

    public static Object getParamValue(Object object, HashMap<String, Object> vars,String paramName) {
        String param = paramName;
        Object oi = "-1";
        if(param.indexOf(".") > 0){
            String[] par = param.split("\\.");
            oi = ValueUtil.get(vars, param);
            if(oi == null)
                oi = object;
            for(String s:par){
                oi = ValueUtil.get(oi,s);
            }
            if(oi == null)oi = "";
        }else{
            oi = ValueUtil.get(vars,param);
            if(oi == null)
                oi = ValueUtil.get(object,param);
            if(oi == null)oi = "";
        }
        return oi;
    }

    public static Object getExp(Object o,HashMap<String, Object> vars,String exp) {
        boolean isVar = false;
        StringBuilder expBuilder = new StringBuilder();
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<exp.length();i++){
            char c = exp.charAt(i);
            if(!isVar && ((c >='a' && c<='z') || (c >='A' && c <='Z') || c == '_')){
                isVar = true;
                builder.append(c);
                continue;
            }else if(isVar && ((c >='a' && c<='z') || (c >='A' && c <='Z') || c == '_'|| (c >= '0' && c <= '9')) ){
                builder.append(c);
                continue;
            }else{
                if(builder.toString().equals("true")||builder.toString().equals("false")){
                    expBuilder.append(builder);
                    builder.delete(0,builder.length());
                }else{
                    isVar = executeVar(o, vars, expBuilder, builder);
                }
            }
            expBuilder.append(c);
        }

        if(builder.toString().equals("true")||builder.toString().equals("false")){
            expBuilder.append(builder);
            builder.delete(0,builder.length());
        }else{
            executeVar(o, vars, expBuilder, builder);
        }
        return expBuilder.toString();
    }

    private static boolean executeVar(Object o, HashMap<String, Object> vars, StringBuilder expBuilder, StringBuilder builder) {
        boolean isVar;
        isVar = false;
        if(builder.length() > 0){
            Object oi = TemplateUtil.getParamValue(o, vars, builder.toString());
            expBuilder.append(oi);
            builder.delete(0,builder.length());
        }
        return isVar;
    }
}
