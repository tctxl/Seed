package com.opdar.framework.template.utils;

import com.opdar.framework.template.base.Expression;
import com.opdar.framework.template.base.Part;
import com.opdar.framework.template.base.Printf;
import com.opdar.framework.template.base.Variable;
import com.opdar.framework.template.expressions.Foreach;
import com.opdar.framework.template.expressions.Include;
import com.opdar.framework.template.expressions.Switch;
import com.opdar.framework.template.parser.Parser;
import com.opdar.framework.template.res.Loader;
import com.opdar.framework.utils.Utils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 俊帆 on 2015/8/4.
 */
public class TemplateUtil {

    public static void part(Part part, Object object, StringWriter sw, String open, String close, Loader loader){
        for (Object o : part.getParts()) {
            if(o instanceof Printf){
                Parser parser = new Parser(open, close);
                parser.get(((Printf) o).getValue());
                parser.parse(object, sw);
            }else if (o instanceof Variable) {
                String type = ((Variable) o).getType();
            }else if(o instanceof Expression){
                if(((Expression) o).getName().equals("for")){
                    try {
                        Foreach foreach = new Foreach(((Expression) o).getCondition());
                        Collection collection = (Collection) ValueUtil.get(object, foreach.getParamName());
                        for(Iterator it = collection.iterator();it.hasNext();){
                            Object o1 = it.next();
                            Map<String,Object> data = new HashMap<String,Object>();
                            data.put(foreach.getValueName(),o1);
                            part(((Expression) o).getProgram(), data, sw, open, close, loader);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if(((Expression) o).getName().equals("include")){
                    Include include = new Include(((Expression) o).getCondition());
                    try {
                        sw.write(new String(Utils.is2byte(loader.load(loader.getCurrentPath()+include.getValue()))));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(((Expression) o).getName().equals("switch")){
                    Switch switchs = new Switch();

                }
            }
        }
    }

}
