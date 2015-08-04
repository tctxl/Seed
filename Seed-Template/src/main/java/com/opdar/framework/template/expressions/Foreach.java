package com.opdar.framework.template.expressions;

/**
 * Created by 俊帆 on 2015/8/4.
 */
public class Foreach {
    String condition;
    String valueName;
    String paramName;
    public Foreach(String condition) throws Exception {
        this.condition = condition;
        String[] conditions = condition.split(" in ");
        if(conditions.length == 2){
            valueName = conditions[0];
            paramName = conditions[1];
        }else{
            throw new Exception("Foreach Error");
        }
    }

    public String getCondition() {
        return condition;
    }

    public String getValueName() {
        return valueName;
    }

    public String getParamName() {
        return paramName;
    }
}
